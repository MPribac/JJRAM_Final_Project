package bank.auth;

import bank.model.Customer;
import bank.persistence.DataRepository;

/**
 * ============================================================================
 *  AuthService.java
 *  Owner   : Abdul Rahman Fornah (afornah1@umbc.edu)
 *  Helped by: Jordon Tang        (jtang10@umbc.edu)
 * ============================================================================
 *
 *  ROLE:
 *      Acts as the authentication gateway for the banking application. It 
 *      handles user login, session management (tracking the current user), 
 *      and account unlocking services.
 *
 *  LOGIC:
 *      - Uses DataRepository to fetch customer information.
 *      - Delegates PIN verification to the Customer model.
 *      - Persists state changes (like failed attempts) back to the repository.
 * ============================================================================
 */
public class AuthService {

    private final DataRepository repository;
    private Customer currentUser;

    /**
     * Initializes the AuthService with a reference to the data repository.
     * 
     * @param repository The repository used for customer data access.
     */
    public AuthService(DataRepository repository) {
        this.repository = repository;
    }

    /**
     * Authenticates a user based on their ID and PIN.
     *
     * @param customerId The ID of the customer attempting to log in.
     * @param pin The plaintext PIN provided.
     * @return The authenticated Customer object on success; null if login fails.
     */
    public Customer login(String customerId, String pin) {
        Customer customer = repository.findCustomer(customerId);
        
        if (customer == null) {
            return null; // Customer not found
        }
        
        if (customer.isLocked()) {
            return null; // Account is locked due to too many failed attempts
        }
        
        if (customer.verifyPin(pin)) {
            this.currentUser = customer;
            repository.save(); // Persist the reset of failed attempts
            return customer;
        }
        
        // PIN was incorrect; Customer.verifyPin already updated failed attempts/lock status.
        repository.save(); // Persist the incremented failed attempt counter or lock status
        return null;
    }

    /**
     * Logs out the current user by clearing the session.
     */
    public void logout() {
        this.currentUser = null;
    }

    public Customer getCurrentUser() {
        return currentUser;
    }

    /**
     * Administrative function to unlock a locked customer account.
     *
     * @param customerId The ID of the customer to unlock.
     * @return true if the customer was found and unlocked; false otherwise.
     */
    public boolean unlock(String customerId) {
        Customer customer = repository.findCustomer(customerId);
        if (customer == null) {
            return false;
        }
        
        customer.unlock();
        repository.save();
        return true;
    }
}
