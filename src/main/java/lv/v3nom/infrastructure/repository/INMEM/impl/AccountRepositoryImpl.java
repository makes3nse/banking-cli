package lv.v3nom.infrastructure.repository.INMEM.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
    private final Gson gson = new Gson();

    public AccountRepositoryImpl() {
        loadFromFile();
    }

    private void loadFromFile() {
        if (!Files.exists(storageFile)) return;
        try (Reader reader = Files.newBufferedReader(storageFile)) {
            Type type = new TypeToken<Map<String, Account>>() {}.getType();
            Map<String, Account> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                loaded.forEach((idString, account) ->
                                accountDatabase.put(AccountId.of(idString), account)
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load accounts", e);
        }
    }
    private void saveToFile() {
        try {
            Files.createDirectories(storageFile.getParent());
            try (Writer writer = Files.newBufferedWriter(storageFile)) {
                Map<String, Account> toSave = new HashMap<>();
                accountDatabase.forEach((accountId, account) ->
                        toSave.put(accountId.toString(), account)
                );
                gson.toJson(toSave, writer);
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
