package bank.reporting;

import bank.model.Customer;
import bank.model.Transaction;
import bank.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * ============================================================================
 *  TransactionHistory.java                                    [PERSON C OWNS]
 * ============================================================================
 *
 *  WHAT THIS DOES:
 *      Searches and filters a customer's transaction list. Many small filters
 *      that can stack -- "between these dates", "only deposits", "amount > $100".
 *
 *  WHO USES IT:
 *      Main.java -- the "View transaction history" menu option calls this.
 *
 *  IMPLEMENTATION TIP:
 *      Use Java Streams. They're tailor-made for this.
 *
 *      EXAMPLE PATTERN:
 *          return customer.getTransactions().stream()
 *                  .filter(t -> t.getTimestamp().toLocalDate().isAfter(from.minusDays(1)))
 *                  .filter(t -> t.getTimestamp().toLocalDate().isBefore(to.plusDays(1)))
 *                  .filter(t -> type == null || t.getType() == type)
 *                  .toList();
 *
 *      Pass `null` for any filter you want to skip. That keeps the API small
 *      while still flexible.
 * ============================================================================
 */
public class TransactionHistory {

    /**
     * Filter a customer's transactions. Any null parameter is "no filter".
     */
    public List<Transaction> search(Customer customer,
                                    LocalDate from,
                                    LocalDate to,
                                    TransactionType type,
                                    String accountId,
                                    BigDecimal minAmount,
                                    BigDecimal maxAmount) {
        // TODO: stream the customer's transactions, apply each filter only if
        //       its parameter is non-null, and return the result as a list.
        return List.of();
    }

    /**
     * Pretty-print a list of transactions to a single string.
     * Main.java just System.out.println()s whatever you return.
     */
    public String format(List<Transaction> transactions) {
        // TODO:
        //  if list is empty -> "No transactions found."
        //  otherwise loop and build a multi-line string. One row per transaction.
        //  use String.format() for alignment.
        return "";
    }
}
