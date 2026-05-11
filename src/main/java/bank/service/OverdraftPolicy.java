package bank.service;

import bank.model.Account;
import bank.model.CheckingAccount;

import java.math.BigDecimal;

/**
 * ============================================================================
 *  OverdraftPolicy.java
 *  Owner            : Max Pribac        (mpribac@umbc.edu)
 *  Debugger / Tester: Ramya Bommakanti  (ramyab1@umbc.edu)
 * ============================================================================
 *
 *  ROLE:
 *      One central place that decides what happens when a checking account
 *      would dip below zero. Two questions:
 *
 *        1. Can this withdrawal go through?    (canWithdraw)
 *        2. If we went negative, is there a fee?  (overdraftFeeFor)
 *
 *  WHY A SEPARATE CLASS:
 *      Keeps the account classes simple. Bank rules tend to change ("waive
 *      fees for VIPs", "first overdraft per month is free") — having one place
 *      to change them is much nicer than scattering rules everywhere.
 * ============================================================================
 */
public class OverdraftPolicy {

    /** Fee charged when a checking account dips below zero. */
    private final BigDecimal overdraftFee = new BigDecimal("35.00");

    /**
     * Returns true if the account can support this withdrawal.
     * - CheckingAccount: allowed down to its overdraft limit.
     * - Savings / Loan:  let the account's own debit() rule decide later.
     */
    public boolean canWithdraw(Account account, BigDecimal amount) {
        if (account instanceof CheckingAccount) {
            CheckingAccount checking = (CheckingAccount) account;
            BigDecimal projected = account.getBalance().subtract(amount);
            BigDecimal floor = checking.getOverdraftLimit().negate();
            return projected.compareTo(floor) >= 0;
        }
        return true;
    }

    /**
     * Returns the fee to charge if this withdrawal causes the account to go
     * below zero. Returns BigDecimal.ZERO if no fee applies.
     *
     * Logic:
     *   - Balance was &gt;= 0 before AND will be &lt; 0 after  → charge the fee.
     *   - Otherwise → no fee.
     */
    public BigDecimal overdraftFeeFor(Account account, BigDecimal amount) {
        if (!(account instanceof CheckingAccount)) {
            return BigDecimal.ZERO;
        }
        BigDecimal before = account.getBalance();
        BigDecimal after  = before.subtract(amount);
        boolean wentNegative = before.compareTo(BigDecimal.ZERO) >= 0
                && after.compareTo(BigDecimal.ZERO) < 0;
        return wentNegative ? overdraftFee : BigDecimal.ZERO;
    }

    /** The flat fee charged on overdraft (exposed for tests / display). */
    public BigDecimal getOverdraftFee() {
        return overdraftFee;
    }
}
