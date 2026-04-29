package bank.service;

import bank.model.Account;
import bank.model.CheckingAccount;

import java.math.BigDecimal;

/**
 * ============================================================================
 *  OverdraftPolicy.java                                       [PERSON B OWNS]
 * ============================================================================
 *
 *  WHAT THIS DOES:
 *      One central place that decides what happens when a checking account
 *      would go below zero. Two questions:
 *
 *        1. Can this withdrawal go through?  (canWithdraw)
 *        2. If we did go negative, is there a fee?  (overdraftFeeFor)
 *
 *  WHY A SEPARATE CLASS:
 *      Keeps account classes simple. Bank rules tend to change ("waive fees
 *      for VIPs", "first overdraft per month is free") -- having one class to
 *      change is much nicer than scattering rules everywhere.
 *
 *  EXAMPLE PATTERN:
 *
 *      public boolean canWithdraw(Account a, BigDecimal amount) {
 *          if (a instanceof CheckingAccount c) {
 *              BigDecimal projected = a.getBalance().subtract(amount);
 *              BigDecimal floor = c.getOverdraftLimit().negate();
 *              return projected.compareTo(floor) >= 0;
 *          }
 *          // savings/loan handle their own rules
 *          return true;
 *      }
 * ============================================================================
 */
public class OverdraftPolicy {

    /** Fee charged if a checking account dips below zero. */
    private final BigDecimal overdraftFee = new BigDecimal("35.00");

    /** Returns true if the account can support this withdrawal. */
    public boolean canWithdraw(Account account, BigDecimal amount) {
        // TODO: implement the example pattern above.
        return true;
    }

    /**
     * Returns the fee to charge if this withdrawal causes the account to go
     * below zero. Returns BigDecimal.ZERO if no fee applies.
     *
     * EXAMPLE LOGIC:
     *   - Was the balance >= 0 before?
     *   - Will the balance be < 0 after?
     *   - If both yes, charge the fee. Otherwise zero.
     */
    public BigDecimal overdraftFeeFor(Account account, BigDecimal amount) {
        // TODO: implement.
        return BigDecimal.ZERO;
    }
}
