package bank.reporting;

import bank.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Person C — StatementGenerator
 *
 * Prints a formatted monthly statement to the terminal for a given customer
 * and date range. Covers:
 *   - Header  : customer name, statement period
 *   - Per-account section : account ID, type, opening balance, transactions, closing balance
 *   - Footer  : totals for deposits, withdrawals, fees, and interest
 *
 * All output goes to System.out so Main.java just calls generate() and the
 * statement appears in the terminal.
 */
public class StatementGenerator {

    // ── Formatting constants ──────────────────────────────────────────────────
    private static final int LINE_WIDTH = 65;
    private static final String DIVIDER = "─".repeat(LINE_WIDTH);
    private static final String DOUBLE_DIVIDER = "═".repeat(LINE_WIDTH);
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    private static final DateTimeFormatter PERIOD_FMT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy");

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Generates and prints the statement for the customer between fromDate and toDate
     * (both inclusive).
     *
     * @param customer  the logged-in customer
     * @param fromDate  start of statement period (inclusive)
     * @param toDate    end of statement period (inclusive)
     */
    public void generate(Customer customer, LocalDateTime fromDate, LocalDateTime toDate) {
        printHeader(customer, fromDate, toDate);

        // Totals accumulated across all accounts
        BigDecimal totalDeposits    = BigDecimal.ZERO;
        BigDecimal totalWithdrawals = BigDecimal.ZERO;
        BigDecimal totalFees        = BigDecimal.ZERO;
        BigDecimal totalInterest    = BigDecimal.ZERO;

        for (Account account : customer.getAccounts()) {
            // Filter transactions in range for this account
            List<Transaction> txns = customer.getTransactions().stream()
                    .filter(t -> t.getAccountId().equals(account.getId()))
                    .filter(t -> !t.getTimestamp().isBefore(fromDate)
                              && !t.getTimestamp().isAfter(toDate))
                    .sorted((a, b) -> a.getTimestamp().compareTo(b.getTimestamp()))
                    .collect(Collectors.toList());

            // Compute opening balance by subtracting in-range transactions from current
            BigDecimal closingBalance = account.getBalance();
            BigDecimal netEffect = txns.stream()
                    .map(this::signedAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal openingBalance = closingBalance.subtract(netEffect);

            printAccountSection(account, openingBalance, closingBalance, txns);

            // Accumulate summary totals
            for (Transaction t : txns) {
                switch (t.getType()) {
                    case DEPOSIT:
                        totalDeposits = totalDeposits.add(t.getAmount());
                        break;
                    case WITHDRAWAL:
                    case TRANSFER_OUT:
                        totalWithdrawals = totalWithdrawals.add(t.getAmount());
                        break;
                    case FEE:
                        totalFees = totalFees.add(t.getAmount());
                        break;
                    case INTEREST:
                        totalInterest = totalInterest.add(t.getAmount());
                        break;
                    default:
                        break;
                }
            }
        }

        printFooter(totalDeposits, totalWithdrawals, totalFees, totalInterest);
    }

    // ── Print helpers ─────────────────────────────────────────────────────────

    private void printHeader(Customer customer, LocalDateTime from, LocalDateTime to) {
        System.out.println();
        System.out.println(DOUBLE_DIVIDER);
        System.out.println(center("ACCOUNT STATEMENT", LINE_WIDTH));
        System.out.println(DOUBLE_DIVIDER);
        System.out.printf("  Customer   : %s (ID: %s)%n", customer.getName(), customer.getId());
        System.out.printf("  Period     : %s  →  %s%n",
                from.format(PERIOD_FMT), to.format(PERIOD_FMT));
        System.out.printf("  Generated  : %s%n", LocalDateTime.now().format(DATE_FMT));
        System.out.println(DOUBLE_DIVIDER);
    }

    private void printAccountSection(Account account,
                                     BigDecimal openingBalance,
                                     BigDecimal closingBalance,
                                     List<Transaction> txns) {
        System.out.println();
        System.out.println(DIVIDER);
        System.out.printf("  Account : %-20s  Type: %s%n",
                account.getId(), account.getType());
        System.out.println(DIVIDER);
        System.out.printf("  %-38s  %10s%n", "Opening Balance", fmt(openingBalance));
        System.out.println(DIVIDER);

        if (txns.isEmpty()) {
            System.out.println("  No transactions in this period.");
        } else {
            System.out.printf("  %-12s  %-18s  %-12s  %10s%n",
                    "Date", "Description", "Type", "Amount");
            System.out.println("  " + "·".repeat(LINE_WIDTH - 2));
            for (Transaction t : txns) {
                System.out.printf("  %-12s  %-18s  %-12s  %10s%n",
                        t.getTimestamp().format(DateTimeFormatter.ofPattern("MMM dd HH:mm")),
                        truncate(t.getDescription(), 18),
                        t.getType(),
                        signedFmt(t));
            }
        }

        System.out.println(DIVIDER);
        System.out.printf("  %-38s  %10s%n", "Closing Balance", fmt(closingBalance));
    }

    private void printFooter(BigDecimal deposits,
                              BigDecimal withdrawals,
                              BigDecimal fees,
                              BigDecimal interest) {
        System.out.println();
        System.out.println(DOUBLE_DIVIDER);
        System.out.println(center("SUMMARY", LINE_WIDTH));
        System.out.println(DOUBLE_DIVIDER);
        System.out.printf("  %-38s  %10s%n", "Total Deposits",    fmt(deposits));
        System.out.printf("  %-38s  %10s%n", "Total Withdrawals", fmt(withdrawals));
        System.out.printf("  %-38s  %10s%n", "Total Fees",        fmt(fees));
        System.out.printf("  %-38s  %10s%n", "Total Interest",    fmt(interest));
        System.out.println(DOUBLE_DIVIDER);
        System.out.println();
    }

    // ── Formatting utilities ──────────────────────────────────────────────────

    /** The signed net effect of a transaction on the account (positive = credit, negative = debit). */
    private BigDecimal signedAmount(Transaction t) {
        switch (t.getType()) {
            case DEPOSIT:
            case TRANSFER_IN:
            case INTEREST:
                return t.getAmount();
            case WITHDRAWAL:
            case TRANSFER_OUT:
            case FEE:
                return t.getAmount().negate();
            default:
                return BigDecimal.ZERO;
        }
    }

    /** Formats a transaction amount with a +/- prefix. */
    private String signedFmt(Transaction t) {
        BigDecimal signed = signedAmount(t);
        String prefix = signed.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        return prefix + fmt(signed);
    }

    private String fmt(BigDecimal value) {
        return String.format("$%,.2f", value);
    }

    private String center(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}
