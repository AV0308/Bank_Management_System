import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class representing a bank account.
 *
 * Holds the data common to every account (account number, holder name,
 * balance, transaction history) and the validation / business rules that
 * apply to ALL accounts. Concrete subclasses (SavingsAccount, CurrentAccount)
 * only need to supply their own withdrawal rule via withdraw().
 */
public abstract class Account {

    // A single withdrawal/deposit above this amount is rejected.
    // (simple fraud-control business rule)
    protected static final double MAX_SINGLE_TRANSACTION = 500000.0;

    private final String accountNumber;
    private final String accountHolderName;
    private final String phoneNumber;
    private final LocalDateTime dateOpened;
    private final List<Transaction> transactionHistory = new ArrayList<>();

    protected double balance;

    protected Account(String accountNumber, String accountHolderName,
                       String phoneNumber, double openingBalance) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.phoneNumber = phoneNumber;
        this.balance = openingBalance;
        this.dateOpened = LocalDateTime.now();
        transactionHistory.add(new Transaction(TransactionType.ACCOUNT_OPENED, openingBalance, balance));
    }

    // ---------------------------------------------------------------
    // Core operations
    // ---------------------------------------------------------------

    /**
     * Deposits money into the account. Deposit rules are identical for
     * every account type, so this is implemented once here (final).
     */
    public final void deposit(double amount) throws InvalidAmountException {
        validateAmount(amount);
        balance = round2(balance + amount);
        transactionHistory.add(new Transaction(TransactionType.DEPOSIT, amount, balance));
    }

    /**
     * Withdraws money from the account. Each account type enforces its
     * own business rule (minimum balance vs. overdraft limit), so this
     * is left abstract for subclasses to implement.
     */
    public abstract void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException;

    /**
     * Common amount validation shared by deposit and every withdraw()
     * implementation. Delegates to the static version so the exact same
     * rule set can also be applied to opening deposits by Bank.
     */
    protected void validateAmount(double amount) throws InvalidAmountException {
        validateMonetaryAmount(amount);
    }

    /**
     * Reusable, instance-independent amount validation: must be positive,
     * within the per-transaction ceiling, and no more than 2 decimal places.
     * Public and static so Bank can apply it to opening deposits too.
     */
    public static void validateMonetaryAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero.");
        }
        if (amount > MAX_SINGLE_TRANSACTION) {
            throw new InvalidAmountException(
                    String.format("Amount exceeds the maximum allowed per transaction (%.2f).", MAX_SINGLE_TRANSACTION));
        }
        // Guard against more than 2 decimal places (e.g. 100.999)
        double rounded = round2(amount);
        if (Math.abs(rounded - amount) > 1e-9) {
            throw new InvalidAmountException("Amount cannot have more than 2 decimal places.");
        }
    }

    protected static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Lets subclasses append a completed transaction to the shared,
     * encapsulated history list (the list itself stays private to Account).
     */
    protected final void addTransaction(TransactionType type, double amount, double balanceAfter) {
        transactionHistory.add(new Transaction(type, amount, balanceAfter));
    }

    // ---------------------------------------------------------------
    // Display
    // ---------------------------------------------------------------

    public void displayDetails() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        System.out.println("------------------------------------------------------");
        System.out.println("Account Number   : " + accountNumber);
        System.out.println("Account Holder   : " + accountHolderName);
        System.out.println("Phone Number     : " + phoneNumber);
        System.out.println("Account Type     : " + getAccountType());
        System.out.printf ("Current Balance  : %.2f%n", balance);
        System.out.println("Date Opened      : " + dateOpened.format(fmt));
        System.out.println("------------------------------------------------------");
    }

    public void printTransactionHistory() {
        System.out.println("Transaction history for account " + accountNumber + ":");
        if (transactionHistory.isEmpty()) {
            System.out.println("  (no transactions yet)");
            return;
        }
        for (Transaction t : transactionHistory) {
            System.out.println("  " + t);
        }
    }

    /** Returns an unmodifiable view so callers can read but not tamper with history. */
    public List<Transaction> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    // ---------------------------------------------------------------
    // Simple getters
    // ---------------------------------------------------------------

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public double getBalance() {
        return balance;
    }

    public LocalDateTime getDateOpened() {
        return dateOpened;
    }

    public abstract AccountType getAccountType();
}
