# Bank Management System (Java)

A console-based banking system demonstrating object-oriented design, data
validation, and business-logic implementation.

## How to run

```bash
javac *.java
java BankManagementSystem
```

Requires a JDK (Java 17+; written and tested against Java 21).

## Project structure

| File | Responsibility |
|---|---|
| `BankManagementSystem.java` | `main()` and the console menu — the only class that talks to `System.in`/`System.out`. |
| `Bank.java` | Owns all accounts. Creates accounts, generates account numbers, validates holder details, and is the entry point for deposit/withdraw/transfer. |
| `Account.java` | **Abstract** base class holding shared state (account number, holder, balance, transaction history) and shared rules (deposit logic, amount validation). |
| `SavingsAccount.java` | Extends `Account`. Enforces a **minimum balance of 500** on withdrawal. |
| `CurrentAccount.java` | Extends `Account`. Allows the balance to go negative up to a **10,000 overdraft limit**. |
| `Transaction.java` | Immutable record of one deposit/withdrawal, with a generated ID and timestamp. |
| `TransactionType.java`, `AccountType.java` | Supporting enums. |
| `InvalidAmountException.java`, `InsufficientFundsException.java`, `AccountNotFoundException.java`, `InvalidAccountException.java` | Custom checked exceptions used to drive validation (see below). |

## Design notes (mapped to the 5-step workflow)

1. **Account class (account no, balance)** — `Account` is an abstract class
   holding `accountNumber` and `balance` (plus holder name, phone, open date,
   and history). Making it abstract, with `SavingsAccount`/`CurrentAccount`
   subclasses, lets each account type enforce its own withdrawal rule while
   sharing everything else — a natural fit for OOP over a single flat class
   with `if/else` branches on account type.
2. **Deposit and withdrawal** — `deposit()` is implemented once in `Account`
   (identical for every account type). `withdraw()` is `abstract` in
   `Account` and overridden by each subclass with its own business rule.
3. **Show account details** — `Account.displayDetails()` prints a formatted
   summary; `Bank.listAllAccounts()` + the menu's "List All Accounts" option
   prints a table of every account.
4. **Validate transactions** — every deposit/withdraw amount is checked for:
   positivity, a per-transaction ceiling, and at most 2 decimal places
   (`Account.validateMonetaryAmount`). Withdrawals are additionally checked
   against the account-specific rule (minimum balance / overdraft limit).
   Account creation validates the holder name (letters only, 3-50 chars),
   phone number (10 digits), and opening balance (same amount rules, plus a
   type-specific minimum). Invalid input raises a specific checked exception
   rather than returning an error code, so callers can't accidentally ignore
   a failure.
5. **Maintain transaction history** — every successful deposit/withdrawal
   (and the initial deposit at account opening) appends a `Transaction` to
   an internal list on the account, viewable via menu option 5.

A `transfer()` method (menu option 7) is also included as a small bonus: it
withdraws from one account and deposits into another, rolling back the
withdrawal if the deposit side ever fails.

## Sample session

```
1. Create Account   -> holder name, 10-digit phone, Savings/Current, opening deposit
2. Deposit           -> account number, amount
3. Withdraw           -> account number, amount
4. Check Balance / Account Details
5. View Transaction History
6. List All Accounts
7. Transfer Funds
0. Exit
```

Account numbers are auto-generated starting at `100001`.
