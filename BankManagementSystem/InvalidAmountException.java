/**
 * Thrown when a deposit/withdrawal amount fails validation
 * (negative, zero, exceeds limits, invalid precision, etc.)
 */
public class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }
}
