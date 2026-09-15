import java.util.List;
import java.util.Scanner;

/**
 * Console entry point for the Bank Management System.
 * Wires user input to the Bank's business logic and prints results.
 */
public class BankManagementSystem {

    private final Bank bank = new Bank("AV Bank");
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new BankManagementSystem().run();
    }

    private void run() {
        System.out.println("=================================================");
        System.out.println(" Welcome to " + bank.getBankName() + " - Account Management");
        System.out.println("=================================================");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readMenuChoice();
            switch (choice) {
                case 1 -> createAccount();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> showAccountDetails();
                case 5 -> showTransactionHistory();
                case 6 -> listAllAccounts();
                case 7 -> transferFunds();
                case 0 -> {
                    System.out.println("Thank you for using " + bank.getBankName() + ". Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please choose a number from the menu.");
            }
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println();
        System.out.println("---------------- MAIN MENU ----------------");
        System.out.println("1. Create Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Check Balance / Account Details");
        System.out.println("5. View Transaction History");
        System.out.println("6. List All Accounts");
        System.out.println("7. Transfer Funds");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    // ---------------------------------------------------------------
    // Menu actions
    // ---------------------------------------------------------------

    private void createAccount() {
        System.out.println("\n-- Create New Account --");
        String name = readLine("Account holder name: ");
        String phone = readLine("Phone number (10 digits): ");
        AccountType type = readAccountType();
        double openingBalance = readAmount("Opening deposit amount: ");

        try {
            Account account = bank.openAccount(name, phone, type, openingBalance);
            System.out.println("\nAccount created successfully!");
            account.displayDetails();
        } catch (InvalidAccountException | InvalidAmountException e) {
            System.out.println("Could not create account: " + e.getMessage());
        }
    }

    private void deposit() {
        System.out.println("\n-- Deposit --");
        String accNo = readLine("Account number: ");
        double amount = readAmount("Amount to deposit: ");
        try {
            bank.deposit(accNo, amount);
            System.out.printf("Deposit successful. New balance: %.2f%n", bank.getAccount(accNo).getBalance());
        } catch (AccountNotFoundException | InvalidAmountException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }

    private void withdraw() {
        System.out.println("\n-- Withdraw --");
        String accNo = readLine("Account number: ");
        double amount = readAmount("Amount to withdraw: ");
        try {
            bank.withdraw(accNo, amount);
            System.out.printf("Withdrawal successful. New balance: %.2f%n", bank.getAccount(accNo).getBalance());
        } catch (AccountNotFoundException | InvalidAmountException | InsufficientFundsException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }

    private void showAccountDetails() {
        System.out.println("\n-- Account Details --");
        String accNo = readLine("Account number: ");
        try {
            bank.getAccount(accNo).displayDetails();
        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showTransactionHistory() {
        System.out.println("\n-- Transaction History --");
        String accNo = readLine("Account number: ");
        try {
            bank.getAccount(accNo).printTransactionHistory();
        } catch (AccountNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listAllAccounts() {
        System.out.println("\n-- All Accounts --");
        List<Account> accounts = bank.listAllAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts have been created yet.");
            return;
        }
        System.out.printf("%-12s %-22s %-10s %12s%n", "Acc. No.", "Holder Name", "Type", "Balance");
        for (Account a : accounts) {
            System.out.printf("%-12s %-22s %-10s %12.2f%n",
                    a.getAccountNumber(), a.getAccountHolderName(), a.getAccountType(), a.getBalance());
        }
        System.out.printf("%nTotal accounts: %d | Total deposits held: %.2f%n",
                bank.getTotalAccountCount(), bank.getTotalDeposits());
    }

    private void transferFunds() {
        System.out.println("\n-- Transfer Funds --");
        String from = readLine("From account number: ");
        String to = readLine("To account number: ");
        double amount = readAmount("Amount to transfer: ");
        try {
            bank.transfer(from, to, amount);
            System.out.println("Transfer successful.");
        } catch (AccountNotFoundException | InvalidAmountException | InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------------
    // Input helpers (keep validation/retry logic out of the business methods)
    // ---------------------------------------------------------------

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private double readAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number (e.g. 1500.50).");
            }
        }
    }

    private int readMenuChoice() {
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1; // falls into "invalid option" in the switch
        }
    }

    private AccountType readAccountType() {
        while (true) {
            System.out.print("Account type (1 = Savings, 2 = Current): ");
            String input = scanner.nextLine().trim();
            if (input.equals("1")) return AccountType.SAVINGS;
            if (input.equals("2")) return AccountType.CURRENT;
            System.out.println("Please enter 1 or 2.");
        }
    }
}
