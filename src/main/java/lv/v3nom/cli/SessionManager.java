package lv.v3nom.cli;

import lv.v3nom.cli.impl.UserContext;

import java.util.Optional;

public interface SessionManager {
    // should manage the .session cookies
    // declare the path to storage file -> save to variable as Path sessionFile ...

    void saveToken(String token); // write currentToken to file
    String getToken(); // return if currentToken in memory, otherwise readString from sessionFile
    void clearSession(); // delete currentToken from file
    boolean isLoggedIn(); // getToken, if not null then true
    void saveUser(UserContext userContext);
    UserContext getUser();
}
