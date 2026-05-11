package bank.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * ============================================================================
 *  Transaction.java
 *  Owner: Max Pribac (mpribac@umbc.edu)
 * ============================================================================
 *
 *  WHAT THIS REPRESENTS:
 *      One single thing happening to one account at one time. Examples:
 *        - "$200 deposited to CHK-1001 on 2026-04-29 at 10:15"
 *        - "$50 fee charged on CHK-1001 on 2026-04-30"
 *
 *      A TRANSFER produces TWO Transaction records (one TRANSFER_OUT on the
 *      source, one TRANSFER_IN on the destination).
 *
 *  WHO READS THESE:
 *      - StatementGenerator  — prints them
 *      - TransactionHistory  — filters them
 *      - JsonStore           — saves and loads them
 *
 *  IMMUTABILITY:
 *      Once a Transaction is recorded its fields never change. No setters.
 * ============================================================================
 */
public class Transaction {

    private String id;                  // unique UUID
    private TransactionType type;
    private String accountId;           // which account this happened on
    private BigDecimal amount;          // always POSITIVE; type tells direction
    private LocalDateTime timestamp;
    private String description;         // short human note
    private String relatedAccountId;    // for transfers: the OTHER account, else null

    /** No-arg constructor required by Gson. */
    protected Transaction() { }

    /**
     * Records a new transaction.
     *
     * @param type              the kind of transaction (DEPOSIT, WITHDRAW, ...)
     * @param accountId         the account this happened on
     * @param amount            the dollar amount (must be positive)
     * @param description       a short human-readable note
     * @param relatedAccountId  the other account in a transfer, or null
     */
    public Transaction(TransactionType type,
                       String accountId,
                       BigDecimal amount,
                       String description,
                       String relatedAccountId) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.accountId = accountId;
        this.amount = amount.abs().setScale(2, RoundingMode.HALF_UP);
        this.timestamp = LocalDateTime.now();
        this.description = description;
        this.relatedAccountId = relatedAccountId;
    }

    /** Convenience constructor for non-transfer transactions. */
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
        return String.format("[%s] %s %s $%s — %s",
                timestamp, type, accountId, amount, description);
    }
}
