package bank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ============================================================================
 *  Account.java
 *  Owner: Abdul Rahman Fornah (afornah1@umbc.edu)
 * ============================================================================
 *
 *  ABSTRACT BASE CLASS:
 *      Represents the core properties and behaviors common to all bank accounts
 *      (ID, balance, and transaction logic). Specific account types like 
 *      Checking, Savings, and Loan extend this class to implement their unique
 *      rules (interest rates, overdraft limits, etc.).
 *
 *  KEY DESIGN DECISIONS:
 *      1. ABSTRACT: Prevents direct instantiation of a generic "Account".
 *         Users must use specific types (CheckingAccount, SavingsAccount, LoanAccount).
 *      2. TYPE FIELD: Used for JSON deserialization (handled by Person C). 
 *         Since Gson cannot automatically determine which subclass to create, 
 *         this field identifies the account type.
 *      3. BIGDECIMAL: Used for all monetary values to ensure precision. 
 *         Standard floating-point types (double/float) are avoided due to 
 *         rounding errors in financial calculations.
 * ============================================================================
 */
public abstract class Account {

    // ------------------------------------------------------------------
    // Core Fields
    // ------------------------------------------------------------------

    /** The type of account ("CHECKING", "SAVINGS", "LOAN"). Essential for JSON parsing. */
    protected String type;

    /** Unique identifier for the account, e.g., "CHK-1001". */
    protected String id;

    /** 
     * Current account balance. 
     * For LoanAccounts, a negative balance indicates the amount owed. 
     */
    protected BigDecimal balance = BigDecimal.ZERO;

    // ------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------

    /** 
     * No-argument constructor required for Gson deserialization.
     */
    protected Account() { }

    /**
     * Protected constructor for use by subclasses.
     * 
     * @param type The account type identifier.
     * @param id The unique account ID.
     * @param initialBalance Starting balance, which will be normalized to 2 decimal places.
     */
    protected Account(String type, String id, BigDecimal initialBalance) {
        this.type = type;
        this.id = id;
        this.balance = initialBalance.setScale(2, RoundingMode.HALF_UP);
    }

    // ------------------------------------------------------------------
    // Transaction Methods
    // ------------------------------------------------------------------

    /**
     * Adds a specified amount to the account balance.
     *
     * @param amount The amount to credit.
     * @return The updated account balance.
     */
    public BigDecimal credit(BigDecimal amount) {
        this.balance = this.balance.add(amount).setScale(2, RoundingMode.HALF_UP);
        return this.balance;
    }

    /**
     * Subtracts a specified amount from the account balance.
     * Subclasses may override this method to enforce specific rules (e.g., overdraft limits).
     *
     * @param amount The amount to debit.
     * @return The updated account balance.
     */
    public BigDecimal debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount).setScale(2, RoundingMode.HALF_UP);
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
