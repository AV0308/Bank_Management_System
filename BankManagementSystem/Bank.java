import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The Bank owns the full set of customer accounts and is the single
 * entry point for account creation and transactions. It is responsible
 * for account-number generation and for validating account-opening data.
 */
public class Bank {

    private final String bankName;
    private final Map<String, Account> accounts = new LinkedHashMap<>();
    private int nextAccountNumber = 100001; // simple auto-incrementing account numbers

    public Bank(String bankName) {
        this.bankName = bankName;
    }

    // ---------------------------------------------------------------
    // Account creation
    // ---------------------------------------------------------------

    public Account openAccount(String holderName, String phoneNumber,
                                AccountType type, double openingBalance)
            throws InvalidAccountException, InvalidAmountException {

        validateHolderName(holderName);
        validatePhoneNumber(phoneNumber);
        Account.validateMonetaryAmount(openingBalance);

        double minimumOpeningBalance = (type == AccountType.SAVINGS)
                ? SavingsAccount.MINIMUM_BALANCE
                : 0.0;

        if (openingBalance < minimumOpeningBalance) {
            throw new InvalidAmountException(String.format(
                    "Opening balance must be at least %.2f for a %s account.",
                    minimumOpeningBalance, type));
        }

        String accountNumber = generateAccountNumber();
        Account account = (type == AccountType.SAVINGS)
                ? new SavingsAccount(accountNumber, holderName.trim(), phoneNumber.trim(), openingBalance)
                : new CurrentAccount(accountNumber, holderName.trim(), phoneNumber.trim(), openingBalance);

        accounts.put(accountNumber, account);
        return account;
    }

    private String generateAccountNumber() {
        return String.valueOf(nextAccountNumber++);
    }

    private void validateHolderName(String name) throws InvalidAccountException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidAccountException("Account holder name cannot be empty.");
        }
        if (!name.trim().matches("[A-Za-z ]{3,50}")) {
            throw new InvalidAccountException(
                    "Account holder name must be 3-50 letters/spaces only.");
        }
    }

    private void validatePhoneNumber(String phone) throws InvalidAccountException {
        if (phone == null || !phone.trim().matches("\\d{10}")) {
            throw new InvalidAccountException("Phone number must be exactly 10 digits.");
        }
    }

    // ---------------------------------------------------------------
    // Lookup
    // ---------------------------------------------------------------

    public Account getAccount(String accountNumber) throws AccountNotFoundException {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("No account found with number: " + accountNumber);
        }
        return account;
    }

    // ---------------------------------------------------------------
    // Transactions (thin pass-through that also confirms the account exists)
    // ---------------------------------------------------------------

    public void deposit(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException {
        getAccount(accountNumber).deposit(amount);
    }

    public void withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException, InsufficientFundsException {
        getAccount(accountNumber).withdraw(amount);
    }

    /** Simple transfer built on top of withdraw + deposit, with rollback on failure. */
    public void transfer(String fromAccountNumber, String toAccountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException, InsufficientFundsException {
        Account from = getAccount(fromAccountNumber);
        Account to = getAccount(toAccountNumber); // validates target exists before touching balances
        from.withdraw(amount);
        try {
            to.deposit(amount);
        } catch (InvalidAmountException e) {
            // Should not normally happen since 'amount' already passed from.withdraw's validation,
            // but roll back defensively so money is never lost mid-transfer.
            from.deposit(amount);
            throw e;
        }
    }

    // ---------------------------------------------------------------
    // Reporting
    // ---------------------------------------------------------------

    public List<Account> listAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public double getTotalDeposits() {
        return accounts.values().stream().mapToDouble(Account::getBalance).sum();
    }

    public String getBankName() {
        return bankName;
    }

    public int getTotalAccountCount() {
        return accounts.size();
    }
}
