package bank.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 *  Bank.java
 *  Owner: John (smartdude879@gmail.com)
 * ============================================================================
 *
 *  REPRESENTS:
 *      The top-level "bank" container — really just a list of all customers.
 *      This is the object that gets serialized to and from bank.json.
 *
 *  WHY A WRAPPER CLASS:
 *      Gson works best with a single root object. By wrapping the customer list
 *      in a Bank, we can later add fields (next account number, bank name, etc.)
 *      without breaking the JSON format.
 * ============================================================================
 */
public class Bank {

    /** Every customer the bank knows about. */
    private List<Customer> customers = new ArrayList<>();

    /** No-arg constructor required by Gson. */
    public Bank() { }

    /**
     * Returns the live list of customers — mutations are persisted on save().
     */
    public List<Customer> getCustomers() {
        if (customers == null) {
            customers = new ArrayList<>();
        }
        return customers;
    }
}
