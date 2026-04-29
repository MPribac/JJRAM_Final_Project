# Person C — Statements, History & JSON Persistence

You own the "memory" of the bank: saving everything to disk and showing nice
output to the user.

---

## What you're building

1. A `JsonStore` that reads and writes the entire bank state to `bank.json`.
2. A `DataRepository` — the only class anyone else talks to. Wraps `JsonStore`.
3. A `StatementGenerator` that prints a customer's monthly statement.
4. A `TransactionHistory` searcher (filter by date range, type, amount, etc.).

---

## Your files

| File                                    | What it does                                        |
|-----------------------------------------|-----------------------------------------------------|
| `persistence/JsonStore.java`            | Read/write `bank.json` with Gson                    |
| `persistence/DataRepository.java`       | The "front door" — what everyone else uses          |
| `reporting/StatementGenerator.java`     | Pretty-prints a monthly statement                   |
| `reporting/TransactionHistory.java`     | Filters / searches a customer's transactions        |

---

## Day-by-day for you

### Day 1
- Empty stubs so the project compiles.
- Add Gson to the pom.xml (already done in the starter — verify it's there).
- Sit with Persons A and B: tell them their classes need no-arg constructors so
  Gson can deserialize them.

### Day 2
- Get `JsonStore.save()` and `JsonStore.load()` working with a hardcoded dummy
  `Bank` object. Save it, delete the in-memory version, reload from JSON, verify
  it matches.
- Don't worry about the file format being "pretty" yet — just get it working.

### Day 3
- Build `DataRepository`. It holds the `Bank` object in memory. Other people call
  `repository.findCustomer(id)`, `repository.save()`, etc.
- Wire it up so the program loads on startup and saves on shutdown.

### Day 4
- `StatementGenerator.generate(Customer, fromDate, toDate)`:
  - Header (customer name, statement period)
  - One section per account (account number, type, opening balance, transactions, closing)
  - Footer (total deposits, withdrawals, fees, interest)
- `TransactionHistory.search(...)` — should support filters: date range, type,
  min/max amount.

### Day 5
- Help wire up `Main.java` — the menu options "Print statement" and "View history"
  go through your code.

### Day 6
- Edge cases:
  - First run: `bank.json` doesn't exist → create empty bank, don't crash.
  - Corrupted JSON → tell user, don't lose their data (back up before overwriting).
  - Save on every transaction OR save on quit? Decide with team. **Recommend save-on-every-change** — safer.
  - `BigDecimal` deserialization: Gson handles it but make sure precision is preserved.

### Day 7
- Tests:
  - `save_then_load_returnsSameBank()`
  - `statement_includesAllTransactions_inDateRange()`

---

## What other people need from you

**Person A needs:**
- `repository.findCustomer(String id)` — used by `AuthService`
- `repository.save()` — used after PIN attempt counter changes (optional)

**Person B needs:**
- `repository.save()` — called after every successful transaction
- `repository.allCustomers()` — used by `InterestCalculator` to loop everyone

---

## JSON format (proposed)

Show this to the team on Day 1 and get agreement:

```json
{
  "customers": [
    {
      "id": "1001",
      "name": "Jane Doe",
      "pinHash": "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8",
      "failedLoginAttempts": 0,
      "locked": false,
      "accounts": [
        {
          "type": "CHECKING",
          "id": "CHK-1001",
          "balance": "500.00",
          "overdraftLimit": "100.00"
        },
        {
          "type": "SAVINGS",
          "id": "SAV-1001",
          "balance": "2000.00",
          "interestRate": "0.025"
        },
        {
          "type": "LOAN",
          "id": "LN-1001",
          "balance": "-5000.00",
          "interestRate": "0.06"
        }
      ],
      "transactions": [
        {
          "id": "T-0001",
          "type": "DEPOSIT",
          "accountId": "CHK-1001",
          "amount": "200.00",
          "timestamp": "2026-04-29T10:15:00",
          "description": "Cash deposit"
        }
      ]
    }
  ]
}
```

**Tip for handling subclasses with Gson:** because `Account` is abstract with three
subclasses, you need a `RuntimeTypeAdapterFactory` OR a `type` field that you switch
on manually. The starter code shows the manual approach (simpler).

---

## What to skip on Day 1 (do later if you have time)
- File encryption
- Backup/restore commands
- CSV export
- Pretty-printed statement (plain text is fine — boxes and ASCII tables are nice-to-have)
