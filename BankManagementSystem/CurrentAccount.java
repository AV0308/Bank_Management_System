/**
 * A Current account has no minimum balance requirement, but instead
 * allows the balance to go negative up to a fixed overdraft limit.
 */
public class CurrentAccount extends Account {

    public static final double OVERDRAFT_LIMIT = 10000.0;

    public CurrentAccount(String accountNumber, String accountHolderName,
                           String phoneNumber, double openingBalance) {
        super(accountNumber, accountHolderName, phoneNumber, openingBalance);
    }

    @Override
    public void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException {
        validateAmount(amount);
        double balanceAfter = round2(balance - amount);
        if (balanceAfter < -OVERDRAFT_LIMIT) {
            throw new InsufficientFundsException(String.format(
                    "Withdrawal denied: Exceeds overdraft limit of %.2f "
                            + "(available to withdraw: %.2f).",
                    OVERDRAFT_LIMIT, balance + OVERDRAFT_LIMIT));
        }
        balance = balanceAfter;
        addTransaction(TransactionType.WITHDRAWAL, amount, balance);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.CURRENT;
    }
}
