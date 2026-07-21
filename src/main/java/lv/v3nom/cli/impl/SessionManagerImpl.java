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
    private volatile UserContext user;

    public SessionManagerImpl(AuthService authService, Gson gson) {
        this.authService = authService;
        this.gson = gson;
    }

    @Override
    public void saveToken(String token) {
        try {
            Files.writeString(sessionFile, token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.currentToken = token;
    }
    @Override
    public void saveUser(UserContext user) {
        try {
            Files.writeString(userFile, gson.toJson(user));
        } catch (IOException | JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
        this.user = Objects.requireNonNull(user);
    }
    @Override
    public String getToken() {
        if (currentToken != null) return currentToken;
        if (Files.exists(sessionFile)) {
            try {
                currentToken = Files.readString(sessionFile).trim();
                return currentToken;

            } catch (IOException e) {
                currentToken = null;
                throw new RuntimeException(e);
            }
        }
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
        if (token == null || user == null) return false;
        BooleanResponse isValidToken = authService.validateToken(
                new ValidateTokenRequest(token)
        );
        return isValidToken.value();
    }
    @Override
    public UserContext getUser() {
        if (user != null) return user;
        if (Files.exists(userFile)) {
            try {
                user = gson.fromJson(Files.readString(userFile), UserContext.class);
                return user;

            } catch (IOException | JsonSyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
