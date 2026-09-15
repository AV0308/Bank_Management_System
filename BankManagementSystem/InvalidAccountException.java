/**
 * Thrown when account-holder details or account-opening
 * parameters fail validation.
 */
public class InvalidAccountException extends Exception {
    public InvalidAccountException(String message) {
        super(message);
    }
}
