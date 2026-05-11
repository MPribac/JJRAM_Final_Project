package bank.reporting;

import bank.model.Customer;
import bank.model.Transaction;
import bank.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================================
 *  TransactionHistory.java
 *  Owner: John (smartdude879@gmail.com)
 * ============================================================================
 *
 *  ROLE:
 *      Searches and filters a customer's transactions, then prints results.
 *
 *  SUPPORTED FILTERS (all optional — null means "no filter"):
 *      - date range  (from / to, inclusive)
 *      - account ID  (limit to one account)
 *      - type        (DEPOSIT, WITHDRAW, FEE, INTEREST, ...)
 *      - min / max amount
 *      - description keyword (case-insensitive substring match)
 *
 *  Results are always in chronological order (oldest first).
 *
 *  USAGE:
 *      TransactionHistory th = new TransactionHistory();
 *      List&lt;Transaction&gt; results = th.search(customer, new SearchCriteria()
 *          .type(TransactionType.WITHDRAW)
 *          .minAmount(new BigDecimal("100.00")));
 *      th.print(customer, criteria, results);
 * ============================================================================
 */
public class TransactionHistory {

    private static final int LINE_WIDTH = 70;
    private static final String DIVIDER = "-".repeat(LINE_WIDTH);
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    /**
     * Applies the given criteria to the customer's transactions and returns
     * the matching list in chronological order.
     *
     * Plain for-loop on purpose — easy to read line by line.
     */
    public List<Transaction> search(Customer customer, SearchCriteria criteria) {
        List<Transaction> results = new ArrayList<>();

        for (Transaction t : customer.getTransactions()) {
            if (matches(t, criteria)) {
                results.add(t);
            }
        }

        // Sort oldest -> newest
        Collections.sort(results, (a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));
        return results;
    }

    /**
     * Returns true if this transaction passes every non-null criteria filter.
     */
    private boolean matches(Transaction t, SearchCriteria c) {
        if (c.fromDate  != null && t.getTimestamp().isBefore(c.fromDate))  return false;
        if (c.toDate    != null && t.getTimestamp().isAfter(c.toDate))     return false;
        if (c.accountId != null && !t.getAccountId().equals(c.accountId))  return false;
        if (c.type      != null && t.getType() != c.type)                  return false;
        if (c.minAmount != null && t.getAmount().compareTo(c.minAmount) < 0) return false;
        if (c.maxAmount != null && t.getAmount().compareTo(c.maxAmount) > 0) return false;

        if (c.keyword != null) {
            String description = t.getDescription() == null ? "" : t.getDescription();
            if (!description.toLowerCase().contains(c.keyword.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    /** Convenience: search and print in one call. */
    public void searchAndPrint(Customer customer, SearchCriteria criteria) {
        print(customer, criteria, search(customer, criteria));
    }

    /**
     * Prints a formatted list of transactions to the terminal.
     */
    public void print(Customer customer, SearchCriteria criteria, List<Transaction> txns) {
        System.out.println();
        System.out.println(DIVIDER);
        System.out.println("  TRANSACTION HISTORY - " + customer.getName()
                + " (" + customer.getId() + ")");

        if (criteria.fromDate != null || criteria.toDate != null) {
            System.out.printf("  Period  : %s  ->  %s%n",
                    criteria.fromDate != null ? criteria.fromDate.format(DATE_FMT) : "start",
                    criteria.toDate   != null ? criteria.toDate.format(DATE_FMT)   : "now");
        }
        if (criteria.accountId != null) System.out.println("  Account : " + criteria.accountId);
        if (criteria.type      != null) System.out.println("  Type    : " + criteria.type);
        if (criteria.minAmount != null) System.out.printf("  Min amt : $%,.2f%n", criteria.minAmount);
        if (criteria.maxAmount != null) System.out.printf("  Max amt : $%,.2f%n", criteria.maxAmount);
        if (criteria.keyword   != null) System.out.println("  Keyword : \"" + criteria.keyword + "\"");

        System.out.println(DIVIDER);

        if (txns.isEmpty()) {
            System.out.println("  No transactions matched your criteria.");
            System.out.println(DIVIDER);
            System.out.println();
            return;
        }

        System.out.printf("  %-4s  %-16s  %-12s  %-14s  %10s%n",
                "#", "Date/Time", "Type", "Account", "Amount");
        System.out.println("  " + ".".repeat(LINE_WIDTH - 2));

        int i = 1;
        for (Transaction t : txns) {
            System.out.printf("  %-4d  %-16s  %-12s  %-14s  %10s%n",
                    i++,
                    t.getTimestamp().format(DATE_FMT),
                    t.getType(),
                    t.getAccountId(),
                    String.format("$%,.2f", t.getAmount()));
            if (t.getDescription() != null && !t.getDescription().isBlank()) {
                System.out.printf("        Note: %s%n", t.getDescription());
            }
        }

        System.out.println(DIVIDER);
        System.out.printf("  %d transaction(s) found.%n", txns.size());
        System.out.println(DIVIDER);
        System.out.println();
    }

    // ── SearchCriteria builder ────────────────────────────────────────────────

    /**
     * Fluent builder for search filters. Any unset field means "no filter".
     *
     * Example:
     * <pre>{@code
     *   new SearchCriteria()
     *       .from(LocalDateTime.of(2026, 1, 1, 0, 0))
     *       .to(LocalDateTime.of(2026, 3, 31, 23, 59))
     *       .type(TransactionType.DEPOSIT)
     *       .minAmount(new BigDecimal("50.00"));
     * }</pre>
     */
    public static class SearchCriteria {

        LocalDateTime fromDate;
        LocalDateTime toDate;
        String accountId;
        TransactionType type;
        BigDecimal minAmount;
        BigDecimal maxAmount;
        String keyword;

        public SearchCriteria from(LocalDateTime from)       { this.fromDate = from;   return this; }
        public SearchCriteria to(LocalDateTime to)           { this.toDate   = to;     return this; }
        public SearchCriteria account(String accountId)      { this.accountId = accountId; return this; }
        public SearchCriteria type(TransactionType type)     { this.type = type;       return this; }
        public SearchCriteria minAmount(BigDecimal min)      { this.minAmount = min;   return this; }
        public SearchCriteria maxAmount(BigDecimal max)      { this.maxAmount = max;   return this; }
        public SearchCriteria keyword(String keyword)        { this.keyword = keyword; return this; }
    }
}
