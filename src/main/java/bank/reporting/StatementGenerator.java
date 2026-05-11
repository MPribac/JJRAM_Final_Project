package bank.reporting;

import bank.model.Account;
import bank.model.Customer;
import bank.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ============================================================================
 *  StatementGenerator.java
 *  Owner: John (smartdude879@gmail.com)
 * ============================================================================
 *
 *  ROLE:
 *      Prints a formatted monthly statement to the terminal for a given customer
 *      and date range.
 *
 *  STRUCTURE:
 *      - Header              : customer name, statement period
 *      - Per-account section : account ID, type, opening balance,
 *                              transactions, closing balance
 *      - Footer              : totals for deposits, withdrawals, fees, interest
 * ============================================================================
 */
public class StatementGenerator {

    private static final int LINE_WIDTH = 65;
    private static final String DIVIDER = "-".repeat(LINE_WIDTH);
    private static final String DOUBLE_DIVIDER = "=".repeat(LINE_WIDTH);
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
    private static final DateTimeFormatter PERIOD_FMT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter ROW_FMT =
            DateTimeFormatter.ofPattern("MMM dd HH:mm");

    /**
     * Generates and prints the statement for the customer between fromDate and
     * toDate (both inclusive).
     *
     * @param customer  the customer whose statement to print
     * @param fromDate  start of statement period (inclusive)
     * @param toDate    end of statement period (inclusive)
     */
    public void generate(Customer customer, LocalDateTime fromDate, LocalDateTime toDate) {
        printHeader(customer, fromDate, toDate);

        BigDecimal totalDeposits    = BigDecimal.ZERO;
        BigDecimal totalWithdrawals = BigDecimal.ZERO;
        BigDecimal totalFees        = BigDecimal.ZERO;
        BigDecimal totalInterest    = BigDecimal.ZERO;

        for (Account account : customer.getAccounts()) {
            // Pick out this account's transactions inside the date range.
            List<Transaction> txns = new ArrayList<>();
            for (Transaction t : customer.getTransactions()) {
                boolean sameAccount = t.getAccountId().equals(account.getId());
                boolean inRange = !t.getTimestamp().isBefore(fromDate)
                        && !t.getTimestamp().isAfter(toDate);
                if (sameAccount && inRange) {
                    txns.add(t);
                }
            }
            Collections.sort(txns, (a, b) -> a.getTimestamp().compareTo(b.getTimestamp()));

            // Opening balance = current balance MINUS everything that happened
            // in this period.
            BigDecimal closingBalance = account.getBalance();
            BigDecimal netEffect = BigDecimal.ZERO;
            for (Transaction t : txns) {
                netEffect = netEffect.add(signedAmount(t));
            }
            BigDecimal openingBalance = closingBalance.subtract(netEffect);

            printAccountSection(account, openingBalance, closingBalance, txns);

            for (Transaction t : txns) {
                switch (t.getType()) {
                    case DEPOSIT:
                    case TRANSFER_IN:
                        totalDeposits = totalDeposits.add(t.getAmount());
                        break;
                    case WITHDRAW:
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
        System.out.printf("  Period     : %s  ->  %s%n",
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
            System.out.println("  " + ".".repeat(LINE_WIDTH - 2));
            for (Transaction t : txns) {
                System.out.printf("  %-12s  %-18s  %-12s  %10s%n",
                        t.getTimestamp().format(ROW_FMT),
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

    /** Signed net effect of a transaction (positive = credit, negative = debit). */
    private BigDecimal signedAmount(Transaction t) {
        switch (t.getType()) {
            case DEPOSIT:
            case TRANSFER_IN:
            case INTEREST:
                return t.getAmount();
            case WITHDRAW:
            case TRANSFER_OUT:
            case FEE:
                return t.getAmount().negate();
            default:
                return BigDecimal.ZERO;
        }
    }

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
        return s.length() <= max ? s : s.substring(0, max - 1) + "~";
    }
}
