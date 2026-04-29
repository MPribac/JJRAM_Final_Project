package bank.service;

import bank.model.Account;
import bank.model.Customer;
import bank.model.LoanAccount;
import bank.model.SavingsAccount;
import bank.model.Transaction;
import bank.model.TransactionType;
import bank.persistence.DataRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * ============================================================================
 *  InterestCalculator.java                                    [PERSON B OWNS]
 * ============================================================================
 *
 *  WHAT THIS DOES:
 *      Once a month, walk every customer, every account, and apply interest:
 *        - SavingsAccount: balance grows.        balance += balance * monthlyRate
 *        - LoanAccount:    debt grows.           balance += balance * monthlyRate
 *                          (balance is negative, so this makes it MORE negative)
 *        - CheckingAccount: skip (rate is 0 anyway).
 *
 *      Each interest application is itself a Transaction (type = INTEREST).
 *
 *  WHEN IT RUNS:
 *      The user picks "Apply monthly interest" from the admin menu in Main.
 *      In a real bank a scheduler would call this on the 1st of each month;
 *      for our demo, the menu choice is fine.
 *
 *  EXAMPLE PATTERN for a single account:
 *
 *      BigDecimal interest = account.getBalance()
 *                                   .multiply(account.getMonthlyInterestRate())
 *                                   .setScale(2, RoundingMode.HALF_UP);
 *      account.credit(interest);
 *      customer.recordTransaction(
 *          new Transaction(TransactionType.INTEREST, account.getId(), interest.abs(),
 *              "Monthly interest"));
 *
 *  GOTCHA:
 *      For loans, balance is negative, so balance * rate is negative -- credit-ing
 *      a negative number makes the balance MORE negative, which is correct
 *      (debt grew). Just record the transaction with .abs() for display.
 * ============================================================================
 */
public class InterestCalculator {

    private final DataRepository repository;

    public InterestCalculator(DataRepository repository) {
        this.repository = repository;
    }

    /**
     * Loop every customer, every account; apply interest where applicable.
     * Save once at the end.
     */
    public void applyMonthlyInterest() {
        // TODO:
        //  for each Customer c in repository.allCustomers():
        //      for each Account a in c.getAccounts():
        //          if (a instanceof SavingsAccount || a instanceof LoanAccount):
        //              compute interest using a.getMonthlyInterestRate()
        //              a.credit(interest)   // works for both types
        //              c.recordTransaction(new Transaction(INTEREST, a.getId(), interest.abs(), "..."))
        //  repository.save();
    }
}
