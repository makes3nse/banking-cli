package lv.v3nom.cli.impl;

import lv.v3nom.application.dto.responses.AccountResponse;
import lv.v3nom.application.dto.responses.BalanceResponse;
import lv.v3nom.application.dto.responses.TransactionHistoryResponse;
import lv.v3nom.application.dto.responses.TransactionResponse;

import java.util.List;

public class MenuRenderer {
    // === Authentication ==
    public void showWelcomeScreen() {

    }
    public void showLoginMenu() {

    }
    public void showRegisterMenu() {

    }
    public void showLoginSuccess(String name) {

    }
    public void showLoginFailure(String reason) {

    }
    public void showLoggedOut() {

    }

    // === Main menu ===
    public void showMainMenu(String userName) {
        // Prints Logged in as: {username};
        // Lists: 1.Accounts 2.Open Account 3.Profile settings 4.Logout 5.Exit;
        // *1.Accounts lists (for each found account): 1.Account details 2.Balance 3.Transaction history 4.Account settings;
        // *1.Account details shows account status, currency, balance etc.
        // *2.Balance just quickly fetches small portion of data
        // *3.Transaction history lists indexed transactions (e.g. 1,2,3,4,5... prompts to enter index to view transaction details)
        // *4.Account settings lists: 1.Freeze/Unfreeze 2.Close account
    }

    // === Prompts ===
    public void promptAccountId() {
        
    }
    public void promptAmount() {

    }
    public void promptCurrency() {

    }
    public void promptSourceAccount() {

    }
    public void promptTargetAccount() {

    }
    public void promptNewName() {

    }
    public void promptNewEmail() {

    }
    public void promptNewPhone() {

    }
    public void promptOldPassword() {

    }
    public void promptNewPassword() {

    }
    public void promptTransactionId() {

    }
    public void promptFromDate() {
        // "Enter From Date (YYYY-MM-DD) or press Enter for all: "
    }
    public void promptToDate() {
        // "Enter To Date (YYYY-MM-DD) or press Enter for all: "
    }
    public void promptAccountIdToClose() {

    }
    public void promptAccountIdToFreeze() {

    }
    public void promptAccountIdToUnfreeze() {

    }

    // Success/Error output
    public void showSuccess(String message) {

    }
    public void showError(String message) {

    }
    public void showInvalidOption() {

    }
    public void showGoodbye() {

    }

    // response display
    public void showBalance(BalanceResponse balance) {

    }
    public void showAccount(AccountResponse account) {

    }
    public void showAccountList(List<AccountResponse> accounts) {

    }
    public void showTransaction(TransactionResponse transaction) {

    }
    public void showTransactionHistory(TransactionHistoryResponse response) {

    }
    public void showOperationStatus(String operation, String status, String message) {

    }

    // utils/etc
    public void showInfo(String message) {

    }
}
