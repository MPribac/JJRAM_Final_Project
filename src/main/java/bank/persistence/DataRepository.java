package bank.persistence;

import bank.model.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 *  DataRepository.java                                        [PERSON C OWNS]
 * ============================================================================
 *
 *  WHAT THIS DOES:
 *      The "front door" for data. Nobody else talks to JsonStore directly --
 *      they go through here. Holds the list of customers in memory; saves to
 *      disk when asked.
 *
 *  WHO USES IT:
 *      - AuthService            -> findCustomer
 *      - TransactionService     -> save() after every transaction
 *      - InterestCalculator     -> allCustomers() to loop, then save()
 *      - Main                   -> load() at startup, save() at shutdown
 *
 *  WHY THIS EXISTS:
 *      If we ever swap JSON for SQLite, only THIS class changes. The rest of
 *      the app keeps working.
 *
 *  EXAMPLE PATTERN:
 *
 *      public Customer findCustomer(String id) {
 *          for (Customer c : customers) {
 *              if (c.getId().equals(id)) return c;
 *          }
 *          return null;
 *      }
 * ============================================================================
 */
public class DataRepository {

    private final JsonStore store;
    private List<Customer> customers = new ArrayList<>();

    public DataRepository(String filePath) {
        // TODO: this.store = new JsonStore(filePath);
        this.store = new JsonStore(filePath);
    }

    /** Load everything from disk. Call once at startup. */
    public void load() {
        // TODO: this.customers = store.load();
    }

    /** Persist current state to disk. Call after every change. */
    public void save() {
        // TODO: store.save(customers);
    }

    public Customer findCustomer(String id) {
        // TODO: loop through customers, return the match or null.
        return null;
    }

    public List<Customer> allCustomers() {
        return customers;
    }

    /** Used at setup time / by tests to seed data. */
    public void addCustomer(Customer c) {
        // TODO: customers.add(c);
    }
}
