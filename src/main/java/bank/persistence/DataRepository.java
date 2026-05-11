package bank.persistence;

import bank.model.Bank;
import bank.model.Customer;

import java.io.IOException;
import java.util.List;

/**
 * ============================================================================
 *  DataRepository.java
 *  Owner: John (smartdude879@gmail.com)
 * ============================================================================
 *
 *  ROLE:
 *      The single "front door" to the bank's persisted state. Everyone else
 *      (AuthService, TransactionService, InterestCalculator) talks to this
 *      class — never to JsonStore directly.
 *
 *  USAGE PATTERN:
 *      DataRepository repo = new DataRepository();
 *      repo.load();                  // call once at startup
 *      ...
 *      repo.findCustomer("1001");    // used by AuthService
 *      repo.save();                  // call after every change
 *
 *  WHY THIS WRAPPER EXISTS:
 *      If we ever swap JSON for a database, only THIS class changes. The rest
 *      of the app keeps working unchanged.
 * ============================================================================
 */
public class DataRepository {

    private final JsonStore store;
    private Bank bank;

    // ── Construction ──────────────────────────────────────────────────────────

    public DataRepository() {
        this.store = new JsonStore();
    }

    /** Package-private constructor for testing with a stub JsonStore. */
    DataRepository(JsonStore store) {
        this.store = store;
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Loads bank state from disk. Call once at program startup.
     * If the file is missing or corrupted the bank is initialised empty.
     */
    public void load() {
        this.bank = store.load();
    }

    /**
     * Persists the current in-memory bank state to disk.
     * Errors are logged — the program never crashes from a save failure.
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
     *
     * @param customerId the customer's unique ID
     * @return the matching customer, or {@code null} if no such customer exists
     */
    public Customer findCustomer(String customerId) {
        assertLoaded();
        for (Customer c : bank.getCustomers()) {
            if (c.getId().equals(customerId)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Returns every customer in the bank.
     * Used by InterestCalculator to loop over everyone.
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
     * Returns the raw Bank object — avoid using outside of tests.
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
