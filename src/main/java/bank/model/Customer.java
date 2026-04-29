package bank.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 *  Customer.java                                              [PERSON A OWNS]
 * ============================================================================
 *
 *  WHAT THIS CLASS REPRESENTS:
 *      One person who has accounts at the bank. They have an ID, a name, a
 *      hashed PIN, and a list of accounts (Checking, Savings, Loan).
 *
 *  WHO USES IT:
 *      - AuthService           -> checks PIN against this customer
 *      - TransactionService    -> finds an account by ID inside this customer
 *      - StatementGenerator    -> reads name and transactions to print
 *      - JsonStore             -> serializes/deserializes this object
 *
 *  WHAT YOU NEED TO DO:
 *      Fill in the TODOs. The fields are mostly here. The methods need bodies.
 *
 *  GOTCHAS:
 *      1. Gson needs a no-arg constructor. The protected one below is for that.
 *      2. Don't store PINs as plaintext. Always hash before storing.
 *      3. The list of transactions belongs to the Customer (not Account) so
 *         it's easy for Person C to load all of a customer's history at once.
 * ============================================================================
 */
public class Customer {

    // ------------------------------------------------------------------
    // Fields. Gson reads these directly when loading from JSON, so the
    // names here have to match what's in bank.json (talk to Person C).
    // ------------------------------------------------------------------

    private String id;                              // e.g. "1001"
    private String name;                            // e.g. "Jane Doe"
    private String pinHash;                         // SHA-256 hex of the PIN
    private int failedLoginAttempts;
    private boolean locked;
    private List<Account> accounts = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();

    // ------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------

    /** No-arg constructor required by Gson. Don't delete. */
    protected Customer() { }

    public Customer(String id, String name, String rawPin) {
        // TODO: assign id and name.
        // TODO: hash the rawPin and store it in pinHash (use hashPin() below).
        // TODO: leave failedLoginAttempts at 0 and locked at false.
    }

    // ------------------------------------------------------------------
    // PIN handling
    // ------------------------------------------------------------------

    /**
     * Returns true if the given PIN matches the stored hash.
     *
     * EXAMPLE PATTERN (similar shape -- DON'T copy verbatim, the real method
     * also has to handle the locked flag and the failed attempt counter):
     *
     *     public boolean checkSomething(String input) {
     *         String hashed = hashPin(input);
     *         return hashed.equals(this.pinHash);
     *     }
     */
    public boolean verifyPin(String rawPin) {
        // TODO:
        //  1. If the account is locked, return false immediately.
        //  2. Hash the rawPin.
        //  3. Compare to stored pinHash.
        //  4. If it matches: reset failedLoginAttempts to 0 and return true.
        //  5. If it doesn't: increment failedLoginAttempts. If >= 3, set locked = true.
        //     Return false.
        return false;
    }

    /**
     * Standard SHA-256 hex hash. Use this everywhere you touch a PIN.
     */
    public static String hashPin(String rawPin) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(rawPin.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("PIN hashing failed", e);
        }
    }

    // ------------------------------------------------------------------
    // Account helpers
    // ------------------------------------------------------------------

    /**
     * Find one of this customer's accounts by ID. Return null if not found.
     *
     * EXAMPLE PATTERN:
     *
     *     for (Thing t : list) {
     *         if (t.getId().equals(id)) return t;
     *     }
     *     return null;
     */
    public Account findAccount(String accountId) {
        // TODO: loop through accounts, return the one whose ID matches, else null.
        return null;
    }

    public void addAccount(Account a) {
        // TODO: add to the accounts list.
    }

    public void recordTransaction(Transaction t) {
        // TODO: add to the transactions list.
        //       Person B's TransactionService will call this every time money moves.
    }

    // ------------------------------------------------------------------
    // Plain getters (Gson + everyone else needs these)
    // ------------------------------------------------------------------

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isLocked() { return locked; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public List<Account> getAccounts() { return accounts; }
    public List<Transaction> getTransactions() { return transactions; }

    // Used by AuthService when an admin unlocks a customer:
    public void unlock() {
        // TODO: set locked = false and failedLoginAttempts = 0.
    }
}
