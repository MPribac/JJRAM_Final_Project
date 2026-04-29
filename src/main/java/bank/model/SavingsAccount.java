package bank.model;

import java.math.BigDecimal;

/**
 * ============================================================================
 *  SavingsAccount.java                                        [PERSON A OWNS]
 * ============================================================================
 *
 *  RULES FOR THIS ACCOUNT TYPE:
 *      - Earns interest each month (e.g. 2.5% annual = ~0.208% monthly).
 *      - CANNOT go negative. Withdraw must be <= balance.
 *      - No overdraft.
 *
 *  WHAT TO IMPLEMENT:
 *      1. Constructor that calls super("SAVINGS", id, initialBalance) and
 *         stores the interestRate field.
 *      2. Override debit() to throw if (balance - amount) < 0.
 *      3. getMonthlyInterestRate() returns the field.
 *
 *  HINT for monthly rate:
 *      If interestRate is the ANNUAL rate (e.g. 0.025 for 2.5%), monthly is
 *      annualRate / 12. Decide as a team whether to store annual or monthly
 *      and be consistent. Store ANNUAL, do the /12 in getMonthlyInterestRate().
 * ============================================================================
 */
public class SavingsAccount extends Account {

    /** Annual interest rate. Stored as a decimal: 0.025 = 2.5%. */
    private BigDecimal interestRate = new BigDecimal("0.025");

    /** No-arg for Gson. */
    protected SavingsAccount() { }

    public SavingsAccount(String id, BigDecimal initialBalance, BigDecimal annualInterestRate) {
        // TODO: super("SAVINGS", id, initialBalance);
        // TODO: this.interestRate = annualInterestRate;
    }

    @Override
    public BigDecimal debit(BigDecimal amount) {
        // TODO: if (balance - amount) < 0, throw IllegalStateException("insufficient funds")
        //       otherwise super.debit(amount).
        return super.debit(amount);
    }

    @Override
    public BigDecimal getMonthlyInterestRate() {
        // TODO: return interestRate divided by 12.
        //       use: interestRate.divide(new BigDecimal("12"), 6, RoundingMode.HALF_UP)
        return BigDecimal.ZERO;
    }

    public BigDecimal getAnnualInterestRate() {
        return interestRate;
    }
}
