package bank.persistence;

import bank.model.Bank;
import bank.model.Customer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Person C — DataRepository
 *
 * The single "front door" to the bank's persisted state.
 * Holds the Bank in memory between operations and delegates
 * all disk I/O to JsonStore.
 *
 * Usage pattern:
 *   DataRepository repo = new DataRepository();
 *   repo.load();               // call once at startup
 *   ...
 *   repo.findCustomer("1001"); // used by AuthService
 *   repo.save();               // call after every mutating operation
 */
public class DataRepository {

    private final JsonStore store;
    private Bank bank;

    // ── Construction ──────────────────────────────────────────────────────────

    public DataRepository() {
        this.store = new JsonStore();
    }

    /** Package-private constructor for testing with a mock/stub JsonStore. */
    DataRepository(JsonStore store) {
        this.store = store;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Loads bank state from disk.
     * Call once at program startup — before any other method.
     * If the file doesn't exist or is corrupted, the bank is initialised empty.
     */
    public void load() {
        this.bank = store.load();
    }

    /**
     * Persists the current in-memory bank state to disk.
     *
     * Called by:
     *  - Person B (TransactionService) after every successful transaction.
     *  - Person A (AuthService) after PIN attempt counter changes (optional).
     *  - Main.java on shutdown as a safety net.
     */
    public void save() {
        if (bank == null) {
            System.err.println("[DataRepository] save() called before load() — nothing to save.");
            return;
        }
        try {
            store.save(bank);
        } catch (IOException e) {
            System.err.println("[DataRepository] Failed to save bank.json: " + e.getMessage());
        }
    }

    // ── Customer queries ──────────────────────────────────────────────────────

    /**
     * Finds a customer by their ID.
     * Returns an Optional so callers handle the missing-customer case explicitly.
     *
     * Used by: AuthService (Person A)
     */
    public Optional<Customer> findCustomer(String customerId) {
        assertLoaded();
        return bank.getCustomers().stream()
                .filter(c -> c.getId().equals(customerId))
                .findFirst();
    }

    /**
     * Returns all customers.
     * Used by: InterestCalculator (Person B) to loop over everyone.
     */
    public List<Customer> allCustomers() {
        assertLoaded();
        return bank.getCustomers();
    }

    /**
     * Adds a new customer to the bank.
     * Throws if a customer with that ID already exists.
     */
    public void addCustomer(Customer customer) {
        assertLoaded();
        boolean exists = bank.getCustomers().stream()
                .anyMatch(c -> c.getId().equals(customer.getId()));
        if (exists) {
            throw new IllegalArgumentException(
                    "Customer with ID " + customer.getId() + " already exists.");
        }
        bank.getCustomers().add(customer);
    }

    // ── Low-level access ──────────────────────────────────────────────────────

    /**
     * Returns the raw Bank object.
     * Avoid using this outside of testing — prefer the typed query methods above.
     */
    public Bank getBank() {
        assertLoaded();
        return bank;
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private void assertLoaded() {
        if (bank == null) {
            throw new IllegalStateException(
                    "DataRepository.load() must be called before any data access.");
        }
    }
}
