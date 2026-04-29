package bank.persistence;

import bank.model.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 *  JsonStore.java                                             [PERSON C OWNS]
 * ============================================================================
 *
 *  WHAT THIS DOES:
 *      The ONLY class that actually reads and writes bank.json. Two methods:
 *
 *          List<Customer> load()                       <-- read JSON file
 *          void save(List<Customer> customers)         <-- write JSON file
 *
 *      Everyone else uses DataRepository, which uses this.
 *
 *  THE BIG PROBLEM YOU NEED TO SOLVE:
 *      `Account` is abstract with three subclasses. Plain Gson doesn't know
 *      which subclass to build when it sees `{"type":"CHECKING", ...}`. You
 *      have two options:
 *
 *      OPTION 1 (recommended): Custom JsonDeserializer / JsonSerializer.
 *          Read the "type" field manually, then build the matching subclass.
 *          More code but transparent.
 *
 *      OPTION 2: Gson's RuntimeTypeAdapterFactory (in gson-extras).
 *          Less code but adds a dependency.
 *
 *      The example below uses Option 1.
 *
 *  EXAMPLE PATTERN (rough sketch -- don't copy verbatim):
 *
 *      Gson gson = new GsonBuilder()
 *          .registerTypeAdapter(Account.class, new AccountAdapter())
 *          .registerTypeAdapter(BigDecimal.class, new BigDecimalAdapter())
 *          .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
 *          .setPrettyPrinting()
 *          .create();
 *
 *      // load:
 *      try (Reader r = Files.newBufferedReader(path)) {
 *          Type listType = new TypeToken<List<Customer>>(){}.getType();
 *          return gson.fromJson(r, listType);
 *      }
 *
 *      // save:
 *      try (Writer w = Files.newBufferedWriter(path)) {
 *          gson.toJson(customers, w);
 *      }
 *
 *  THE Account ADAPTER (sketch):
 *
 *      class AccountAdapter implements JsonDeserializer<Account>, JsonSerializer<Account> {
 *          public Account deserialize(JsonElement el, Type t, JsonDeserializationContext ctx) {
 *              JsonObject o = el.getAsJsonObject();
 *              String type = o.get("type").getAsString();
 *              switch (type) {
 *                  case "CHECKING": return ctx.deserialize(o, CheckingAccount.class);
 *                  case "SAVINGS":  return ctx.deserialize(o, SavingsAccount.class);
 *                  case "LOAN":     return ctx.deserialize(o, LoanAccount.class);
 *                  default: throw new JsonParseException("unknown type: " + type);
 *              }
 *          }
 *          public JsonElement serialize(Account src, Type t, JsonSerializationContext ctx) {
 *              return ctx.serialize(src, src.getClass());   // writes correct subclass
 *          }
 *      }
 *
 *  EDGE CASES TO HANDLE:
 *      - File doesn't exist yet (first run) -> return empty list, don't crash.
 *      - File is corrupted JSON -> back it up to bank.json.bak, return empty,
 *        log a warning.
 *      - Atomic save: write to bank.json.tmp first, then rename. Otherwise a
 *        crash mid-write corrupts the data.
 * ============================================================================
 */
public class JsonStore {

    private final String filePath;

    public JsonStore(String filePath) {
        // TODO: assign the filePath ("data/bank.json").
        this.filePath = filePath;
    }

    /**
     * Read the JSON file and return the list of customers.
     * Returns an empty list if the file doesn't exist.
     */
    public List<Customer> load() {
        // TODO:
        //  1. If file doesn't exist, return new ArrayList<>().
        //  2. Build the configured Gson (with the AccountAdapter).
        //  3. Read the file, deserialize into List<Customer>.
        //  4. If JSON is corrupted, back it up to <path>.bak, return empty list.
        //  5. Return the list.
        return new ArrayList<>();
    }

    /**
     * Write the list of customers to JSON. Atomic: write to .tmp then rename.
     */
    public void save(List<Customer> customers) {
        // TODO:
        //  1. Build the configured Gson.
        //  2. Write JSON to "<path>.tmp".
        //  3. Atomically rename ".tmp" -> filePath
        //     (Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE,
        //                 StandardCopyOption.REPLACE_EXISTING)).
    }
}
