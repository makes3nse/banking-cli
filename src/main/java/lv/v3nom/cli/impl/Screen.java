package lv.v3nom.cli.impl;

public enum Screen {
    WELCOME,          // initial welcome screen
    LOGIN,            // sign‑in / register
    MAIN_MENU,        // after login
    OPEN_ACCOUNT,
    PROFILE_SETTINGS,
    ACCOUNT_LIST,     // list of accounts
    ACCOUNT_DETAILS,  // details + operations for a chosen account
    TRANSACTION_HISTORY,
    TRANSACTION_RESULT, // show result of deposit/withdraw/etc.
    LOGOUT,
    EXIT              // terminate the app
}
