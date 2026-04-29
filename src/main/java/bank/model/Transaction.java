package bank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ============================================================================
 *  Transaction.java                                           [PERSON B OWNS]
 * ============================================================================
 *
 *  WHAT THIS REPRESENTS:
 *      One single thing happening to one account at one time. Examples:
 *      - "$200 deposited to CHK-1001 on 2026-04-29 at 10:15"
 *      - "$50 fee charged on CHK-1001 on 2026-04-30"
 *
 *      A TRANSFER produces TWO Transaction records (one TRANSFER_OUT on the
 *      source, one TRANSFER_IN on the destination).
 *
 *  WHO READS THESE:
 *      - StatementGenerator -- prints them
 *      - TransactionHistory -- filters them
 *      - JsonStore           -- saves and loads them
 *
 *  IMMUTABILITY:
 *      Once a Transaction is recorded, its fields should NEVER change. Treat
 *      this object as read-only after construction. (No setters.)
 * ============================================================================
 */
public class Transaction {

    // ------------------------------------------------------------------
    // Fields. Talk to Person C before changing -- the JSON file mirrors
    // these names.
    // ------------------------------------------------------------------

    private String id;                  // unique, e.g. UUID
    private TransactionType type;
    private String accountId;           // which account this happened on
    private BigDecimal amount;          // always POSITIVE; type tells direction
    private LocalDateTime timestamp;
    private String description;         // short human note
    private String relatedAccountId;    // for transfers: the OTHER account, else null

    // ------------------------------------------------------------------

    /** No-arg for Gson. */
    protected Transaction() { }

    /**
     * EXAMPLE PATTERN for the constructor body:
     *
     *     this.id = UUID.randomUUID().toString();
     *     this.type = type;
     *     this.accountId = accountId;
     *     this.amount = amount.setScale(2, RoundingMode.HALF_UP);
     *     this.timestamp = LocalDateTime.now();
     *     this.description = description;
     *     this.relatedAccountId = relatedAccountId;
     */
    public Transaction(TransactionType type,
                       String accountId,
                       BigDecimal amount,
                       String description,
                       String relatedAccountId) {
        // TODO: see the example pattern above.
    }

    /** Convenience for non-transfer transactions. */
    public Transaction(TransactionType type, String accountId, BigDecimal amount, String description) {
        this(type, accountId, amount, description, null);
    }

    // ------------------------------------------------------------------
    // Plain getters
    // ------------------------------------------------------------------

    public String getId() { return id; }
    public TransactionType getType() { return type; }
    public String getAccountId() { return accountId; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getDescription() { return description; }
    public String getRelatedAccountId() { return relatedAccountId; }

    @Override
    public String toString() {
        // EXAMPLE -- Person C may format these differently in statements.
        return String.format("[%s] %s %s $%s  -- %s",
                timestamp, type, accountId, amount, description);
    }
}
