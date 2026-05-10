package bank.reporting;

import bank.model.Customer;
import bank.model.Transaction;
import bank.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Person C — TransactionHistory
 *
 * Searches and filters a customer's transactions, then prints the results.
 *
 * Supported filters (all optional — pass null to skip):
 *   - date range  (fromDate / toDate, inclusive)
 *   - account ID  (filter to one account only)
 *   - type        (e.g. DEPOSIT, WITHDRAWAL, FEE, INTEREST …)
 *   - min / max amount
 *   - description keyword (case-insensitive substring match)
 *
 * Results are always returned in chronological order (oldest first).
 *
 * Usage:
 *   TransactionHistory th = new TransactionHistory();
 *
 *   // All withdrawals over $100
 *   List<Transaction> results = th.search(customer, new SearchCriteria()
 *       .type(TransactionType.WITHDRAWAL)
 *       .minAmount(new BigDecimal("100.00")));
 *   th.print(results);
 *
 *   // Convenience: search and print in one call
 *   th.searchAndPrint(customer, criteria);
 */
public class TransactionHistory {

    private static final int LINE_WIDTH = 70;
    private static final String DIVIDER = "─".repeat(LINE_WIDTH);
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Applies the given criteria to the customer's transactions and returns the
     * matching list in chronological order.
     */
    public List<Transaction> search(Customer customer, SearchCriteria criteria) {
        return customer.getTransactions().stream()
                .filter(t -> criteria.fromDate == null || !t.getTimestamp().isBefore(criteria.fromDate))
                .filter(t -> criteria.toDate   == null || !t.getTimestamp().isAfter(criteria.toDate))
                .filter(t -> criteria.accountId == null || t.getAccountId().equals(criteria.accountId))
                .filter(t -> criteria.type      == null || t.getType() == criteria.type)
                .filter(t -> criteria.minAmount == null || t.getAmount().compareTo(criteria.minAmount) >= 0)
                .filter(t -> criteria.maxAmount == null || t.getAmount().compareTo(criteria.maxAmount) <= 0)
                .filter(t -> criteria.keyword   == null
                        || (t.getDescription() != null
                            && t.getDescription().toLowerCase()
                                .contains(criteria.keyword.toLowerCase())))
                .sorted(Comparator.comparing(Transaction::getTimestamp))
                .collect(Collectors.toList());
    }

    /**
     * Searches and immediately prints results. Convenience wrapper.
     */
    public void searchAndPrint(Customer customer, SearchCriteria criteria) {
        List<Transaction> results = search(customer, criteria);
        print(customer, criteria, results);
    }

    /**
     * Prints a formatted list of transactions to the terminal.
     *
     * @param customer  the customer (used for the header)
     * @param criteria  the criteria applied (shown in sub-header for context)
     * @param txns      the filtered transaction list
     */
    public void print(Customer customer, SearchCriteria criteria, List<Transaction> txns) {
        System.out.println();
        System.out.println(DIVIDER);
        System.out.println("  TRANSACTION HISTORY — " + customer.getName()
                + " (" + customer.getId() + ")");

        // Show active filters
        if (criteria.fromDate != null || criteria.toDate != null) {
            System.out.printf("  Period  : %s  →  %s%n",
                    criteria.fromDate != null ? criteria.fromDate.format(DATE_FMT) : "start",
                    criteria.toDate   != null ? criteria.toDate.format(DATE_FMT)   : "now");
        }
        if (criteria.accountId != null) System.out.println("  Account : " + criteria.accountId);
        if (criteria.type      != null) System.out.println("  Type    : " + criteria.type);
        if (criteria.minAmount != null) System.out.printf("  Min amt : $%,.2f%n", criteria.minAmount);
        if (criteria.maxAmount != null) System.out.printf("  Max amt : $%,.2f%n", criteria.maxAmount);
        if (criteria.keyword   != null) System.out.println("  Keyword : "" + criteria.keyword + """);

        System.out.println(DIVIDER);

        if (txns.isEmpty()) {
            System.out.println("  No transactions matched your criteria.");
            System.out.println(DIVIDER);
            System.out.println();
            return;
        }

        // Column headers
        System.out.printf("  %-4s  %-16s  %-12s  %-14s  %10s%n",
                "#", "Date/Time", "Type", "Account", "Amount");
        System.out.println("  " + "·".repeat(LINE_WIDTH - 2));

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
     * Fluent builder for search filters.
     * All fields default to null (= no filter applied for that field).
     *
     * Example:
     *   new SearchCriteria()
     *       .from(LocalDateTime.of(2026, 1, 1, 0, 0))
     *       .to(LocalDateTime.of(2026, 3, 31, 23, 59))
     *       .type(TransactionType.DEPOSIT)
     *       .minAmount(new BigDecimal("50.00"))
     */
    public static class SearchCriteria {

        LocalDateTime fromDate;
        LocalDateTime toDate;
        String accountId;
        TransactionType type;
        BigDecimal minAmount;
        BigDecimal maxAmount;
        String keyword;

        public SearchCriteria from(LocalDateTime from) {
            this.fromDate = from;
            return this;
        }

        public SearchCriteria to(LocalDateTime to) {
            this.toDate = to;
            return this;
        }

        public SearchCriteria account(String accountId) {
            this.accountId = accountId;
            return this;
        }

        public SearchCriteria type(TransactionType type) {
            this.type = type;
            return this;
        }

        public SearchCriteria minAmount(BigDecimal min) {
            this.minAmount = min;
            return this;
        }

        public SearchCriteria maxAmount(BigDecimal max) {
            this.maxAmount = max;
            return this;
        }

        public SearchCriteria keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }
    }
}
