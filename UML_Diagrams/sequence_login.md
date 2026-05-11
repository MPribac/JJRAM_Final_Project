# Sequence Diagram — Login

What happens when a user types their Customer ID and PIN at the prompt.

```mermaid
sequenceDiagram
    actor User
    participant Main
    participant AuthService
    participant DataRepository
    participant Customer
    participant JsonStore

    User->>Main: Customer ID, PIN
    Main->>AuthService: login(id, pin)
    AuthService->>DataRepository: findCustomer(id)
    DataRepository-->>AuthService: Customer (or null)

    alt customer is null
        AuthService-->>Main: null
    else customer exists
        AuthService->>Customer: verifyPin(pin)
        Customer->>Customer: hashPin(pin) and compare
        alt pin matches
            Customer-->>AuthService: true (resets failedAttempts)
            AuthService->>DataRepository: save()
            DataRepository->>JsonStore: save(bank)
            AuthService-->>Main: Customer
        else pin wrong
            Customer-->>AuthService: false (increments counter, locks at 3)
            AuthService->>DataRepository: save()
            AuthService-->>Main: null
        end
    end

    Main-->>User: Welcome OR "try again"
```

## Notes

- PINs are stored as SHA-256 hex hashes — the plaintext never touches disk.
- Three wrong PINs in a row sets `locked = true`. Only an admin call to
  `auth.unlock(id)` clears the flag.
- Every state change (failed attempt, lock, successful login) gets persisted
  immediately — closing the program mid-attempt doesn't reset the counter.
