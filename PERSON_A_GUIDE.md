# Person A — Customers, Accounts & Login

You own the "who" of the bank: customers, their accounts, and how they log in.

---

## What you're building

1. A `Customer` class — name, ID, PIN (hashed), list of accounts.
2. An `Account` base class with three subclasses: `CheckingAccount`, `SavingsAccount`,
   `LoanAccount`. Each has different rules.
3. An `AuthService` that handles login: customer enters ID + PIN, you say yes or no.

---

## Your files

| File                                   | What it does                                          |
|----------------------------------------|-------------------------------------------------------|
| `model/Customer.java`                  | Customer data + helper methods                        |
| `model/Account.java`                   | Abstract base — common fields like balance, ID        |
| `model/CheckingAccount.java`           | Allows overdraft (with fee). No interest.             |
| `model/SavingsAccount.java`            | Earns interest. No overdraft allowed.                 |
| `model/LoanAccount.java`               | Negative balance = amount owed. Charges interest.     |
| `auth/AuthService.java`                | Login, lockout after 3 failed PINs, logout            |

---

## Day-by-day for you

### Day 1
- Write all the empty class skeletons so the project compiles.
- Decide with the team: are PINs stored as plaintext or hashed? **Hash them.**
  Use `java.security.MessageDigest` with SHA-256. (See example in `Customer.java`.)

### Day 2
- Fill in `Customer` and `Account` (constructors, getters, basic balance changes).
- Write `CheckingAccount` fully. It's the simplest — start here.

### Day 3
- Make sure Person C can save and load a Customer. They'll come asking.
- Start `SavingsAccount` and `LoanAccount`.

### Day 4
- Finish all three account types.
- Write `AuthService` — login, logout, 3-strike lockout.

### Day 5
- Help wire up `Main.java`. Most "log in / who am I" calls go through your code.

### Day 6
- Edge cases: nonexistent customer ID, locked account, expired session.

### Day 7
- Tests. Write at least 2 JUnit tests for AuthService (good login + bad PIN).

---

## What other people need from you

**Person B needs:**
- `Account.getBalance()` and `Account.setBalance(BigDecimal)` (or `adjustBalance(...)`)
- A way to find an account by its ID — `Customer.findAccount(String accountId)`
- Each subclass exposes its own rules:
  - `CheckingAccount.getOverdraftLimit()`
  - `SavingsAccount.getInterestRate()`
  - `LoanAccount.getInterestRate()`

**Person C needs:**
- `Customer` and all `Account` subclasses must be plain data objects that Gson can
  serialize. **No fancy constructors with required arguments — Gson uses no-arg
  constructors.** Make sure each class has a no-arg constructor (can be `protected`).

---

## What to skip on Day 1 (do later if you have time)
- Password reset
- Multiple PIN attempts tracking persistence (just keep it in memory for now)
- Customer registration UI (assume customers are pre-loaded from JSON)
