package bank.model;

/**
 * ============================================================================
 *  TransactionType.java                                       [PERSON B OWNS]
 * ============================================================================
 *
 *  WHAT THIS IS:
 *      An enum -- a fixed list of allowed values. Every Transaction object has
 *      one of these as its `type`.
 *
 *      Using an enum (instead of a String like "deposit") means the compiler
 *      catches typos for you.
 *
 *  WHO USES IT:
 *      - TransactionService writes it.
 *      - StatementGenerator and TransactionHistory filter on it.
 *      - Person C's JsonStore -- Gson serializes enums as strings automatically.
 * ============================================================================
 */
public enum TransactionType {
    DEPOSIT,
    WITHDRAW,
    TRANSFER_OUT,    // money leaving an account in a transfer
    TRANSFER_IN,     // money arriving in an account in a transfer
    INTEREST,        // monthly interest applied
    FEE              // overdraft fee, etc.
}
