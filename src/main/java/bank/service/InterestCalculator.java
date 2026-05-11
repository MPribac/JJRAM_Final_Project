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
 *  InterestCalculator.java
 *  Owner: Max Pribac (mpribac@umbc.edu)
 * ============================================================================
 *
 *  ROLE:
 *      Once a month, walk every customer and every account, applying interest:
 *        - SavingsAccount : balance grows
 *        - LoanAccount    : debt grows (balance becomes more negative)
 *        - CheckingAccount: skipped (monthly rate is zero anyway)
 *
 *      Each interest application is itself a Transaction (type = INTEREST).
 *
 *  GOTCHA:
 *      For loans, the balance is negative, so balance * rate is negative.
 *      Calling credit() with a negative number makes the balance MORE negative,
 *      which is what we want (debt grew). For display we record the
 *      transaction with .abs() so users always see a positive interest amount.
 * ============================================================================
 */
public class InterestCalculator {

    private final DataRepository repository;

    public InterestCalculator(DataRepository repository) {
        this.repository = repository;
    }

    /**
     * Loops every customer and every account, applying monthly interest where
     * applicable. Saves once at the end.
     *
     * @return total number of accounts that received interest
     */
    public int applyMonthlyInterest() {
        int affected = 0;
        for (Customer customer : repository.allCustomers()) {
            for (Account account : customer.getAccounts()) {
                if (!(account instanceof SavingsAccount) && !(account instanceof LoanAccount)) {
                    continue;
                }

                BigDecimal rate = account.getMonthlyInterestRate();
                if (rate.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }

                BigDecimal interest = account.getBalance()
                        .multiply(rate)
                        .setScale(2, RoundingMode.HALF_UP);

                if (interest.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }

                account.credit(interest);

                String description = (account instanceof LoanAccount)
                        ? "Monthly loan interest"
                        : "Monthly savings interest";

                customer.recordTransaction(new Transaction(
                        TransactionType.INTEREST,
                        account.getId(),
                        interest.abs(),
                        description));

                affected++;
            }
        }
        repository.save();
        return affected;
    }
}
