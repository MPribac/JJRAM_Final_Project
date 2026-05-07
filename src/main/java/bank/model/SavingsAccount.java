package bank.model;

import java.math.BigDecimal;

/**
 * ============================================================================
 *  SavingsAccount.java
 *  Owner: Abdul Rahman Fornah (afornah1@umbc.edu)
 * ============================================================================
 *
 *  RULES FOR THIS ACCOUNT TYPE:
 *      - Earns interest on the balance each month.
 *      - Balance cannot fall below zero (no overdraft allowed).
 * ============================================================================
 */
public class SavingsAccount extends Account {

    /** Annual interest rate as a decimal (e.g., 0.025 represents 2.5%). */
    private BigDecimal interestRate = new BigDecimal("0.025");

    /** No-argument constructor required for Gson deserialization. */
    protected SavingsAccount() { }

    /**
     * Initializes a new SavingsAccount.
     * 
     * @param id The account identifier.
     * @param initialBalance The starting balance.
     * @param annualInterestRate The annual interest rate as a decimal.
     */
    public SavingsAccount(String id, BigDecimal initialBalance, BigDecimal annualInterestRate) {
        super("SAVINGS", id, initialBalance);
        this.interestRate = annualInterestRate;
    }

    /**
     * Subtracts an amount from the balance, ensuring it remains non-negative.
     *
     * @param amount The amount to withdraw.
     * @throws IllegalStateException If there are insufficient funds.
     * @return The updated balance.
     */
    @Override
    public BigDecimal debit(BigDecimal amount) {
        if (this.balance.subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Transaction rejected: Insufficient funds in savings account.");
        }
        return super.debit(amount);
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
}
