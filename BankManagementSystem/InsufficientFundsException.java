/**
 * Thrown when a withdrawal would violate the account's
 * minimum-balance or overdraft-limit business rules.
 */
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
