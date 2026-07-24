package lv.v3nom.infrastructure.security;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lv.v3nom.application.dto.storage.TokenStorageDTO;
import lv.v3nom.application.mapper.StorageMapper;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenStore {
    private final Map<String, Token> validTokens = new ConcurrentHashMap<>();
    private final Path storageFile = Paths.get("data", "tokens.json");
    private final Gson gson;

    public TokenStore(Gson gson) {
        this.gson = gson;
        loadFromFile();
    }

    private void loadFromFile() {
        try {
            if (!Files.exists(storageFile) || Files.size(storageFile) == 0) {
                System.out.println("No tokens data found.");
                return;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        try (Reader reader = Files.newBufferedReader(storageFile)) {
            Type listType = new TypeToken<List<TokenStorageDTO>>() {}.getType();
            List<TokenStorageDTO> loaded = gson.fromJson(reader, listType);

            if (loaded != null) {
                for (TokenStorageDTO token : loaded) {
                    Token tokenReconstructed = StorageMapper.fromStorageToken(token);
                    validTokens.put(tokenReconstructed.getValue(), tokenReconstructed);
                }
                System.out.println("Loaded " + loaded.size() + " tokens from file.");
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load tokens ", e);

        } catch (JsonSyntaxException e) {
            System.err.println("Token file is corrupted. Starting with empty token store.");

            try {
                Path backup = storageFile.getParent().resolve("tokens_backup_" + System.currentTimeMillis() + ".json");
                Files.copy(storageFile, backup);
                Files.delete(storageFile);
                System.err.println("Corrupted token file backed up to: " + backup);

            } catch (IOException ioException) {
                System.err.println("Failed to backup corrupted token file: " + ioException.getMessage());
            }
        }
    }

    private void saveToFile() {
        try {
            Files.createDirectories(storageFile.getParent());
            try (Writer writer = Files.newBufferedWriter(storageFile)) {
                List<Token> tokens = new ArrayList<>(validTokens.values());
                List<TokenStorageDTO> tDTOs = new ArrayList<>();
                for (Token t : tokens) {
                    tDTOs.add(StorageMapper.toStorageToken(t));
                }

                System.out.println("Saving " + tokens.size() + " tokens to file.");
                System.out.println("TknStore: saveToFile() -> List<Token> toSave = " + tokens);

                gson.toJson(tDTOs, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save tokens", e);
        }
    }

    public void store(Token token) {
        System.out.println("Storing token for customer: " + token.getCustomerId().getValue());
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
        int before = validTokens.size();
        validTokens.entrySet().removeIf(entry -> !entry.getValue().isValid(now));
        if (before != validTokens.size()) {
            System.out.println("Cleaned " + (before - validTokens.size()) + " expired tokens.");
            saveToFile();
        }
    }
}
