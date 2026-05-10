package bank.persistence;

import bank.model.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Person C — JsonStore
 *
 * The ONLY class allowed to touch bank.json directly.
 * Uses Gson to serialize / deserialize the entire Bank state.
 *
 * Handles:
 *  - First run (file doesn't exist) → returns empty Bank
 *  - Corrupted JSON → backs up the bad file, returns empty Bank
 *  - BigDecimal precision via a custom TypeAdapter
 *  - Account polymorphism via a manual "type" field switch
 */
public class JsonStore {

    // ── Configuration ────────────────────────────────────────────────────────
    private static final String DATA_DIR  = "data";
    private static final String DATA_FILE = DATA_DIR + "/bank.json";
    private static final String BACKUP_SUFFIX = ".bak";

    // ── Gson instance (shared, thread-safe once built) ────────────────────────
    private final Gson gson;

    public JsonStore() {
        gson = buildGson();
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Saves the entire Bank to disk.
     * Writes to a temp file first, then atomically replaces the real file
     * so a crash mid-write never corrupts the existing data.
     */
    public void save(Bank bank) throws IOException {
        // Ensure the data directory exists
        Files.createDirectories(Paths.get(DATA_DIR));

        // Serialize to a temp file
        Path tempPath = Paths.get(DATA_FILE + ".tmp");
        try (Writer writer = Files.newBufferedWriter(tempPath)) {
            gson.toJson(bank, writer);
        }

        // Atomic replace
        Files.move(tempPath, Paths.get(DATA_FILE),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE);
    }

    /**
     * Loads the Bank from disk.
     * Returns an empty Bank if the file doesn't exist.
     * Returns an empty Bank (after backing up the bad file) if JSON is corrupted.
     */
    public Bank load() {
        Path filePath = Paths.get(DATA_FILE);

        if (!Files.exists(filePath)) {
            System.out.println("[JsonStore] No bank.json found — starting with an empty bank.");
            return new Bank();
        }

        try (Reader reader = Files.newBufferedReader(filePath)) {
            Bank bank = gson.fromJson(reader, Bank.class);
            if (bank == null) {
                System.out.println("[JsonStore] bank.json was empty — starting fresh.");
                return new Bank();
            }
            System.out.println("[JsonStore] Loaded bank.json successfully.");
            return bank;

        } catch (JsonSyntaxException | JsonIOException e) {
            // Corrupted JSON — back up and return empty bank
            backupCorruptedFile(filePath);
            System.err.println("[JsonStore] WARNING: bank.json is corrupted. A backup was created. Starting fresh.");
            return new Bank();

        } catch (IOException e) {
            System.err.println("[JsonStore] Could not read bank.json: " + e.getMessage());
            return new Bank();
        }
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private void backupCorruptedFile(Path original) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path backup = Paths.get(DATA_FILE + "." + timestamp + BACKUP_SUFFIX);
        try {
            Files.copy(original, backup, StandardCopyOption.REPLACE_EXISTING);
            System.err.println("[JsonStore] Backup saved to: " + backup);
        } catch (IOException ex) {
            System.err.println("[JsonStore] Could not create backup: " + ex.getMessage());
        }
    }

    /**
     * Builds a Gson instance configured for the banking model:
     *  - BigDecimal adapter that preserves precision
     *  - LocalDateTime adapter (ISO format)
     *  - Account polymorphism via a custom deserializer
     *  - Pretty-printing for readability
     */
    private Gson buildGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(BigDecimal.class, bigDecimalAdapter())
                .registerTypeAdapter(LocalDateTime.class, localDateTimeAdapter())
                .registerTypeAdapter(Account.class, accountDeserializer())
                .create();
    }

    // ── Custom TypeAdapters ───────────────────────────────────────────────────

    /** Preserves exact decimal strings (e.g. "500.00") so no floating-point drift. */
    private TypeAdapter<BigDecimal> bigDecimalAdapter() {
        return new TypeAdapter<BigDecimal>() {
            @Override
            public void write(JsonWriter out, BigDecimal value) throws IOException {
                if (value == null) { out.nullValue(); return; }
                out.value(value.toPlainString());
            }
            @Override
            public BigDecimal read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) { in.nextNull(); return null; }
                return new BigDecimal(in.nextString());
            }
        };
    }

    /** Stores LocalDateTime as an ISO-8601 string. */
    private TypeAdapter<LocalDateTime> localDateTimeAdapter() {
        return new TypeAdapter<LocalDateTime>() {
            @Override
            public void write(JsonWriter out, LocalDateTime value) throws IOException {
                if (value == null) { out.nullValue(); return; }
                out.value(value.toString());
            }
            @Override
            public LocalDateTime read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) { in.nextNull(); return null; }
                return LocalDateTime.parse(in.nextString());
            }
        };
    }

    /**
     * Handles Account polymorphism by switching on the "type" JSON field.
     * Persons A agreed to include a "type" field on every account subclass.
     */
    private JsonDeserializer<Account> accountDeserializer() {
        return (json, typeOfT, context) -> {
            JsonObject obj = json.getAsJsonObject();
            String type = obj.has("type") ? obj.get("type").getAsString() : "";

            switch (type.toUpperCase()) {
                case "CHECKING":
                    return context.deserialize(obj, CheckingAccount.class);
                case "SAVINGS":
                    return context.deserialize(obj, SavingsAccount.class);
                case "LOAN":
                    return context.deserialize(obj, LoanAccount.class);
                default:
                    throw new JsonParseException("Unknown account type: " + type);
            }
        };
    }

    // ── Inner class: JsonReader / JsonWriter (Gson internal imports) ──────────
    // Gson's JsonReader / JsonWriter are imported via com.google.gson.stream.*
    // The imports at the top cover them through the TypeAdapter API.
}
