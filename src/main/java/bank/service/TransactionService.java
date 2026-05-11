package bank.service;

import bank.model.Account;
import bank.model.Customer;
import bank.model.Transaction;
import bank.model.TransactionType;
import bank.persistence.DataRepository;

import java.math.BigDecimal;

/**
 * ============================================================================
 *  TransactionService.java
 *  Owner: Max Pribac (mpribac@umbc.edu)
 * ============================================================================
 *
 *  ROLE:
 *      Three public methods: deposit, withdraw, transfer. Each one:
 *          1. Validates the input.
 *          2. Calls the right method on the Account (credit / debit).
 *          3. Records a Transaction on the Customer.
 *          4. Tells the repository to save.
 *
 *  THE GOLDEN RULE:
 *      Either the operation FULLY succeeds, or NOTHING changes. For a transfer
 *      this means: if the credit to "to" fails after the debit to "from"
 *      succeeded, the money must be put back on "from" before the error
 *      propagates out.
 * ============================================================================
 */
public class TransactionService {

    private final DataRepository repository;
    private final OverdraftPolicy overdraftPolicy;

    public TransactionService(DataRepository repository, OverdraftPolicy overdraftPolicy) {
        this.repository = repository;
        this.overdraftPolicy = overdraftPolicy;
    }

    // ── DEPOSIT ──────────────────────────────────────────────────────────────

    /**
     * Adds money to one of the customer's accounts.
     *
     * @param customer  the account owner
     * @param accountId the target account
     * @param amount    must be positive
     */
    public void deposit(Customer customer, String accountId, BigDecimal amount) {
        requirePositive(amount);
        Account account = customer.findAccount(accountId);
        requireAccount(account, accountId);

        account.credit(amount);
        customer.recordTransaction(new Transaction(
                TransactionType.DEPOSIT, accountId, amount, "Cash deposit"));
        repository.save();
    }

    // ── WITHDRAW ─────────────────────────────────────────────────────────────

    /**
     * Removes money from one of the customer's accounts.
     * If the withdrawal would push the balance negative, the overdraft policy
     * decides whether to allow it (and whether to charge a fee).
     *
     * @throws IllegalStateException if the withdrawal is not allowed
     */
    public void withdraw(Customer customer, String accountId, BigDecimal amount) {
        requirePositive(amount);
        Account account = customer.findAccount(accountId);
        requireAccount(account, accountId);

        // Compute fee from the withdrawal amount, then verify that the account
        // can absorb BOTH the withdrawal and the fee before touching it.
        // Without this combined check, the withdrawal can succeed and the fee
        // debit can then exceed the overdraft limit — leaving a recorded
        // WITHDRAW with no FEE and a half-applied state.
        BigDecimal fee = overdraftPolicy.overdraftFeeFor(account, amount);
        BigDecimal totalDebit = amount.add(fee);

        if (!overdraftPolicy.canWithdraw(account, totalDebit)) {
            throw new IllegalStateException(
                    "Withdrawal denied: would exceed overdraft limit on " + accountId);
        }

        account.debit(amount);
        customer.recordTransaction(new Transaction(
                TransactionType.WITHDRAW, accountId, amount, "Cash withdrawal"));

        if (fee.compareTo(BigDecimal.ZERO) > 0) {
            account.debit(fee);
            customer.recordTransaction(new Transaction(
                    TransactionType.FEE, accountId, fee, "Overdraft fee"));
        }

        repository.save();
    }

    // ── TRANSFER ─────────────────────────────────────────────────────────────

    /**
     * Moves money from one of the customer's accounts to another.
     * Atomic: if the credit fails, the debit is rolled back.
     */
    public void transfer(Customer customer, String fromAccountId, String toAccountId, BigDecimal amount) {
        requirePositive(amount);
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("Cannot transfer to the same account.");
        }

        Account from = customer.findAccount(fromAccountId);
        Account to   = customer.findAccount(toAccountId);
        requireAccount(from, fromAccountId);
        requireAccount(to,   toAccountId);

        if (!overdraftPolicy.canWithdraw(from, amount)) {
            throw new IllegalStateException(
                    "Transfer denied: would exceed overdraft limit on " + fromAccountId);
        }

        from.debit(amount);
        try {
            to.credit(amount);
        } catch (RuntimeException ex) {
            // Rollback the debit so the customer keeps their money.
            from.credit(amount);
            throw ex;
        }

        customer.recordTransaction(new Transaction(
                TransactionType.TRANSFER_OUT, fromAccountId, amount,
                "Transfer to " + toAccountId, toAccountId));
        customer.recordTransaction(new Transaction(
                TransactionType.TRANSFER_IN, toAccountId, amount,
                "Transfer from " + fromAccountId, fromAccountId));

        repository.save();
    }

    // ── Tiny helpers ─────────────────────────────────────────────────────────

    private static void requirePositive(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }

    private static void requireAccount(Account account, String id) {
        if (account == null) {
            throw new IllegalArgumentException("No such account: " + id);
        }
    }
}
