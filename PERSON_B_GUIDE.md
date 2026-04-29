# Person B — Transactions, Overdraft & Interest

You own the "what happens" of the bank: every time money moves, your code runs.

---

## What you're building

1. A `TransactionService` that performs **deposit**, **withdraw**, and **transfer**.
2. An `OverdraftPolicy` that decides what happens when a checking account would go
   negative (block? allow with fee? cap?).
3. An `InterestCalculator` that, when called, applies interest to all savings and
   loan accounts.
4. The `Transaction` model object (shared with Person C, but you own it).

---

## Your files

| File                                   | What it does                                          |
|----------------------------------------|-------------------------------------------------------|
| `service/TransactionService.java`      | Deposit, withdraw, transfer. Records Transactions.    |
| `service/OverdraftPolicy.java`         | Rules for negative balances on checking                |
| `service/InterestCalculator.java`      | "End of month" interest application                   |
| `model/Transaction.java`               | One record of money moving (date, amount, type, ...)  |
| `model/TransactionType.java`           | Enum: DEPOSIT, WITHDRAW, TRANSFER, INTEREST, FEE      |

---

## Day-by-day for you

### Day 1
- Empty stubs for everything so the project compiles.
- Sit with Person A and look at the `Account` class. Make sure you can read and
  change a balance from outside (you'll need to).
- Sit with Person C: agree on what fields a `Transaction` has. **Don't move on
  until you both like the model.**

### Day 2
- Implement `deposit(Account, BigDecimal)` — should be ~5 lines.
- Implement `withdraw(Account, BigDecimal)` — slightly trickier, has to check balance.
- Every successful operation creates a `Transaction` and saves it.

### Day 3
- Implement `transfer(Account from, Account to, BigDecimal)`. **Important:** this is
  two operations. If the second one fails, the first must be rolled back.
- Wire in `OverdraftPolicy` — for now just "block if balance would go below zero".

### Day 4
- `InterestCalculator.applyMonthlyInterest(Bank bank)` — loops through all customers,
  finds their savings/loan accounts, applies interest.
- Each interest application is itself a Transaction (type = INTEREST).

### Day 5
- Help wire `Main.java`. Most menu options like "Deposit" call into your service.

### Day 6
- Edge cases:
  - Negative deposit amount? Reject.
  - Zero amount? Reject.
  - Transfer to the same account? Reject.
  - More precision than 2 decimal places? Round (use `BigDecimal.setScale(2, RoundingMode.HALF_UP)`).

### Day 7
- Write at least 3 JUnit tests:
  - `withdraw_whenInsufficientFunds_throws()`
  - `transfer_isAtomic()` (if `to` fails, `from` is unchanged)
  - `interest_appliedCorrectly_onSavings()`

---

## What other people need from you

**Person A needs:** nothing directly — you call into their code, not the other way.

**Person C needs:**
- Every `Transaction` you create must be added to the customer's history list so they
  can save it.
- Tell them when something changed so they know to save. Easiest: have your service
  call `repository.save()` at the end of every successful operation. Talk to them
  about how.

---

## Important rules for money

**ALWAYS use `BigDecimal`, never `double` or `float` for money.** Floating-point math
will lose pennies. Example:

```java
// BAD — don't do this
double balance = 100.0;
balance -= 0.1;
balance -= 0.1;
balance -= 0.1;
// balance is now 99.69999999999999, not 99.70

// GOOD
BigDecimal balance = new BigDecimal("100.00");
balance = balance.subtract(new BigDecimal("0.10"));
```

Round to 2 decimal places when displaying:
```java
balance.setScale(2, RoundingMode.HALF_UP);
```

---

## What to skip on Day 1 (do later if you have time)
- Daily/compound interest (monthly is fine for the demo)
- Per-account fee schedules
- Anti-fraud rules
