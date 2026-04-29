package bank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ============================================================================
 *  Account.java                                               [PERSON A OWNS]
 * ============================================================================
 *
 *  WHAT THIS CLASS IS:
 *      The base class for all three account types. Anything that's true of
 *      EVERY account goes here (id, balance). Anything specific (interest
 *      rate, overdraft limit) goes in the subclass.
 *
 *  WHY ABSTRACT:
 *      So no one can do `new Account(...)` -- they must pick a real type
 *      (CheckingAccount, SavingsAccount, LoanAccount).
 *
 *  IMPORTANT FOR PERSON C:
 *      Gson cannot guess which subclass to deserialize from a JSON object.
 *      We solve this with the `type` field below. When loading JSON, Person C
 *      will read `type`, then build the right subclass manually.
 *
 *  IMPORTANT FOR EVERYONE:
 *      Money is `BigDecimal`. Never `double`.
 * ============================================================================
 */
public abstract class Account {

    // ------------------------------------------------------------------
    // Fields shared by every account type.
    // ------------------------------------------------------------------

    /** "CHECKING", "SAVINGS", or "LOAN". Used by Person C when deserializing. */
    protected String type;

    /** Account number, e.g. "CHK-1001". */
    protected String id;

    /** Money in the account. Negative on a Loan = amount still owed. */
    protected BigDecimal balance = BigDecimal.ZERO;

    // ------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------

    /** No-arg constructor for Gson. Don't delete. */
    protected Account() { }

    protected Account(String type, String id, BigDecimal initialBalance) {
        // TODO: assign type, id.
        // TODO: assign balance, but normalized to 2 decimal places.
        //       use: initialBalance.setScale(2, RoundingMode.HALF_UP)
    }

    // ------------------------------------------------------------------
    // Balance changes -- ONLY way Person B's code should change a balance.
    // ------------------------------------------------------------------

    /**
     * Add to the balance. Returns the new balance.
     *
     * EXAMPLE PATTERN (this one's actually pretty close to right):
     *
     *     this.balance = this.balance.add(amount).setScale(2, RoundingMode.HALF_UP);
     *     return this.balance;
     */
    public BigDecimal credit(BigDecimal amount) {
        // TODO: add `amount` to balance, round to 2dp, return new balance.
        return this.balance;
    }

    /**
     * Subtract from balance. Subclass decides whether this is allowed.
     * Default behavior: just subtract. CheckingAccount overrides to enforce
     * overdraft rules. SavingsAccount overrides to block negative balance.
     */
    public BigDecimal debit(BigDecimal amount) {
        // TODO: subtract `amount` from balance, round to 2dp, return new balance.
        return this.balance;
    }

    // ------------------------------------------------------------------
    // Subclasses must implement this so the InterestCalculator knows what
    // monthly rate to use. Checking can return ZERO.
    // ------------------------------------------------------------------

    public abstract BigDecimal getMonthlyInterestRate();

    // ------------------------------------------------------------------
    // Plain getters
    // ------------------------------------------------------------------

    public String getId() { return id; }
    public String getType() { return type; }
    public BigDecimal getBalance() { return balance; }
}
