/**
 * A Savings account must always retain a minimum balance.
 * Withdrawals that would drop the balance below that floor are rejected.
 */
public class SavingsAccount extends Account {

    public static final double MINIMUM_BALANCE = 500.0;
    private static final double ANNUAL_INTEREST_RATE = 4.0; // percent

    public SavingsAccount(String accountNumber, String accountHolderName,
                           String phoneNumber, double openingBalance) {
        super(accountNumber, accountHolderName, phoneNumber, openingBalance);
    }

    @Override
    public void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException {
        validateAmount(amount);
        double balanceAfter = round2(balance - amount);
        if (balanceAfter < MINIMUM_BALANCE) {
            throw new InsufficientFundsException(String.format(
                    "Withdrawal denied: Savings account must maintain a minimum balance of %.2f "
                            + "(available to withdraw: %.2f).",
                    MINIMUM_BALANCE, Math.max(0, balance - MINIMUM_BALANCE)));
        }
        balance = balanceAfter;
        addTransaction(TransactionType.WITHDRAWAL, amount, balance);
    }

    /** Estimated interest for the current balance, for one month. */
    public double calculateMonthlyInterest() {
        return round2(balance * ANNUAL_INTEREST_RATE / 100 / 12);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.SAVINGS;
    }
}
