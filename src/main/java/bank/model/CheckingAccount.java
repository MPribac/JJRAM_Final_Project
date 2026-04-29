package bank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ============================================================================
 *  CheckingAccount.java                                       [PERSON A OWNS]
 * ============================================================================
 *
 *  RULES FOR THIS ACCOUNT TYPE:
 *      - Allows overdraft up to a limit (e.g. $100).
 *      - If you withdraw past the limit, the operation is REJECTED.
 *      - Charges no interest (rate = 0).
 *
 *  WHO USES IT:
 *      - TransactionService calls debit(...) on it.
 *      - OverdraftPolicy reads getOverdraftLimit().
 *      - InterestCalculator reads getMonthlyInterestRate() (will be zero).
 *
 *  EXAMPLE OF AN OVERRIDE PATTERN (similar shape -- not the exact code):
 *
 *      @Override
 *      public BigDecimal debit(BigDecimal amount) {
 *          BigDecimal projected = this.balance.subtract(amount);
 *          if (projected.compareTo(someLimit) < 0) {
 *              throw new IllegalStateException("would exceed overdraft");
 *          }
 *          this.balance = projected.setScale(2, RoundingMode.HALF_UP);
 *          return this.balance;
 *      }
 * ============================================================================
 */
public class CheckingAccount extends Account {

    /** How far below zero the balance is allowed to go, e.g. -$100. */
    private BigDecimal overdraftLimit = new BigDecimal("100.00");

    /** No-arg for Gson. */
    protected CheckingAccount() { }

    public CheckingAccount(String id, BigDecimal initialBalance, BigDecimal overdraftLimit) {
        // TODO: call super("CHECKING", id, initialBalance).
        // TODO: assign overdraftLimit.
    }

    /**
     * Override debit() to enforce the overdraft rule.
     *
     * Reject (throw IllegalStateException) if (balance - amount) < -overdraftLimit.
     * Otherwise behave like the parent class.
     */
    @Override
    public BigDecimal debit(BigDecimal amount) {
        // TODO: see the example pattern in the class comment above.
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
