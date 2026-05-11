package bank.persistence;

import bank.model.Account;
import bank.model.Bank;
import bank.model.CheckingAccount;
import bank.model.LoanAccount;
import bank.model.SavingsAccount;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.lang.reflect.Type;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================================
 *  JsonStore.java
 *  Owner: John (smartdude879@gmail.com)
 * ============================================================================
 *
 *  ROLE:
 *      The ONLY class allowed to touch bank.json directly. Reads and writes
 *      the entire Bank object via Gson.
 *
 *  HANDLES:
 *      - First run (file doesn't exist)  → returns empty Bank
 *      - Corrupted JSON                  → backs up the bad file, returns empty Bank
 *      - BigDecimal precision            → custom TypeAdapter
 *      - LocalDateTime                   → ISO-8601 string adapter
 *      - Account polymorphism            → manual "type" field switch
 *      - Atomic writes                   → temp file + atomic rename
 * ============================================================================
 */
public class JsonStore {

    // ── Configuration ────────────────────────────────────────────────────────
    private static final String DATA_DIR  = "data";
    private static final String DATA_FILE = DATA_DIR + "/bank.json";
    private static final String BACKUP_SUFFIX = ".bak";

    private final Gson gson;

    public JsonStore() {
        this.gson = buildGson();
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Saves the entire Bank to disk.
     * Writes to a temp file first then atomically replaces the real file
     * so a crash mid-write never corrupts the existing data.
     *
     * @param bank the bank state to persist
     * @throws IOException if the file cannot be written
     */
    public void save(Bank bank) throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));

        Path tempPath = Paths.get(DATA_FILE + ".tmp");
        try (Writer writer = Files.newBufferedWriter(tempPath)) {
            gson.toJson(bank, writer);
        }

        try {
            Files.move(tempPath, Paths.get(DATA_FILE),
                    StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException atomicFailed) {
            // Some Windows filesystems don't support ATOMIC_MOVE — fall back
            Files.move(tempPath, Paths.get(DATA_FILE),
                    StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Loads the Bank from disk.
     * Returns an empty Bank if the file doesn't exist.
     * Backs up corrupted JSON and returns an empty Bank.
     *
     * @return the loaded bank, or a fresh empty Bank
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
            backupCorruptedFile(filePath);
            System.err.println("[JsonStore] WARNING: bank.json is corrupted. Backup created. Starting fresh.");
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
     * Handles Account polymorphism. Gson cannot guess which subclass of
     * Account to build, so we read the "type" field by hand and call back
     * into Gson with the right concrete class.
     */
    private JsonDeserializer<Account> accountDeserializer() {
        return new JsonDeserializer<Account>() {
            @Override
            public Account deserialize(JsonElement json, Type typeOfT,
                                       JsonDeserializationContext context) {
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
            }
        };
    }
}
