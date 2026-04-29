package bank;

import bank.auth.AuthService;
import bank.model.Customer;
import bank.persistence.DataRepository;
import bank.reporting.StatementGenerator;
import bank.reporting.TransactionHistory;
import bank.service.InterestCalculator;
import bank.service.TransactionService;

import java.util.Scanner;

/**
 * ============================================================================
 *  Main.java  --  Entry point for the Banking System
 * ============================================================================
 *
 *  WHO OWNS THIS FILE:  Shared. One person drives the menu, the other two help
 *                       wire in their own services.
 *
 *  WHAT THIS FILE DOES:
 *      1. Loads the bank from JSON on startup.
 *      2. Asks the user to log in.
 *      3. Shows a menu loop until they quit.
 *      4. Saves the bank back to JSON on shutdown.
 *
 *  HOW TO USE THIS SKELETON:
 *      Don't touch the structure -- just fill in the TODOs. The flow already
 *      shows you which classes call which.
 *
 *  WHAT THIS SHOWS:
 *      The pattern below is just an example of how the pieces fit together.
 *      Your team can rename methods, change the menu wording, etc.
 * ============================================================================
 */
public class Main {

    public static void main(String[] args) {

        // ------------------------------------------------------------------
        // STEP 1 -- Wire up the services.
        //
        // Person C builds DataRepository. Persons A and B's services take it
        // in their constructor so they can save/load.
        //
        // EXAMPLE PATTERN (delete the comments and fill in the real thing):
        //
        //     DataRepository repo = new DataRepository("data/bank.json");
        //     repo.load();
        //
        //     AuthService auth = new AuthService(repo);
        //     TransactionService tx = new TransactionService(repo);
        //     InterestCalculator interest = new InterestCalculator(repo);
        //     StatementGenerator statements = new StatementGenerator();
        //     TransactionHistory history = new TransactionHistory();
        // ------------------------------------------------------------------

        // TODO: create repository, auth, transaction service, etc.

        Scanner in = new Scanner(System.in);

        // ------------------------------------------------------------------
        // STEP 2 -- Login loop.
        //
        // Ask for ID and PIN until they get it right or hit 3 strikes.
        //
        // EXAMPLE PATTERN:
        //
        //     Customer current = null;
        //     while (current == null) {
        //         System.out.print("Customer ID: ");
        //         String id = in.nextLine().trim();
        //         System.out.print("PIN: ");
        //         String pin = in.nextLine().trim();
        //         current = auth.login(id, pin);  // returns null if bad
        //         if (current == null) System.out.println("Try again.");
        //     }
        // ------------------------------------------------------------------

        // TODO: login loop

        // ------------------------------------------------------------------
        // STEP 3 -- Main menu loop.
        //
        // EXAMPLE PATTERN:
        //
        //     boolean running = true;
        //     while (running) {
        //         System.out.println("\n1) View accounts");
        //         System.out.println("2) Deposit");
        //         System.out.println("3) Withdraw");
        //         System.out.println("4) Transfer");
        //         System.out.println("5) Print statement");
        //         System.out.println("6) Transaction history");
        //         System.out.println("7) [Admin] Apply monthly interest");
        //         System.out.println("0) Quit");
        //         String choice = in.nextLine().trim();
        //         switch (choice) {
        //             case "1": ... ; break;
        //             case "2": handleDeposit(in, current, tx); break;
        //             // ...
        //             case "0": running = false; break;
        //         }
        //     }
        // ------------------------------------------------------------------

        // TODO: menu loop

        // ------------------------------------------------------------------
        // STEP 4 -- Always save before exit.
        //
        //     repo.save();
        //     System.out.println("Goodbye.");
        // ------------------------------------------------------------------

        // TODO: save and exit
    }

    // ----------------------------------------------------------------------
    //  Helper methods for each menu choice. Add more as needed.
    //  Keep them small -- the real work happens inside the services.
    // ----------------------------------------------------------------------

    // EXAMPLE PATTERN for one menu handler:
    //
    // private static void handleDeposit(Scanner in, Customer customer, TransactionService tx) {
    //     System.out.print("Account ID: ");
    //     String accountId = in.nextLine().trim();
    //     System.out.print("Amount: ");
    //     BigDecimal amount = new BigDecimal(in.nextLine().trim());
    //     try {
    //         tx.deposit(customer, accountId, amount);
    //         System.out.println("Deposited.");
    //     } catch (Exception e) {
    //         System.out.println("Failed: " + e.getMessage());
    //     }
    // }
}
