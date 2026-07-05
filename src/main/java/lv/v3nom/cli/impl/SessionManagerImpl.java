package lv.v3nom.cli.impl;

import lv.v3nom.application.dto.requests.ValidateTokenRequest;
import lv.v3nom.application.dto.responses.BooleanResponse;
import lv.v3nom.application.service.AuthService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SessionManagerImpl {
    private final AuthService authService;
    private final Path sessionFile = Paths.get(System.getProperty("user.dir"), ".session");
    private String currentToken;

    public SessionManagerImpl(AuthService authService) {
        this.authService = authService;
    }

    public void saveToken(String token) {
        try {
            Files.writeString(sessionFile, token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.currentToken = token;
    }
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
    public void clearSession() {
        try {
            Files.deleteIfExists(sessionFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        currentToken = null;
    }
    public boolean isLoggedIn() {
        String token = getToken();
        if (token == null) return false;
        BooleanResponse isValidToken = authService.validateToken(
                new ValidateTokenRequest(token)
        );
        return isValidToken.value();
    }
}
