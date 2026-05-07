package bank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ============================================================================
 *  CheckingAccount.java
 *  Owner: Abdul Rahman Fornah (afornah1@umbc.edu)
 * ============================================================================
 *
 *  RULES FOR THIS ACCOUNT TYPE:
 *      - Allows overdraft up to a specified limit.
 *      - Withdrawals exceeding the overdraft limit are rejected.
 *      - This account type does not earn interest.
 * ============================================================================
 */
public class CheckingAccount extends Account {

    /** The maximum allowed negative balance. Default is $100.00. */
    private BigDecimal overdraftLimit = new BigDecimal("100.00");

    /** No-argument constructor required for Gson deserialization. */
    protected CheckingAccount() { }

    /**
     * Initializes a new CheckingAccount.
     * 
     * @param id The account identifier.
     * @param initialBalance The starting balance.
     * @param overdraftLimit The maximum allowed overdraft amount.
     */
    public CheckingAccount(String id, BigDecimal initialBalance, BigDecimal overdraftLimit) {
        super("CHECKING", id, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    /**
     * Subtracts an amount from the balance, enforcing the overdraft limit.
     *
     * @param amount The amount to withdraw.
     * @throws IllegalStateException If the withdrawal would exceed the overdraft limit.
     * @return The updated balance.
     */
    @Override
    public BigDecimal debit(BigDecimal amount) {
        BigDecimal projectedBalance = this.balance.subtract(amount);
        
        // Check if the projected balance would drop below the allowed overdraft limit.
        // overdraftLimit is stored as a positive value (e.g., 100), so we compare against -overdraftLimit.
        if (projectedBalance.compareTo(overdraftLimit.negate()) < 0) {
            throw new IllegalStateException("Transaction rejected: Would exceed overdraft limit of $" + overdraftLimit);
        }
        
        return super.debit(amount);
    }

    @Override
    public BigDecimal getMonthlyInterestRate() {
        return BigDecimal.ZERO;   // Checking earns nothing.
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }
}
