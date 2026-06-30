package lv.v3nom.cli;

public interface SessionManager {
    // should manage the .session cookies
    // declare the path to storage file -> save to variable as Path sessionFile ...

    void saveToken(String token); // write currentToken to file
    String getToken(); // return if currentToken in memory, otherwise readString from sessionFile
    void clearSession(); // delete currentToken from file
    boolean isLoggedIn(); // getToken, if not null then true
}
