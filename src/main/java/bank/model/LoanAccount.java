package bank.model;

import java.math.BigDecimal;

/**
 * ============================================================================
 *  LoanAccount.java
 *  Owner: Abdul Rahman Fornah (afornah1@umbc.edu)
 * ============================================================================
 *
 *  RULES FOR THIS ACCOUNT TYPE:
 *      - Balance represents debt and is stored as a NEGATIVE number.
 *      - Interest is charged monthly, increasing the debt (making the balance
 *        more negative).
 *      - Deposits (credits) reduce the debt.
 *      - Direct withdrawals (debits) are not permitted from a loan account.
 * ============================================================================
 */
public class LoanAccount extends Account {

    /** Annual interest rate as a decimal (e.g., 0.06 represents 6%). */
    private BigDecimal interestRate = new BigDecimal("0.06");

    /** No-argument constructor required for Gson deserialization. */
    protected LoanAccount() { }

    /**
     * Initializes a new LoanAccount.
     * 
     * @param id The account identifier.
     * @param initialDebt The starting debt (should be provided as a negative value).
     * @param annualInterestRate The annual interest rate as a decimal.
     */
    public LoanAccount(String id, BigDecimal initialDebt, BigDecimal annualInterestRate) {
        super("LOAN", id, initialDebt);
        this.interestRate = annualInterestRate;
    }

    /**
     * Rejects all debit attempts, as funds cannot be withdrawn from a loan.
     *
     * @param amount The amount to debit.
     * @throws IllegalStateException Always thrown to prevent withdrawals.
     */
    @Override
    public BigDecimal debit(BigDecimal amount) {
        throw new IllegalStateException("Invalid Operation: Withdrawals are not permitted from a loan account.");
    }

    /**
     * Calculates the monthly interest rate based on the annual rate.
     * 
     * @return The monthly interest rate (Annual Rate / 12).
     */
    @Override
    public BigDecimal getMonthlyInterestRate() {
        return interestRate.divide(new BigDecimal("12"), 6, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getAnnualInterestRate() {
        return interestRate;
    }

    /** 
     * Calculates the absolute amount of debt remaining.
     * 
     * @return The outstanding debt as a positive BigDecimal.
     */
    public BigDecimal getOutstandingDebt() {
        return balance.abs();
    }
}
