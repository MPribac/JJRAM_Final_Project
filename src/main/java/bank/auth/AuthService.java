package bank.auth;

import bank.model.Customer;
import bank.persistence.DataRepository;

/**
 * ============================================================================
 *  AuthService.java                                           [PERSON A OWNS]
 * ============================================================================
 *
 *  WHAT THIS CLASS DOES:
 *      The login gate. Takes a customer ID + PIN, asks the repository to find
 *      that customer, asks the Customer if the PIN matches, and returns the
 *      Customer if it does (or null if it doesn't).
 *
 *      Also tracks "current user" for the session and handles logout.
 *
 *  WHO USES IT:
 *      Main.java's login loop.
 *
 *  RULES:
 *      - 3 wrong PINs in a row -> the Customer object's `locked` flag goes true.
 *        Person A handles that inside Customer.verifyPin().
 *      - Locked customers can't log in until an admin unlocks them.
 *
 *  EXAMPLE PATTERN for the login() body:
 *
 *      public Customer login(String id, String pin) {
 *          Customer c = repository.findCustomer(id);
 *          if (c == null) return null;        // unknown ID
 *          if (c.isLocked()) return null;     // already locked
 *          if (c.verifyPin(pin)) {
 *              this.currentUser = c;
 *              repository.save();             // save updated attempt counter
 *              return c;
 *          }
 *          repository.save();                 // save the failed-attempt change
 *          return null;
 *      }
 * ============================================================================
 */
public class AuthService {

    private final DataRepository repository;
    private Customer currentUser;

    public AuthService(DataRepository repository) {
        // TODO: assign the repository.
        this.repository = repository;
    }

    /**
     * Try to log in. Return the Customer on success, null on failure.
     * Don't print anything from inside here -- Main.java does the I/O.
     */
    public Customer login(String customerId, String pin) {
        // TODO: implement the example pattern above.
        return null;
    }

    /** Clear the current user. Always succeeds. */
    public void logout() {
        // TODO: set currentUser to null.
    }

    public Customer getCurrentUser() {
        return currentUser;
    }

    /**
     * Admin-only: unlock a customer who got locked out.
     * (You don't have to wire this to a menu option in v1, but write it.)
     */
    public boolean unlock(String customerId) {
        // TODO:
        //  1. Find customer in repo.
        //  2. If not found, return false.
        //  3. Call customer.unlock(), save, return true.
        return false;
    }
}
