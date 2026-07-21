package lv.v3nom.cli.impl;

import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.*;
import lv.v3nom.application.service.AccountService;
import lv.v3nom.application.service.AuthService;
import lv.v3nom.application.service.CustomerService;
import lv.v3nom.application.service.TransactionService;
import lv.v3nom.cli.SessionManager;

import java.util.List;
import java.util.Scanner;

public class CommandHandler {
    private final MenuRenderer xeRenderer;
    private final InputParser parser;
    private final SessionManager sessionManager;
    private final AccountService accountService;
    private final AuthService authService;
    private final CustomerService customerService;
    private final TransactionService transactionService;

    public CommandHandler(MenuRenderer menuRenderer,
                          InputParser inputParser,
                          SessionManager sessionManager,
                          AccountService accountService,
                          AuthService authService,
                          CustomerService customerService,
                          TransactionService transactionService) {

        this.xeRenderer = menuRenderer;
        this.parser = inputParser;
        this.sessionManager = sessionManager;
        this.accountService = accountService;
        this.authService = authService;
        this.customerService = customerService;
        this.transactionService = transactionService;
    }

    private AccountResponse selectedAccount = null;
    private TransactionResponse tempTransaction = null;

    // TODO
    public void run() {
        Scanner scanner = new Scanner(System.in);
        Screen screen = Screen.WELCOME;

        //  Main loop: login → menu → handle commands
        while (screen != Screen.EXIT) {
            switch (screen) {
                case WELCOME:
                    screen = Screen.LOGIN;
                    break;

                case LOGIN:
                    screen = handleAuthentication(scanner);   // returns next screen
                    break;

                case MAIN_MENU:
                    screen = handleMainMenu(scanner);          // returns next screen
                    break;

                case PROFILE_SETTINGS:
                    screen = handleProfileSettings(scanner);
                    break;

                case OPEN_ACCOUNT:
                    screen = handleOpenAccount(scanner);
                    break;

                case ACCOUNT_LIST:
                    screen = handleAccountsList(scanner);
                    break;

                case ACCOUNT_DETAILS:
                    screen = handleAccountDetails(scanner);
                    break;

                case TRANSACTION_HISTORY:
                    screen = handleTransactionHistory(scanner);
                    break;

                case TRANSACTION_RESULT:
                    screen = handleTransactionResult(scanner);
                    break;

                case LOGOUT:
                    screen = handleLogout(scanner);
                    break;

                case EXIT:
                    screen = handleExit();
                    break;
            }
        }
    }

    // TODO
    public Screen handleMainMenu(Scanner scanner) {
        UserContext userContext = sessionManager.getUser();

        xeRenderer.showMainMenu(String.format("%s | %s", userContext.getName(), userContext.getCustomerId()));
        switch (scanner.nextLine()) {
            //ACCOUNTS
            case "1":
                return Screen.ACCOUNT_LIST;
            //OPEN ACCOUNT
            case "2":
                return Screen.OPEN_ACCOUNT;
            //PROFILE SETTINGS
            case "3":
                return Screen.PROFILE_SETTINGS;
            //LOGOUT
            case "4":
                return Screen.LOGOUT;
            //EXIT
            case "5":
                return Screen.EXIT;
            default:
                System.out.println("Invalid option. Choose from the context menu\n");
                scanner.nextLine();
                break;
        }
        return Screen.MAIN_MENU; // loop to this menu until valid input
    }
    public Screen handleAuthentication(Scanner scanner) {
        while (!sessionManager.isLoggedIn()) {
            xeRenderer.showSignInMenu(1, 0, 0);
            switch (scanner.nextLine().trim()) {
                // LOGIN
                case "1":
                    BooleanResponse isLoggedInResponse = handleLogin(scanner);
                    if (!isLoggedInResponse.value()) {
                        System.out.println(isLoggedInResponse.getErrorMessage() + "\n");
                        scanner.nextLine();
                    }
                    break;
                // REGISTER
                case "2":
                    BooleanResponse isRegisteredResponse = handleRegister(scanner);
                    if (!isRegisteredResponse.value()) {
                        System.out.println(isRegisteredResponse.getErrorMessage() + "\n");
                        scanner.nextLine();
                    }
                    break;
                // EXIT
                case "3":
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Choose from the context menu\n");
                    scanner.nextLine();
                    break;
            }
        }
        return Screen.MAIN_MENU; // if quit loop then we already logged in successfully
    }
    public BooleanResponse handleRegister(Scanner scanner) {
        xeRenderer.promptName();
        String regName = scanner.nextLine();
        xeRenderer.promptEmail();
        String regEmail = scanner.nextLine();
        xeRenderer.promptPhone();
        String regPhone = scanner.nextLine();
        xeRenderer.promptPassword();
        String regPassword = scanner.nextLine();

        RegisterCustomerRequest registerCustomerRequest = parser.parseRegister(regName, regEmail, regPhone, regPassword);
        RegisterCustomerResponse registerCustomerResponse = customerService.register(registerCustomerRequest);

        if (!registerCustomerResponse.getOperationStatus().equals("FAILURE")) {
            sessionManager.saveToken(registerCustomerResponse.getSessionToken());
            sessionManager.saveUser(new UserContext(
                    registerCustomerResponse.getCustomerId(),
                    registerCustomerResponse.getName(),
                    registerCustomerResponse.getEmail(),
                    registerCustomerResponse.getPhone(),
                    registerCustomerResponse.getStatus()
            ));
            return new BooleanResponse(true, null);
        }
        return new BooleanResponse(false, registerCustomerResponse.getErrorMessage());
    }
    public BooleanResponse handleLogin(Scanner scanner) {
        xeRenderer.promptEmail();
        String loginEmail = scanner.nextLine();
        xeRenderer.promptPassword();
        String loginPassword = scanner.nextLine();

        //it returns LoginResponse -> maybe it's better to just get a CustomerResponse instead
        LogInRequest logInRequest = parser.parseLogin(loginEmail, loginPassword);
        LogInResponse logInResponse = authService.login(logInRequest);

        if (!logInResponse.getOperationStatus().equals("FAILURE")) {
            sessionManager.saveToken(logInResponse.getSessionToken());
            sessionManager.saveUser(new UserContext(
                    logInResponse.getCustomerId(),
                    logInResponse.getName(),
                    logInResponse.getEmail(),
                    logInResponse.getPhone(),
                    logInResponse.getStatus()
            ));
            return new BooleanResponse(true, null);
        }
        return new BooleanResponse(false, logInResponse.getErrorMessage());
    }
    public Screen handleLogout(Scanner scanner) {
        outer: while (true) {
            //  Calls authService.logout(), clears session
            String sessionToken = sessionManager.getToken();

            xeRenderer.showLogOut();
            switch (scanner.nextLine().trim()) {
                //YES
                case "1":
                    LogOutRequest request = parser.parseLogout(sessionToken);
                    BooleanResponse isLoggedOutResponse = authService.logout(request);

                    // true if auth service did log out with no exceptions
                    if (isLoggedOutResponse.value()) {
                        try {
                            sessionManager.clearSession();
                            xeRenderer.showLoggedOut();
                            break outer;

                        } catch (RuntimeException e) {
                            System.out.println(e.getMessage() + "\n");
                            scanner.nextLine();
                            return Screen.LOGOUT;
                        }
                    }
                    // breaks loop if session files already cleared
                    if (!sessionManager.isLoggedIn()) {
                        break outer;
                    }
                    break;
                //BACK
                case "b":
                    return Screen.MAIN_MENU;
                default:
                    System.out.println("Invalid option. Choose from the context menu\n");
                    scanner.nextLine();
                    return Screen.LOGOUT;
            }
        }
        return Screen.WELCOME;
    }

    // TODO
    public Screen handleAccountsList(Scanner scanner) {
        UserContext userContext = sessionManager.getUser();
        String sessionToken = sessionManager.getToken();

        GetAccountsRequest request = parser.parseGetAccounts(sessionToken);
        AccountListResponse response = accountService.getAccountsByCustomer(request);
        if (response.getOperationStatus().equals("FAILURE")) {
            System.out.println(response.getErrorMessage() + "\n");
            scanner.nextLine();

            return Screen.MAIN_MENU;
        }
        List<AccountResponse> accounts = response.getAccountResponses();

        //ACCOUNTS MENU
        //no while loop because we return to this menu using state machine now
        xeRenderer.showAccountsMenu(response);
        String option = scanner.nextLine().trim();

        if (option.equals("m")) return Screen.MAIN_MENU;
        try {
            int index = Integer.parseInt(option);
            if (index < 0 || index >= accounts.size()) { throw new NumberFormatException(); }
            selectedAccount = accounts.get(index); // Persists even if Exception caught, so it's safe to return to other menu and back to this again

            return Screen.ACCOUNT_DETAILS;
        }
        catch (NumberFormatException e) {
            System.out.println("Invalid option\n");

            return Screen.ACCOUNT_LIST;
        }
    }
    public Screen handleOpenAccount(Scanner scanner) {
        xeRenderer.promptCurrency();
        switch (scanner.nextLine().trim()) {
            //USD
            case "1":
                handleOpenAccountWithCurrency("USD");
                break;
            //EUR
            case "2":
                handleOpenAccountWithCurrency("EUR");
                break;
            //RUB
            case "3":
                handleOpenAccountWithCurrency("RUB");
                break;
            //GBP
            case "4":
                handleOpenAccountWithCurrency("GBP");
                break;
            //MENU
            case "m":
                return Screen.MAIN_MENU;
            default:
                System.out.println("Invalid option. Choose from the context menu\n");
                scanner.nextLine();
                break;
        }

        return Screen.MAIN_MENU;
    }
    private Screen handleOpenAccountWithCurrency(String currency) {
        String sessionToken = sessionManager.getToken();

        OpenAccountRequest request = parser.parseOpenAccount(sessionToken, currency);
        AccountResponse response = accountService.openAccount(request);

        selectedAccount = response;

        return Screen.ACCOUNT_DETAILS;
    }
    public Screen handleCloseAccount(Scanner scanner) {
        //  Gets accountId, calls accountService.closeAccount()
        AccountResponse oldSelectedAccount = selectedAccount;

        if (selectedAccount.getStatus().equals("BLOCKED") || selectedAccount.getStatus().equals("FROZEN")) {
            System.out.println("Cannot close " + selectedAccount.getStatus() + " account\n");
            scanner.nextLine();

            return Screen.ACCOUNT_DETAILS;
        }

        CloseAccountRequest request = parser.parseCloseAccount(sessionManager.getToken(), selectedAccount.getAccountId());
        AccountStatusResponse response = accountService.closeAccount(request);

        if (response.getOperationStatus().equals("FAILURE")) {
            System.out.println(response.getErrorMessage() + "\n");
            scanner.nextLine();

            return Screen.ACCOUNT_DETAILS;
        }

        selectedAccount = new AccountResponse(
                oldSelectedAccount.getAccountId(),
                oldSelectedAccount.getCustomerId(),
                response.getStatus(),
                oldSelectedAccount.getCurrency(),
                oldSelectedAccount.getBalance(),
                oldSelectedAccount.getOperationStatus(),
                oldSelectedAccount.getErrorMessage()
        );

        return Screen.MAIN_MENU;
    }
    public Screen handleFreezeUnfreeze(Scanner scanner) {
        if (selectedAccount.getStatus().equals("FROZEN")) {
            return handleUnfreezeAccount(scanner);
        }
        if (selectedAccount.getStatus().equals("ACTIVE")) {
            return handleFreezeAccount(scanner);
        }

        System.out.println("Cannot change account status from " + selectedAccount.getStatus());
        scanner.nextLine();

        return Screen.ACCOUNT_DETAILS;
    }
    public Screen handleFreezeAccount(Scanner scanner) {
        AccountResponse oldSelectedAccount = selectedAccount;

        FreezeAccountRequest request = parser.parseFreezeAccount(sessionManager.getToken(), selectedAccount.getAccountId());
        AccountStatusResponse response = accountService.freezeAccount(request);

        if (response.getOperationStatus().equals("FAILURE")) {
            System.out.println(response.getErrorMessage() + "\n");
            scanner.nextLine();

            return Screen.ACCOUNT_DETAILS;
        }

        selectedAccount = new AccountResponse(
                oldSelectedAccount.getAccountId(),
                oldSelectedAccount.getCustomerId(),
                response.getStatus(),
                oldSelectedAccount.getCurrency(),
                oldSelectedAccount.getBalance(),
                oldSelectedAccount.getOperationStatus(),
                oldSelectedAccount.getErrorMessage()
        );

        return Screen.MAIN_MENU;
    }
    public Screen handleUnfreezeAccount(Scanner scanner) {
        AccountResponse oldSelectedAccount = selectedAccount;

        UnfreezeAccountRequest request = parser.parseUnfreezeAccount(sessionManager.getToken(), selectedAccount.getAccountId());
        AccountStatusResponse response = accountService.unfreezeAccount(request);

        if (response.getOperationStatus().equals("FAILURE")) {
            System.out.println(response.getErrorMessage() + "\n");
            scanner.nextLine();

            return Screen.ACCOUNT_DETAILS;
        }

        selectedAccount = new AccountResponse(
                oldSelectedAccount.getAccountId(),
                oldSelectedAccount.getCustomerId(),
                response.getStatus(),
                oldSelectedAccount.getCurrency(),
                oldSelectedAccount.getBalance(),
                oldSelectedAccount.getOperationStatus(),
                oldSelectedAccount.getErrorMessage()
        );

        return Screen.MAIN_MENU;
    }
    public Screen handleAccountDetails(Scanner scanner) {
        xeRenderer.showAccountSettings(selectedAccount);
        switch (scanner.nextLine()) {
            //DEPOSIT
            case "1":
                handleDeposit(scanner);
            //WITHDRAW
            case "2":
                handleWithdraw(scanner);
            //TRANSFER
            case "3":
                handleTransfer(scanner);
            //TRANSACTION HISTORY
            case "4":
                handleTransactionHistory(scanner);
            //FREEZE/UNFREEZE
            case "5":
                handleFreezeUnfreeze(scanner);
            //CLOSE ACCOUNT
            case "6":
                handleCloseAccount(scanner);
            //BACK
            case "b":
                return Screen.ACCOUNT_LIST;
            //MAIN
            case "m":
                return Screen.MAIN_MENU;
            default:
                System.out.println("Invalid option\n");
                break;
        }
        return Screen.ACCOUNT_DETAILS; //stay until valid choice
    }
    /*public Screen handleBalance() {
        //  Gets accountId, calls accountService.getBalance()
        return Screen.MAIN_MENU;
    }*/

    // TODO
    public Screen handleDeposit(Scanner scanner) {
        //  Gets accountId + amount, calls accountService.deposit(), shows response
        String sessionToken = sessionManager.getToken();

        xeRenderer.promptAmount();
        String amount = scanner.nextLine().trim();
        DepositRequest depositRequest = parser.parseDeposit(
                sessionToken,
                selectedAccount.getAccountId(),
                amount,
                selectedAccount.getCurrency()
        );
        tempTransaction = accountService.deposit(depositRequest); // saved for next screen

        //don't need to validate. if exception occurs we are going to see it in transaction detals
        return Screen.TRANSACTION_RESULT;
    }
    public Screen handleWithdraw(Scanner scanner) {
        String sessionToken = sessionManager.getToken();

        xeRenderer.promptAmount();
        String amount = scanner.nextLine().trim();
        WithdrawRequest withdrawRequest = parser.parseWithdraw(
                sessionToken,
                selectedAccount.getAccountId(),
                amount,
                selectedAccount.getCurrency()
        );
        tempTransaction = accountService.withdraw(withdrawRequest); // saved for next screen

        //don't need to validate. if exception occurs we are going to see it in transaction details
        return Screen.TRANSACTION_RESULT;
    }
    public Screen handleTransfer(Scanner scanner) {
        //  Gets sourceId + targetId + amount
        String sessionToken = sessionManager.getToken();

        xeRenderer.promptAmount();
        String amount = scanner.nextLine().trim();
        xeRenderer.promptTargetAccount();
        String targetAccountId = scanner.nextLine().trim();
        TransferRequest transferRequest = parser.parseTransfer(
                sessionToken,
                selectedAccount.getAccountId(),
                targetAccountId,
                amount,
                selectedAccount.getCurrency()
        );
        tempTransaction = accountService.transfer(transferRequest); // saved for next screen

        //don't need to validate. if exception occurs we are going to see it in transaction details
        return Screen.TRANSACTION_RESULT;
    }
    public Screen handleTransactionResult(Scanner scanner) {
        xeRenderer.showTransaction(tempTransaction);
        String option = scanner.nextLine().trim();

        if (option.equals("b")) {
            return Screen.ACCOUNT_DETAILS;
        }
        if (option.equals("m")) {
            return Screen.MAIN_MENU;
        }

        System.out.println("Invalid option. Choose from the context menu\n");
        scanner.nextLine();

        return Screen.TRANSACTION_RESULT;
    }
    public Screen handleTransactionHistory(Scanner scanner) {
        //  Gets accountId, calls transactionService.getTransactionHistory()
        String sessionToken = sessionManager.getToken();

        xeRenderer.promptFromDate();
        String fromDate = scanner.nextLine();
        xeRenderer.promptToDate();
        String toDate = scanner.nextLine();

        TransactionHistoryRequest request = parser.parseTransactionHistory(
                sessionToken,
                selectedAccount.getAccountId(),
                fromDate,
                toDate
        );
        TransactionHistoryResponse response = transactionService.getTransactionHistory(request);
        if (response.getOperationStatus().equals("FAILURE")) {
            System.out.println(response.getErrorMessage() + "\n");
            scanner.nextLine();

            return Screen.ACCOUNT_DETAILS;
        }

        xeRenderer.showTransactionHistory(response);
        String option = scanner.nextLine();

        if (option.equals("b")) return Screen.ACCOUNT_DETAILS;
        if (option.equals("m")) return Screen.MAIN_MENU;

        try {
            int index = Integer.parseInt(option);
            if (index < 0 || index >= response.getTransactions().size()) {
                throw new NumberFormatException();
            }
            TransactionSummaryResponse summaryResponse = response.getTransactions().get(index);
            TransactionDetailsRequest detailsRequest = parser.parseTransactionDetails(sessionToken, selectedAccount.getAccountId(), summaryResponse.getTransactionId());
            tempTransaction = transactionService.getTransactionDetails(detailsRequest);

            return Screen.TRANSACTION_RESULT;

        } catch (NumberFormatException e) {
            System.out.println("Invalid option\n");

            return Screen.TRANSACTION_HISTORY;
        }
    }

    // TODO
    public Screen handleProfileSettings(Scanner scanner) {
        while (true) {
            xeRenderer.showProfileSettings();
            switch (scanner.nextLine().trim()) {
                //CHANGE NAME
                case "1":
                    handleChangeName(scanner);
                //CHANGE EMAIL
                case "2":
                    handleChangeEmail(scanner);
                //CHANGE PHONE
                case "4":
                    handleChangePhone(scanner);
                //CHANGE PASSWORD
                case "5":
                    handleChangePassword(scanner);
                //MAIN
                case "m":
                    return Screen.MAIN_MENU;
                default:
                    System.out.println("Invalid option\n");
                    scanner.nextLine();
                    break;
            }
        }
    }
    public Screen handleChangeName(Scanner scanner) {
        UserContext userContext = sessionManager.getUser();
        String sessionToken = sessionManager.getToken();

        while (true) {
            xeRenderer.promptName();
            String input = scanner.nextLine();

            // loop until valid input
            if (input.isBlank() || input.trim().length() <= 3 || input == null) {
                System.out.println("Name is too short\n");
                scanner.nextLine();

            } else if (input.length() > 53) {
                System.out.println("Name is too long\n");
                scanner.nextLine();

            } else if (input.equals(userContext.getName())) {
                System.out.println("New name cannot be identical to old name\n");
                scanner.nextLine();

            } else {
                // process valid input
                ChangeNameRequest request = parser.parseChangeName(sessionToken, userContext.getName(), input);
                ChangeNameResponse response = customerService.changeName(request);

                if (response.getOperationStatus().equals("FAILURE")) {
                    System.out.println(response.getErrorMessage() + "\n");
                    scanner.nextLine();
                    //loop over showing err

                } else {
                    sessionManager.saveUser(new UserContext(
                            userContext.getCustomerId(),
                            response.getName(),
                            userContext.getEmail(),
                            userContext.getPhone(),
                            userContext.getStatus()
                    ));
                    break;
                }
            }
        }
        return Screen.MAIN_MENU;
    }
    public Screen handleChangeEmail(Scanner scanner) {
        UserContext userContext = sessionManager.getUser();
        String sessionToken = sessionManager.getToken();

        while (true) {
            xeRenderer.promptNewEmail();
            String input = scanner.nextLine();

            if (input.isBlank() || input.trim().length() <= 5 || input == null) {
                System.out.println("Email address is too short\n");
                scanner.nextLine();

            } else if (input.length() > 345) {
                System.out.println("Email address is too long\n");
                scanner.nextLine();

            } else if (input.equals(userContext.getEmail())) {
                System.out.println("New email cannot be identical to old email address\n");
                scanner.nextLine();

            } else {
                ChangeEmailRequest request = parser.parseChangeEmail(sessionToken, userContext.getEmail(), input);
                ChangeEmailResponse response = customerService.changeEmail(request);

                if (response.getOperationStatus().equals("FAILURE")) {
                    System.out.println(response.getErrorMessage() + "\n");
                    scanner.nextLine();

                } else {
                    sessionManager.saveUser(new UserContext(
                            userContext.getCustomerId(),
                            userContext.getName(),
                            response.getEmail(),
                            userContext.getPhone(),
                            userContext.getStatus()
                    ));
                    break;
                }
            }
        }
        return Screen.MAIN_MENU;
    }
    public Screen handleChangePassword(Scanner scanner) {
        String sessionToken = sessionManager.getToken();

        while (true) {
            xeRenderer.promptPassword();
            String oldPwd = scanner.nextLine();
            xeRenderer.promptNewPassword();
            String newPwd = scanner.nextLine();

            if (oldPwd.isBlank() || oldPwd == null || oldPwd.length() > 50) {
                System.out.println("Please enter your valid old password\n");
                scanner.nextLine();

            } else if (newPwd.isBlank() || oldPwd == null || oldPwd.length() > 50) {
                System.out.println("Please enter your valid new password\n");
                scanner.nextLine();

            } else {
                ChangePasswordRequest request = parser.parseChangePassword(sessionToken, oldPwd, newPwd);
                ChangePasswordResponse response = customerService.changePassword(request);

                if (response.getOperationStatus().equals("FAILURE")) {
                    System.out.println(response.getErrorMessage() + "\n");
                    scanner.nextLine();

                } else {
                    xeRenderer.showSuccess("Password has been changed");
                    scanner.nextLine();
                    break;
                }
            }
        }
        return Screen.MAIN_MENU;
    }
    public Screen handleChangePhone(Scanner scanner) {
        UserContext userContext = sessionManager.getUser();
        String sessionToken = sessionManager.getToken();

        while (true) {
            xeRenderer.promptPhone();
            String input = scanner.nextLine();

            if (input.isBlank() || input.trim().length() <= 7) {
                System.out.println("Phone is too short\n");
                scanner.nextLine();

            } else if (input.length() >= 16) {
                System.out.println("Phone is too long\n");
                scanner.nextLine();

            } else if (input.equals(userContext.getPhone())) {
                System.out.println("New phone cannot be identical to old phone\n");
                scanner.nextLine();

            } else {
                ChangePhoneNumberRequest request = parser.parseChangePhone(sessionToken, userContext.getPhone(), input);
                ChangePhoneNumberResponse response = customerService.changePhone(request);

                if (response.getOperationStatus().equals("FAILURE")) {
                    System.out.println(response.getErrorMessage() + "\n");
                    scanner.nextLine();
                } else {
                    sessionManager.saveUser(new UserContext(
                            userContext.getCustomerId(),
                            userContext.getName(),
                            userContext.getEmail(),
                            response.getPhone(),
                            userContext.getStatus()
                    ));
                    break;
                }
            }
        }
        return Screen.MAIN_MENU;
    }

    // TODO
    public Screen handleExit() {
        xeRenderer.showGoodbye();
        try {
            System.exit(0);
        } finally {
            return Screen.EXIT;
        }
    }
}
