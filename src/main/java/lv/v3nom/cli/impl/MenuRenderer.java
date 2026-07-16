package lv.v3nom.cli.impl;

import lv.v3nom.application.dto.responses.*;

import java.util.List;

public class MenuRenderer {
    // === Authentication ==
    public void showWelcomeScreen(int major, int minor, int maintenance) {
        clearScreen();
        String version = String.format("%s.%s.%s", major, minor, maintenance);
        String message = String.format("Banking API. Test env. Version: %s", version);

        System.out.println(message);
    }
    public void showLoginSuccess(String name) {
        clearScreen();
        String message = String.format("Welcome, %s", name);
        System.out.println(message);
    }
    public void showLoginFailure(String reason) {
        clearScreen();
        String message = String.format("Login Failed: %s", reason);
        System.out.println(message);
    }
    public void showLoggedOut() {
        clearScreen();
        String message = String.format("Logged out!");
        System.out.println(message);
    }

    // === Main menu ===
    public void showMainMenu(String userName) {
        clearScreen();
        System.out.println("Logged in as: " + userName);
        System.out.println("\n1.Accounts");
        System.out.println("2.Open Account");
        System.out.println("3.Profile settings");
        System.out.println("\n4.Logout");
        System.out.println("5.Exit");
        System.out.print("\nOption: ");

    }
    public void showAccountsMenu(AccountListResponse accountListResponse) {
        clearScreen();
        List<AccountResponse> accounts = accountListResponse.getAccountResponses();
        int i = 0;
        for (AccountResponse a : accounts) {
            System.out.println(String.format("%s. %s | %s | %s", i, a.getAccountId(), a.getCurrency(), a.getBalance()));
            i++;
        }
        System.out.println("\n(b — back, m — main menu)");
        System.out.print("Option: ");
    }
    public void showProfileSettings() {
        clearScreen();
        System.out.println("1.Change Name");
        System.out.println("2.Change Email");
        System.out.println("3.Change Phone");
        System.out.println("4.Change Password");
        System.out.println("\n(b — back, m — main menu)");
        System.out.print("Option: ");
    }
    public void showAccountSettings(AccountResponse account) {
        clearScreen();
        System.out.println("Account ID: ");
        System.out.println("    Status: ");
        System.out.println("  Currency: ");
        System.out.println("   Balance: ");
        System.out.println("\n1.Deposit");
        System.out.println("2.Withdraw");
        System.out.println("3.Transfer");
        switch (account.getStatus()) {
            case "FROZEN":
                System.out.println("\n4.Unfreeze Account");
                break;
            default:
                System.out.println("\n4.Freeze Account");
                break;
        }
        System.out.println("\n5.Close Account");

        if (account.getOperationStatus() != "SUCCESS") {
            clearScreen();
            System.out.println(" Operation: " + account.getOperationStatus());
            System.out.println("     Error: " + account.getErrorMessage());
            System.out.println("\n(b — back, m — main menu)");
            System.out.print("Option: ");
        }
        System.out.println("\n(b — back, m — main menu)");
        System.out.print("Option: ");
    }

    // === Prompts ===
    public void promptEmail() {
        clearScreen();
        System.out.print("Email: ");
    }
    public void promptPassword() {
        clearScreen();
        System.out.print("Password: ");
    }
    public void promptAccountId() {
        clearScreen();
        System.out.print("Account ID: ");
    }
    public void promptAmount() {
        clearScreen();
        System.out.print("Amount: ");
    }
    public void promptCurrency() {
        clearScreen();
        System.out.print("Currency: ");
    }
    public void promptSourceAccount() {
        clearScreen();
        // redundant because we always know it at system level
        System.out.print("Source Account ID: ");
    }
    public void promptTargetAccount() {
        clearScreen();
        System.out.print("Receiver Account ID: ");
    }
    public void promptName() {
        clearScreen();
        System.out.print("Full Name: ");
    }
    public void promptNewEmail() {
        clearScreen();
        System.out.print("Email (new): ");
    }
    public void promptPhone() {
        clearScreen();
        System.out.print("Phone Number: ");
    }
    public void promptNewPassword() {
        clearScreen();
        System.out.print("Password (new): ");
    }
    public void promptTransactionId() {
        clearScreen();
        System.out.print("Transaction: ");
    }
    public void promptFromDate() {
        clearScreen();
        System.out.print("Correct format: dd.MM.yyyy (e.g. 25.01.2001)");
        System.out.print("FROM Date: ");
    }
    public void promptToDate() {
        clearScreen();
        System.out.print("Correct format: dd.MM.yyyy (e.g. 25.01.2001)");
        System.out.print("TO Date: ");
    }

    // Success/Error output
    public void showSuccess(String message) {
        clearScreen();
        System.out.println("Success: " + message);
    }
    public void showError(String message) {
        clearScreen();
        System.out.println(message);
    }
    public void showInvalidOption() {
        clearScreen();
        System.out.println("Input Invalid");
    }
    public void showGoodbye() {
        clearScreen();
        System.out.println("System Shutdown");
    }

    // response display
    public void showBalance(BalanceResponse balance) {
        clearScreen();
        System.out.println("Account ID: ");
        System.out.println("  Currency: ");
        System.out.println("   Balance: ");
        if (balance.getOperationStatus() != "SUCCESS") {
            clearScreen();
            System.out.println(" Operation: " + balance.getOperationStatus());
            System.out.println("     Error: " + balance.getErrorMessage());
        }
        System.out.println("\n(b — back, m — main menu)");
        System.out.print("Option: ");
    }
    public void showTransaction(TransactionResponse transaction) {
        clearScreen();
        System.out.println("Transaction ID: " + transaction.getTransactionId());
        System.out.println("          Type: " + transaction.getType());
        System.out.println("        Status: " + transaction.getStatus());
        System.out.println("        Sender: " + transaction.getSourceAccountId());
        System.out.println("      Receiver: " + transaction.getTargetAccountId());
        System.out.println("      Currency: " + transaction.getCurrency());
        System.out.println("        Amount: " + transaction.getAmount());
        System.out.println("    Created At: " + transaction.getCreatedAt());
        System.out.println("  Completed At: " + transaction.getCompletedAt());
        if ((transaction.getOperationStatus() != "SUCCESS") || (transaction.getFailureReason() != null)) {
            clearScreen();
            System.out.println("Failure reason: " + transaction.getFailureReason());
            System.out.println("     Operation: " + transaction.getOperationStatus());
            System.out.println("         Error: " + transaction.getErrorMessage());
        }
        System.out.println("\n(b — back, m — main menu)");
        System.out.print("Option: ");
    }
    public void showTransactionHistory(TransactionHistoryResponse response) {
        clearScreen();
        List<TransactionSummaryResponse> transactions = response.getTransactions();
        int i = 0;
        for (TransactionSummaryResponse t : transactions) {
            System.out.println(String.format("%s. %s | %s", i, t.getTransactionId(), t.getType()));
            i++;
        }
        System.out.println("\n(b — back, m — main menu)");
        System.out.print("Option: ");
    }
    public void showOperationStatus(String operation, String status, String message) {
        clearScreen();
        System.out.println(String.format("%s | %s | %s", operation, status, message));
        System.out.println("\n(b — back, m — main menu)");
        System.out.print("Option: ");
    }

    // utils/etc
    public void showInfo(String message) {
        clearScreen();
        System.out.println(String.format("INFO: %s", message));
        System.out.println("\n(b — back, m — main menu)");
        System.out.print("Option: ");
    }
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Fallback - print many blank lines
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }
}
