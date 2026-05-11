# Class Diagram

```mermaid
classDiagram
    direction LR

    class Bank {
        -List~Customer~ customers
        +getCustomers() List~Customer~
    }

    class Customer {
        -String id
        -String name
        -String pinHash
        -int failedLoginAttempts
        -boolean locked
        -List~Account~ accounts
        -List~Transaction~ transactions
        +verifyPin(String) boolean
        +findAccount(String) Account
        +addAccount(Account)
        +recordTransaction(Transaction)
        +unlock()
    }

    class Account {
        <<abstract>>
        #String type
        #String id
        #BigDecimal balance
        +credit(BigDecimal) BigDecimal
        +debit(BigDecimal) BigDecimal
        +getMonthlyInterestRate()* BigDecimal
    }

    class CheckingAccount {
        -BigDecimal overdraftLimit
        +debit(BigDecimal) BigDecimal
        +getOverdraftLimit() BigDecimal
    }

    class SavingsAccount {
        -BigDecimal interestRate
        +debit(BigDecimal) BigDecimal
        +getAnnualInterestRate() BigDecimal
    }

    class LoanAccount {
        -BigDecimal interestRate
        +debit(BigDecimal) BigDecimal
        +getOutstandingDebt() BigDecimal
    }

    class Transaction {
        -String id
        -TransactionType type
        -String accountId
        -BigDecimal amount
        -LocalDateTime timestamp
        -String description
        -String relatedAccountId
    }

    class TransactionType {
        <<enumeration>>
        DEPOSIT
        WITHDRAW
        TRANSFER_OUT
        TRANSFER_IN
        INTEREST
        FEE
    }

    class AuthService {
        -DataRepository repository
        -Customer currentUser
        +login(String, String) Customer
        +logout()
        +unlock(String) boolean
    }

    class TransactionService {
        -DataRepository repository
        -OverdraftPolicy overdraftPolicy
        +deposit(Customer, String, BigDecimal)
        +withdraw(Customer, String, BigDecimal)
        +transfer(Customer, String, String, BigDecimal)
    }

    class OverdraftPolicy {
        -BigDecimal overdraftFee
        +canWithdraw(Account, BigDecimal) boolean
        +overdraftFeeFor(Account, BigDecimal) BigDecimal
    }

    class InterestCalculator {
        -DataRepository repository
        +applyMonthlyInterest() int
    }

    class DataRepository {
        -JsonStore store
        -Bank bank
        +load()
        +save()
        +findCustomer(String) Customer
        +allCustomers() List~Customer~
        +addCustomer(Customer)
    }

    class JsonStore {
        -Gson gson
        +save(Bank)
        +load() Bank
    }

    class StatementGenerator {
        +generate(Customer, LocalDateTime, LocalDateTime)
    }

    class TransactionHistory {
        +search(Customer, SearchCriteria) List~Transaction~
        +searchAndPrint(Customer, SearchCriteria)
    }

    Bank "1" *-- "*" Customer
    Customer "1" *-- "*" Account
    Customer "1" *-- "*" Transaction
    Account <|-- CheckingAccount
    Account <|-- SavingsAccount
    Account <|-- LoanAccount
    Transaction --> TransactionType

    AuthService --> DataRepository
    TransactionService --> DataRepository
    TransactionService --> OverdraftPolicy
    InterestCalculator --> DataRepository
    DataRepository --> JsonStore
    DataRepository --> Bank
```

## Reading the diagram

- **Filled diamonds (`*--`)** = composition. The container owns the contents:
  if a `Bank` is destroyed, its `Customer`s go with it.
- **Hollow triangles (`<|--`)** = inheritance. `CheckingAccount` *is an*
  `Account`.
- **Arrows (`-->`)** = uses / has a reference to.
- **Italic methods (e.g., `getMonthlyInterestRate()*`)** = abstract — subclasses
  must implement them.
