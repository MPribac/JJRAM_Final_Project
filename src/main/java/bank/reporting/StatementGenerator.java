package bank.reporting;

import bank.model.Account;
import bank.model.Customer;
import bank.model.Transaction;
import bank.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ============================================================================
 *  StatementGenerator.java                                    [PERSON C OWNS]
 * ============================================================================
 *
 *  WHAT THIS DOES:
 *      Builds a string that looks like a bank statement and returns it.
 *      Main.java prints whatever you return.
 *
 *  WHAT GOES IN A STATEMENT:
 *      - Header: customer name, statement period
 *      - For each account: id, type, opening balance, every transaction in
 *        the period, closing balance
 *      - Footer: total deposits, withdrawals, fees, interest
 *
 *  EXAMPLE OUTPUT (target style -- doesn't have to be pixel-perfect):
 *
 *  ============================================================
 *   STATEMENT FOR Jane Doe (1001)
 *   Period: 2026-04-01  ->  2026-04-30
 *  ============================================================
 *
 *   Account: CHK-1001 (CHECKING)
 *   Opening balance: $500.00
 *  ------------------------------------------------------------
 *   2026-04-02  DEPOSIT      +$200.00   Cash deposit
 *   2026-04-15  WITHDRAW     -$50.00    ATM withdrawal
 *  ------------------------------------------------------------
 *   Closing balance: $650.00
 *
 *   Account: SAV-1001 (SAVINGS)
 *   ...
 *
 *   ----- SUMMARY -----
 *   Deposits:    $1200.00
 *   Withdrawals: $50.00
 *   Fees:        $0.00
 *   Interest:    $4.16
 *
 *  EXAMPLE PATTERN for building output:
 *
 *      StringBuilder sb = new StringBuilder();
 *      sb.append("========\n");
 *      sb.append(" STATEMENT FOR ").append(customer.getName()).append("\n");
 *      ...
 *      return sb.toString();
 * ============================================================================
 */
public class StatementGenerator {

    /**
     * Build the statement for one customer over a date range.
     * Both dates are inclusive.
     */
    public String generate(Customer customer, LocalDate from, LocalDate to) {
        // TODO: build a StringBuilder, append header, loop accounts, append
        //       transactions in range, append summary totals, return string.
        return "";
    }

    // ------------------------------------------------------------------
    // Helper ideas (private). Implement what you need.
    // ------------------------------------------------------------------

    /** Filter a customer's transactions to a given account + date range. */
    private List<Transaction> txForAccountInRange(Customer c, String accountId,
                                                  LocalDate from, LocalDate to) {
        // TODO: stream, filter, collect.
        return List.of();
    }

    /** Sum the amounts of transactions matching a given type. */
    private BigDecimal sumOfType(List<Transaction> txs, TransactionType type) {
        // TODO: stream + filter + map + reduce.
        return BigDecimal.ZERO;
    }
}
