package lv.v3nom.cli.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lv.v3nom.application.dto.requests.ValidateTokenRequest;
import lv.v3nom.application.dto.responses.BooleanResponse;
import lv.v3nom.application.service.AuthService;
import lv.v3nom.cli.SessionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class SessionManagerImpl implements SessionManager {
    private final AuthService authService;
    private final Gson gson;
    private final Path sessionFile = Paths.get(System.getProperty("user.dir"), ".session");
    private final Path userFile = Paths.get(System.getProperty("user.dir"), ".user");
    private String currentToken;
    private UserContext user;

    public SessionManagerImpl(AuthService authService, Gson gson) {
        this.authService = authService;
        this.gson = gson;
    }

    @Override
    public void saveToken(String token) {
        try {
            Files.writeString(sessionFile, token);
            System.out.println("SesMgr: saveToken() -> saved " + token);
        } catch (IOException e) {
            System.out.println("SesMgr: saveToken() -> failed " + e.getMessage());
            throw new RuntimeException(e);
        }
        this.currentToken = token;
    }
    @Override
    public void saveUser(UserContext user) {
        try {
            Files.writeString(userFile, gson.toJson(user));
            System.out.println("SesMgr: saveUser() -> saved " + user.getCustomerId() + " | " + user.getEmail());
        } catch (IOException | JsonSyntaxException e) {
            System.out.println("SesMgr: saveUser() -> failed " + e.getMessage());
            throw new RuntimeException(e);
        }
        this.user = Objects.requireNonNull(user);
    }
    @Override
    public String getToken() {
        if (currentToken != null) {
            System.out.println("SesMgr: getToken() -> token: " + currentToken);
            return currentToken;
        }
        if (Files.exists(sessionFile)) {
            try {
                currentToken = Files.readString(sessionFile).trim();
                System.out.println("SesMgr: getToken() -> token: " + currentToken);
                return currentToken;

            } catch (IOException e) {
                currentToken = null;
                System.out.println("SesMgr: getToken() -> token: " + currentToken);
                throw new RuntimeException(e);
            }
        }
        System.out.println("SesMgr: getToken() -> token: " + currentToken);
        return null;
    }
    @Override
    public void clearSession() {
        try {
            Files.deleteIfExists(sessionFile);
            Files.deleteIfExists(userFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        currentToken = null;
        user = null;
    }
    @Override
    public boolean isLoggedIn() {
        String token = getToken();
        UserContext user = getUser();

        if (token == null || user == null) return false;
        BooleanResponse isValidToken = authService.validateToken(
                new ValidateTokenRequest(token)
        );
        return isValidToken.value();
    }
    @Override
    public UserContext getUser() {
        if (user != null) {
            System.out.println("SesMgr: getUser() -> user: " + user);
            return user;
        }
        if (Files.exists(userFile)) {
            try {
                user = gson.fromJson(Files.readString(userFile), UserContext.class);
                System.out.println("SesMgr: getUser() -> user: " + user);
                return user;

            } catch (IOException | JsonSyntaxException e) {
                System.out.println("SesMgr: getUser() -> user: " + user);
                throw new RuntimeException(e);
            }
        }
        System.out.println("SesMgr: getUser() -> user: " + user);
        return null;
    }
}
