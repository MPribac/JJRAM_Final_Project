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
     * Applies a payment toward the outstanding debt.
     *
     * <p>The loan invariant is that {@code balance} is zero or negative — it
     * represents debt. A positive credit reduces (pays down) the debt. A
     * negative credit (used by the InterestCalculator) grows the debt. Paying
     * more than what is owed is rejected so the balance never goes positive.
     *
     * @throws IllegalStateException if the payment would overpay the loan.
     */
    @Override
    public BigDecimal credit(BigDecimal amount) {
        BigDecimal projected = this.balance.add(amount);
        if (projected.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException(
                    "Payment rejected: amount exceeds outstanding debt of $"
                            + getOutstandingDebt() + " on loan " + id + ".");
        }
        return super.credit(amount);
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
