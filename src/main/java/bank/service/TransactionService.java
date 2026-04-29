package bank.service;

import bank.model.Account;
import bank.model.Customer;
import bank.model.Transaction;
import bank.model.TransactionType;
import bank.persistence.DataRepository;

import java.math.BigDecimal;

/**
 * ============================================================================
 *  TransactionService.java                                    [PERSON B OWNS]
 * ============================================================================
 *
 *  WHAT THIS DOES:
 *      Three public methods: deposit, withdraw, transfer. Each one:
 *          1. Validates the input.
 *          2. Calls the right method on the Account (credit / debit).
 *          3. Records a Transaction on the Customer.
 *          4. Tells the repository to save.
 *
 *  WHO USES IT:
 *      Main.java -- the menu handlers call into this.
 *
 *  THE GOLDEN RULE:
 *      Either the operation FULLY succeeds, or NOTHING changes.
 *      For a transfer that means: if the credit to "to" fails after the debit
 *      to "from" succeeded, you must put the money back on "from".
 * ============================================================================
 */
public class TransactionService {

    private final DataRepository repository;
    private final OverdraftPolicy overdraftPolicy;

    public TransactionService(DataRepository repository, OverdraftPolicy overdraftPolicy) {
        // TODO: assign both fields.
        this.repository = repository;
        this.overdraftPolicy = overdraftPolicy;
    }

    // ------------------------------------------------------------------
    // DEPOSIT
    //
    // EXAMPLE PATTERN:
    //
    //     public void deposit(Customer customer, String accountId, BigDecimal amount) {
    //         requirePositive(amount);
    //         Account account = customer.findAccount(accountId);
    //         requireAccount(account, accountId);
    //         account.credit(amount);
    //         customer.recordTransaction(
    //             new Transaction(TransactionType.DEPOSIT, accountId, amount, "Cash deposit"));
    //         repository.save();
    //     }
    // ------------------------------------------------------------------

    public void deposit(Customer customer, String accountId, BigDecimal amount) {
        // TODO: implement the example pattern above.
    }

    // ------------------------------------------------------------------
    // WITHDRAW
    //
    // Differences from deposit:
    //   - Use overdraftPolicy.canWithdraw(...) BEFORE doing the debit.
    //   - If a fee applies (overdraftPolicy.overdraftFeeFor(...) > 0),
    //     also do account.debit(fee) and record a separate FEE transaction.
    // ------------------------------------------------------------------

    public void withdraw(Customer customer, String accountId, BigDecimal amount) {
        // TODO:
        //  1. requirePositive(amount).
        //  2. Find the account.
        //  3. If !overdraftPolicy.canWithdraw(account, amount), throw IllegalStateException.
        //  4. account.debit(amount); record a WITHDRAW Transaction; save.
        //  5. (Optional) charge overdraft fee if applicable; record a FEE Transaction; save.
    }

    // ------------------------------------------------------------------
    // TRANSFER
    //
    // EXAMPLE PATTERN (atomicity matters!):
    //
    //     public void transfer(Customer customer, String fromId, String toId, BigDecimal amount) {
    //         requirePositive(amount);
    //         if (fromId.equals(toId)) throw new IllegalArgumentException("same account");
    //         Account from = customer.findAccount(fromId);
    //         Account to   = customer.findAccount(toId);
    //         // ... null checks ...
    //         from.debit(amount);                       // step 1
    //         try {
    //             to.credit(amount);                    // step 2
    //         } catch (RuntimeException ex) {
    //             from.credit(amount);                  // ROLLBACK
    //             throw ex;
    //         }
    //         customer.recordTransaction(new Transaction(
    //             TransactionType.TRANSFER_OUT, fromId, amount, "Transfer to " + toId, toId));
    //         customer.recordTransaction(new Transaction(
    //             TransactionType.TRANSFER_IN,  toId,   amount, "Transfer from " + fromId, fromId));
    //         repository.save();
    //     }
    // ------------------------------------------------------------------

    public void transfer(Customer customer, String fromAccountId, String toAccountId, BigDecimal amount) {
        // TODO: implement the example pattern above.
    }

    // ------------------------------------------------------------------
    // Tiny helpers -- write these once, use them everywhere.
    // ------------------------------------------------------------------

    private static void requirePositive(BigDecimal amount) {
        // TODO: if amount is null or <= 0, throw IllegalArgumentException("amount must be > 0").
    }

    private static void requireAccount(Account account, String id) {
        // TODO: if account is null, throw IllegalArgumentException("no such account: " + id).
    }
}
