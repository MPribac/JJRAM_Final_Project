package bank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Scanner;

import bank.auth.AuthService;
import bank.model.Account;
import bank.model.Customer;
import bank.model.TransactionType;
import bank.persistence.DataRepository;
import bank.reporting.StatementGenerator;
import bank.reporting.TransactionHistory;
import bank.reporting.TransactionHistory.SearchCriteria;
import bank.service.InterestCalculator;
import bank.service.OverdraftPolicy;
import bank.service.TransactionService;

/**
 * ============================================================================
 *  Main.java  —  Entry point for the JJRAM Banking System
 * ============================================================================
 *
 *  TEAM CREDITS:
 *      Lead / Wiring   : Abdul Rahman Fornah (afornah1@umbc.edu)
 *      Services        : Max Pribac          (mpribac@umbc.edu)
 *      Persistence     : John                (smartdude879@gmail.com)
 *      Helper / Reviewer: Jordon Tang        (jtang10@umbc.edu)
 *      Debugger / QA   : Ramya Bommakanti    (ramyab1@umbc.edu)
 *
 *  WHAT THIS DOES:
 *      1. Loads the bank from JSON on startup.
 *      2. Asks the user to log in (Customer ID + PIN).
 *      3. Shows a menu loop until they quit.
 *      4. Saves the bank back to JSON on shutdown.
 *
 *  RUN:
 *      mvn compile
 *      mvn exec:java -Dexec.mainClass="bank.Main"
 * ============================================================================
 */
public class Main {

    public static void main(String[] args) {

        // ── Wire up the services ─────────────────────────────────────────────
        DataRepository repo = new DataRepository();
        repo.load();
        seedDemoDataIfEmpty(repo);

        AuthService auth = new AuthService(repo);
        OverdraftPolicy overdraft = new OverdraftPolicy();
        TransactionService tx = new TransactionService(repo, overdraft);
        InterestCalculator interest = new InterestCalculator(repo);
        StatementGenerator statements = new StatementGenerator();
        TransactionHistory history = new TransactionHistory();

        Scanner in = new Scanner(System.in);

        printWelcome();

        // ── Login loop (3 strikes) ──────────────────────────────────────────
        Customer current = loginLoop(in, auth);
        if (current == null) {
            System.out.println("Goodbye.");
            return;
        }

        // ── Menu loop ───────────────────────────────────────────────────────
        boolean running = true;
        while (running) {
            printMenu(current);
            String choice = in.nextLine().trim();

            try {
                switch (choice) {
                    case "1": showAccounts(current); break;
                    case "2": handleDeposit(in, current, tx); break;
                    case "3": handleWithdraw(in, current, tx); break;
                    case "4": handleTransfer(in, current, tx); break;
                    case "5": handleStatement(current, statements); break;
                    case "6": handleHistory(in, current, history); break;
                    case "7":
                        int n = interest.applyMonthlyInterest();
                        System.out.println("Monthly interest applied to " + n + " account(s).");
                        break;
                    case "0":
                        running = false;
                        break;
                    default:
                        System.out.println("Unknown option. Try again.");
                }
            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.out.println("[Error] " + ex.getMessage());
            } catch (RuntimeException ex) {
                System.out.println("[Unexpected error] " + ex.getMessage());
            }
        }

        // ── Save and exit ───────────────────────────────────────────────────
        repo.save();
        System.out.println("Goodbye.");
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Menu handlers
    // ────────────────────────────────────────────────────────────────────────

    private static void showAccounts(Customer customer) {
        System.out.println();
        System.out.println("Accounts for " + customer.getName() + ":");
        for (Account a : customer.getAccounts()) {
            System.out.printf("  %-10s  %-9s  Balance: $%,.2f%n",
                    a.getId(), a.getType(), a.getBalance());
        }
    }

    private static void handleDeposit(Scanner in, Customer customer, TransactionService tx) {
        System.out.print("Account ID: ");
        String accountId = in.nextLine().trim();
        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(in.nextLine().trim());

        tx.deposit(customer, accountId, amount);
        System.out.println("Deposited $" + amount + " into " + accountId + ".");
    }

    private static void handleWithdraw(Scanner in, Customer customer, TransactionService tx) {
        System.out.print("Account ID: ");
        String accountId = in.nextLine().trim();
        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(in.nextLine().trim());

        tx.withdraw(customer, accountId, amount);
        System.out.println("Withdrew $" + amount + " from " + accountId + ".");
    }

    private static void handleTransfer(Scanner in, Customer customer, TransactionService tx) {
        System.out.print("From account ID: ");
        String fromId = in.nextLine().trim();
        System.out.print("To account ID: ");
        String toId = in.nextLine().trim();
        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(in.nextLine().trim());

        tx.transfer(customer, fromId, toId, amount);
        System.out.println("Transferred $" + amount + " from " + fromId + " to " + toId + ".");
    }

    private static void handleStatement(Customer customer, StatementGenerator statements) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusMonths(1);
        statements.generate(customer, start, end);
    }

    private static void handleHistory(Scanner in, Customer customer, TransactionHistory history) {
        System.out.print("Filter by type (DEPOSIT, WITHDRAW, TRANSFER_OUT, TRANSFER_IN, INTEREST, FEE) or blank for all: ");
        String typeStr = in.nextLine().trim();

        SearchCriteria criteria = new SearchCriteria();
        if (!typeStr.isEmpty()) {
            try {
                criteria.type(TransactionType.valueOf(typeStr.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                System.out.println("Unknown type — showing all transactions.");
            }
        }
        history.searchAndPrint(customer, criteria);
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Login + welcome helpers
    // ────────────────────────────────────────────────────────────────────────

    private static Customer loginLoop(Scanner in, AuthService auth) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            System.out.print("Customer ID: ");
            String id = in.nextLine().trim();
            System.out.print("PIN: ");
            String pin = in.nextLine().trim();

            Customer customer = auth.login(id, pin);
            if (customer != null) {
                System.out.println("Welcome, " + customer.getName() + "!");
                return customer;
            }
            int remaining = 3 - attempt;
            if (remaining > 0) {
                System.out.println("Invalid login. " + remaining + " attempt(s) remaining.");
            } else {
                System.out.println("Too many failed attempts. Exiting.");
            }
        }
        return null;
    }

    private static void printWelcome() {
        System.out.println();
        System.out.println("=================================================");
        System.out.println("        JJRAM Banking System — v1.0");
        System.out.println("=================================================");
    }

    private static void printMenu(Customer current) {
        System.out.println();
        System.out.println("--- " + current.getName() + " ---");
        System.out.println("  1) View accounts");
        System.out.println("  2) Deposit");
        System.out.println("  3) Withdraw");
        System.out.println("  4) Transfer");
        System.out.println("  5) Print statement (last 30 days)");
        System.out.println("  6) Transaction history");
        System.out.println("  7) [Admin] Apply monthly interest");
        System.out.println("  0) Quit");
        System.out.print("> ");
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Demo seed data — populated on first run so the program is usable.
    //  Default login: id=1001  pin=1234
    // ────────────────────────────────────────────────────────────────────────

    private static void seedDemoDataIfEmpty(DataRepository repo) {
        if (!repo.allCustomers().isEmpty()) {
            return;
        }
        System.out.println("[Setup] Empty bank — seeding demo customer 1001 (PIN: 1234).");
        Customer demo = new Customer("1001", "Jane Doe", "1234");
        demo.addAccount(new bank.model.CheckingAccount(
                "CHK-1001", new BigDecimal("500.00"), new BigDecimal("100.00")));
        demo.addAccount(new bank.model.SavingsAccount(
                "SAV-1001", new BigDecimal("2000.00"), new BigDecimal("0.025")));
        demo.addAccount(new bank.model.LoanAccount(
                "LN-1001", new BigDecimal("-5000.00"), new BigDecimal("0.06")));
        repo.addCustomer(demo);
        repo.save();
    }
}
