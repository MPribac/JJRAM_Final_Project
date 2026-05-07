package bank.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 *  Customer.java
 *  Owner: Abdul Rahman Fornah (afornah1@umbc.edu)
 * ============================================================================
 *
 *  REPRESENTS:
 *      A bank customer who owns multiple accounts. This class manages the 
 *      customer's profile, authentication security (hashed PINs, lockouts), 
 *      and provides access to their account and transaction history.
 *
 *  SECURITY:
 *      - PINs are never stored in plaintext. They are hashed using SHA-256.
 *      - Implements a 3-strike lockout policy for failed login attempts.
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

    /**
     * Initializes a new Customer.
     * 
     * @param id The unique customer ID.
     * @param name The customer's full name.
     * @param rawPin The initial PIN (will be hashed before storage).
     */
    public Customer(String id, String name, String rawPin) {
        this.id = id;
        this.name = name;
        this.pinHash = hashPin(rawPin);
        this.failedLoginAttempts = 0;
        this.locked = false;
    }

    // ------------------------------------------------------------------
    // PIN handling
    // ------------------------------------------------------------------

    /**
     * Authenticates the customer by comparing the provided PIN with the stored hash.
     * Also manages the failed attempt counter and account locking.
     *
     * @param rawPin The plaintext PIN provided by the user.
     * @return true if the PIN is correct and the account is not locked; false otherwise.
     */
    public boolean verifyPin(String rawPin) {
        if (this.locked) {
            return false;
        }

        String hashedInput = hashPin(rawPin);
        if (hashedInput.equals(this.pinHash)) {
            this.failedLoginAttempts = 0;
            return true;
        } else {
            this.failedLoginAttempts++;
            if (this.failedLoginAttempts >= 3) {
                this.locked = true;
            }
            return false;
        }
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
     * Retrieves an account owned by this customer by its ID.
     *
     * @param accountId The ID of the account to find.
     * @return The Account object if found; null otherwise.
     */
    public Account findAccount(String accountId) {
        for (Account account : accounts) {
            if (account.getId().equals(accountId)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Adds a new account to the customer's profile.
     * 
     * @param a The account to add.
     */
    public void addAccount(Account a) {
        this.accounts.add(a);
    }

    /**
     * Records a transaction in the customer's history.
     * 
     * @param t The transaction to record.
     */
    public void recordTransaction(Transaction t) {
        this.transactions.add(t);
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

    /**
     * Unlocks the customer account and resets the failed login attempt counter.
     */
    public void unlock() {
        this.locked = false;
        this.failedLoginAttempts = 0;
    }
}
