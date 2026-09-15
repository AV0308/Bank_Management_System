import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An immutable record of a single deposit or withdrawal.
 * Every Account keeps a running list of these to form its transaction history.
 */
public class Transaction {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static int sequenceCounter = 100000;

    private final String transactionId;
    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;

    public Transaction(TransactionType type, double amount, double balanceAfter) {
        this.transactionId = "TXN" + (++sequenceCounter);
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = LocalDateTime.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("%-12s | %-19s | %-14s | Amount: %10.2f | Balance After: %10.2f",
                transactionId, timestamp.format(FORMATTER), type, amount, balanceAfter);
    }
}
