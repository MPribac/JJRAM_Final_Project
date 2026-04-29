package bank.model;

import java.math.BigDecimal;

/**
 * ============================================================================
 *  LoanAccount.java                                           [PERSON A OWNS]
 * ============================================================================
 *
 *  RULES FOR THIS ACCOUNT TYPE:
 *      - The "balance" is what the customer OWES. Stored as a NEGATIVE number.
 *        Example: balance = -5000.00 means they owe $5000.
 *      - Each month, interest is added to the debt (balance becomes MORE negative).
 *      - "Deposit" means making a loan payment -- it brings the balance closer
 *        to zero. Once it hits zero, the loan is paid off.
 *      - "Withdraw" doesn't really apply; you can't withdraw from a loan.
 *        Throw an exception if anyone tries.
 *
 *  WHAT TO IMPLEMENT:
 *      1. Constructor that calls super("LOAN", id, initialBalance) and stores
 *         the interestRate.
 *      2. Override debit() to ALWAYS throw IllegalStateException.
 *      3. getMonthlyInterestRate() returns annualRate / 12.
 *
 *  EXAMPLE PATTERN for "loan payment":
 *      Person B's TransactionService will call credit(payment). Because the
 *      balance starts negative, credit makes it less negative. No special code
 *      needed in this class for that -- the parent's credit() works.
 * ============================================================================
 */
public class LoanAccount extends Account {

    /** Annual interest rate, e.g. 0.06 = 6%. */
    private BigDecimal interestRate = new BigDecimal("0.06");

    /** No-arg for Gson. */
    protected LoanAccount() { }

    public LoanAccount(String id, BigDecimal initialDebt, BigDecimal annualInterestRate) {
        // TODO: super("LOAN", id, initialDebt);
        //       initialDebt should already be negative when passed in.
        // TODO: this.interestRate = annualInterestRate;
    }

    @Override
    public BigDecimal debit(BigDecimal amount) {
        // TODO: throw new IllegalStateException("Cannot withdraw from a loan account.");
        return this.balance;
    }

    @Override
    public BigDecimal getMonthlyInterestRate() {
        // TODO: return interestRate.divide(new BigDecimal("12"), 6, RoundingMode.HALF_UP);
        return BigDecimal.ZERO;
    }

    public BigDecimal getAnnualInterestRate() {
        return interestRate;
    }

    /** Convenience: the absolute amount still owed. */
    public BigDecimal getOutstandingDebt() {
        // TODO: return balance.abs();
        return BigDecimal.ZERO;
    }
}
