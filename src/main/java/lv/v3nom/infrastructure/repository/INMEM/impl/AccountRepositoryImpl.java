package lv.v3nom.infrastructure.repository.INMEM.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lv.v3nom.application.dto.storage.AccountStorageDTO;
import lv.v3nom.application.mapper.StorageMapper;
import lv.v3nom.domain.exception.AccountNotFoundException;
import lv.v3nom.domain.exception.CustomerNotFoundException;
import lv.v3nom.domain.model.Account;
import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.value.AccountId;
import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.infrastructure.repository.INMEM.AccountRepository;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountRepositoryImpl implements AccountRepository {
    private final Map<AccountId, Account> accountDatabase = new HashMap<>();
    private final Path storageFile = Paths.get("data", "accounts.json");
    private final Gson gson;

    public AccountRepositoryImpl(Gson gson) {
        this.gson = gson;
        loadFromFile();
    }

    private void loadFromFile() {
        try {
            if (!Files.exists(storageFile) || Files.size(storageFile) == 0) {
                System.out.println("No accounts data found.");
                return;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        try (Reader reader = Files.newBufferedReader(storageFile)) {
            // Load as List, not Map
            Type listType = new TypeToken<List<AccountStorageDTO>>(){}.getType();
            List<AccountStorageDTO> loaded = gson.fromJson(reader, listType);
            if (loaded != null) {
                for (AccountStorageDTO account : loaded) {
                    Account accountReconstructed = StorageMapper.fromStorageAccount(account);
                    accountDatabase.put(accountReconstructed.getAccountId(), accountReconstructed);
                    System.out.println("  Loaded account: " + accountReconstructed.getAccountId().getValue());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load accounts", e);

        } catch (JsonSyntaxException e) {
            System.err.println("Warning: Accounts data file is corrupted. Starting with empty database.");

            try {
                Path backup = storageFile.getParent().resolve("accounts_backup_" + System.currentTimeMillis() + ".json");
                Files.copy(storageFile, backup);
                System.err.println("Corrupted file backed up to: " + backup);
                Files.delete(storageFile);

            } catch (IOException ioException) {
                System.err.println("Failed to backup corrupted file: " + ioException.getMessage());
            }
        }
    }
    private void saveToFile() {
        try {
            Files.createDirectories(storageFile.getParent());
            try (Writer writer = Files.newBufferedWriter(storageFile)) {
                // Save as List
                List<Account> accounts = new ArrayList<>(accountDatabase.values());
                List<AccountStorageDTO> aDTOs = new ArrayList<>();
                for (Account a : accounts) {
                    aDTOs.add(StorageMapper.toStorageAccount(a));
                }

                System.out.println("Saving " + accounts.size() + " accounts to file.");
                System.out.println("AccRepo: saveToFile() -> List<Account> toSave = " + accounts);

                gson.toJson(aDTOs, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save accounts", e);
        }
    }

    public void save(Account account) {
        accountDatabase.put(account.getAccountId(), account);
        saveToFile();
    }
    public Account findById(AccountId accountId) {
        Account account = accountDatabase.get(accountId);
        accountDatabase.forEach((accountId1, account1) ->
                System.out.println(accountId1.getValue() + "_" + account1.getAccountId().getValue()));
        if (account == null) throw new AccountNotFoundException(accountId);
        return account;
    }
    public List<Account> findByCustomerId(CustomerId customerId) {
        List<Account> accounts = new ArrayList<>();
        for (Account a : accountDatabase.values()) {
            if (a.getOwnerId().equals(customerId)) {
                accounts.add(a);
            }
        }
        return accounts;
    }
    public List<Account> findAllCustomerAccountsById(CustomerId customerId) {
        List<Account> accounts = new ArrayList<>();
        for (Account account : accountDatabase.values()) {
            if (account.getOwnerId().equals(customerId)) {
                accounts.add(account);
            }
        }
        return accounts;
    }
    public List<Account> findAll() {
        return List.copyOf(accountDatabase.values());
    }
}
