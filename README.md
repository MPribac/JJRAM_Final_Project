# Banking System Simulation — Team Project

A terminal-only Java banking simulator. Three teammates, one week.

---

## What we're building

A command-line program that lets a customer:

1. Log in with a customer ID + PIN.
2. View their accounts (Checking, Savings, Loan).
3. Deposit, withdraw, and transfer money between accounts.
4. See a printed statement and full transaction history.
5. Have all data saved to a JSON file so it persists between runs.

The bank also runs an "end of month" job that:
- Applies interest to savings accounts.
- Charges interest on loan balances.
- Charges overdraft fees on checking accounts.

---

## Tech stack

- **Java 17+** (anything 11 or newer works)
- **Gson** for reading/writing JSON
- **JUnit 5** for tests (optional but recommended)
- **Maven** for build (use the included `pom.xml`)

If you don't want Maven, you can add `gson-2.10.1.jar` to your classpath manually.

---

## Project layout

```
banking-system/
├── pom.xml
├── data/
│   └── bank.json              <-- created at runtime
└── src/main/java/bank/
    ├── Main.java                       (shared)
    ├── model/
    │   ├── Customer.java               (Person A)
    │   ├── Account.java                (Person A)
    │   ├── CheckingAccount.java        (Person A)
    │   ├── SavingsAccount.java         (Person A)
    │   ├── LoanAccount.java            (Person A)
    │   ├── Transaction.java            (shared model — Person B owns it)
    │   └── TransactionType.java        (shared enum)
    ├── auth/
    │   └── AuthService.java            (Person A)
    ├── service/
    │   ├── TransactionService.java     (Person B)
    │   ├── InterestCalculator.java     (Person B)
    │   └── OverdraftPolicy.java        (Person B)
    ├── reporting/
    │   ├── StatementGenerator.java     (Person C)
    │   └── TransactionHistory.java     (Person C)
    └── persistence/
        ├── JsonStore.java              (Person C)
        └── DataRepository.java         (Person C)
```

---

## Who owns what

| Person | Owns                                                                       |
|--------|----------------------------------------------------------------------------|
| **A**  | Customer model, Account types (Checking/Savings/Loan), Login + PIN auth    |
| **B**  | Deposits, withdrawals, transfers, overdraft rules, interest calculation    |
| **C**  | Statements, transaction history search/filter, JSON load + save            |

See `PERSON_A_GUIDE.md`, `PERSON_B_GUIDE.md`, `PERSON_C_GUIDE.md` for your detailed brief.

---

## Day-by-day plan

### Day 1 — Setup + agreed contracts (everyone together, ~2 hrs)
- Pull the repo, get it compiling.
- Read all three guides together so everyone knows what the others are building.
- **Agree on the data model.** Don't skip this. Look at `Customer.java`, `Account.java`,
  `Transaction.java` together and decide if any fields are missing.
- Each person writes empty stubs for all the methods they own — just enough that the
  whole project compiles.

### Day 2 — Build the happy path
- **A:** Customer + Account + AuthService working with hardcoded test data.
- **B:** TransactionService.deposit() and withdraw() working in-memory.
- **C:** JsonStore can save and load a single dummy Customer to/from JSON.

### Day 3 — Wire it together
- First end-to-end test: log in → deposit → save → quit → reopen → balance is still there.
- This is where most teams discover their data model is wrong. Fix it now, not on Day 6.

### Day 4 — Fill in the rest
- **A:** Finish SavingsAccount and LoanAccount.
- **B:** Transfer, overdraft fee, interest accrual.
- **C:** Full transaction history search, statement formatting.

### Day 5 — Connect everything in `Main.java`
- One person owns the menu loop. The other two are on call to fix bugs as their
  modules get exercised.

### Day 6 — Polish + edge cases
- Negative deposits? Withdraw more than balance? Transfer to your own account?
  Bad PIN three times? Empty JSON file on first run? Each of these should fail
  gracefully with a clear message.

### Day 7 — Test + demo prep
- Run through the demo script (see bottom of this file) twice. Fix anything ugly.

---

## How everyone's code connects

```
        Main (menu loop)
           |
           v
      AuthService  --->  loads Customer from DataRepository
           |
           v
   TransactionService  --->  reads/writes Account balances
           |                      records Transaction objects
           v
   DataRepository  --->  JsonStore  --->  bank.json
           |
           v
   StatementGenerator / TransactionHistory  --->  prints to terminal
```

Key rule: **nobody talks to `bank.json` directly except `JsonStore`**. Everyone else
goes through `DataRepository`.

---

## How to run

```bash
mvn compile
mvn exec:java -Dexec.mainClass="bank.Main"
```

Or in your IDE: right-click `Main.java` → Run.

---

## Demo script (what you'll show on Day 7)

1. Start with an empty `bank.json` — program creates a fresh bank.
2. Log in as customer `1001` with PIN `1234`.
3. Show three accounts: Checking $500, Savings $2000, Loan -$5000.
4. Deposit $200 into checking.
5. Transfer $1000 from savings to checking.
6. Try to withdraw $100,000 — see overdraft message.
7. Print statement.
8. Quit. Reopen the program. Log in again. Balances are still correct.
9. Run "end of month" — savings earns interest, loan accrues interest.
10. Print transaction history filtered by date range.

---

## Definition of "done"

- [ ] Program runs from `mvn exec:java` with no errors.
- [ ] All three account types work.
- [ ] Login + PIN auth works (and locks after 3 bad attempts).
- [ ] Deposit, withdraw, transfer all work and update the JSON file.
- [ ] Overdraft is handled (either blocked or fee applied — your team's call).
- [ ] Interest can be applied to savings and loans.
- [ ] Statement prints cleanly.
- [ ] Transaction history can be filtered by date range and type.
- [ ] At least 5 JUnit tests pass.
- [ ] README + demo script work as written.
