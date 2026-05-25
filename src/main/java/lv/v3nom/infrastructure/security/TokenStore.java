package lv.v3nom.infrastructure.security;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.domain.value.Token;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenStore {
    private final Map<String, Token> validTokens = new ConcurrentHashMap<>();
    private final Path storageFile = Paths.get("data", "tokens.json");
    private final Gson gson = new Gson();

    public TokenStore() {
        loadFromFile();
    }

    private void loadFromFile() {
        if (!Files.exists(storageFile)) return;
        try (Reader reader = Files.newBufferedReader(storageFile)) {
            Type type = new TypeToken<Map<String, Token>>() {
            }.getType();
            Map<String, Token> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                loaded.forEach((tokenValue, token) -> validTokens.put(tokenValue, token));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load tokens ", e);
        }
    }
    private void saveToFile() {
        try {
            Files.createDirectories(storageFile.getParent());
            try (Writer writer = Files.newBufferedWriter(storageFile)) {
                Map<String, Token> toSave = new HashMap<>();
                validTokens.forEach((tokenValue, token) -> toSave.put(tokenValue, token));
                gson.toJson(toSave, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void store(Token token) {
        validTokens.put(token.getValue(), token);
        saveToFile();
    }
    public boolean isValid(String tokenValue, LocalDateTime now) {
        cleanExpired(now);
        Token token = validTokens.get(tokenValue);
        return token != null && token.isValid(now);
    }
    public CustomerId getCustomerId(String tokenValue) {
        Token token = validTokens.get(tokenValue);
        return token != null ? token.getCustomerId() : null;
    }
    public void invalidate(String tokenValue) {
        validTokens.remove(tokenValue);
    }
    public void cleanExpired(LocalDateTime now) {
        validTokens.entrySet().removeIf(entry -> !entry.getValue().isValid(now));
    }
}
