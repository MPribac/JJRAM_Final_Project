# Sequence Diagram — Transfer (with rollback)

The most interesting flow in the system — moving money between two accounts
atomically. If anything fails after the debit, the money goes back.

```mermaid
sequenceDiagram
    actor User
    participant Main
    participant TransactionService
    participant OverdraftPolicy
    participant FromAccount as Account (from)
    participant ToAccount as Account (to)
    participant Customer
    participant DataRepository

    User->>Main: transfer fromId, toId, amount
    Main->>TransactionService: transfer(customer, fromId, toId, amount)

    TransactionService->>TransactionService: requirePositive(amount)
    TransactionService->>Customer: findAccount(fromId)
    Customer-->>TransactionService: fromAccount
    TransactionService->>Customer: findAccount(toId)
    Customer-->>TransactionService: toAccount

    TransactionService->>OverdraftPolicy: canWithdraw(from, amount)
    OverdraftPolicy-->>TransactionService: true / false

    alt not allowed
        TransactionService-->>Main: throws IllegalStateException
    else allowed
        TransactionService->>FromAccount: debit(amount)
        FromAccount-->>TransactionService: new balance

        alt to.credit succeeds
            TransactionService->>ToAccount: credit(amount)
            ToAccount-->>TransactionService: new balance
            TransactionService->>Customer: recordTransaction(TRANSFER_OUT)
            TransactionService->>Customer: recordTransaction(TRANSFER_IN)
            TransactionService->>DataRepository: save()
            TransactionService-->>Main: ok
        else to.credit throws
            TransactionService->>FromAccount: credit(amount)  Note over TransactionService,FromAccount: ROLLBACK — put the money back
            TransactionService-->>Main: rethrows the exception
        end
    end
```

## The golden rule

> Either the whole transfer succeeds, or *nothing* changed.

This is why the rollback exists: if step 2 (`to.credit`) ever fails — corrupt
state, a loan account rejecting the credit, anything — the debit on `from` is
reversed before the exception propagates. The customer never loses money on a
half-finished transfer.
