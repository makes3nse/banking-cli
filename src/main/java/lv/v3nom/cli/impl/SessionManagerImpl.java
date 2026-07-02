package lv.v3nom.cli.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SessionManagerImpl {
    private final Path sessionFile = Paths.get(System.getProperty("user.dir"), ".session");
    private String currentToken;

    public void saveToken(String token) {
        try {
            Files.writeString(sessionFile, token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.currentToken = token;
    }
}
