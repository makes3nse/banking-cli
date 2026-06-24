package lv.v3nom.infrastructure.repository.INMEM.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lv.v3nom.domain.exception.TransactionNotFoundException;
import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.model.Transaction;
import lv.v3nom.domain.value.AccountId;
import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.domain.value.TransactionId;
import lv.v3nom.domain.value.TransactionStatus;
import lv.v3nom.infrastructure.repository.INMEM.TransactionRepository;

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

public class TransactionRepositoryImpl implements TransactionRepository {
    private final Map<TransactionId, Transaction> transactionDatabase = new HashMap<>();
    private final Path storageFile = Paths.get("data", "transactions.json");
    private final Gson gson = new Gson();

    public TransactionRepositoryImpl() {
        loadFromFile();
    }

    private void loadFromFile() {
        if (!Files.exists(storageFile)) return;
        try (Reader reader = Files.newBufferedReader(storageFile)) {
            Type type = new TypeToken<Map<String, Transaction>>() {}.getType();
            Map<String, Transaction> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                loaded.forEach((idString, transaction) ->
                        transactionDatabase.put(TransactionId.of(idString), transaction)
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load transactions", e);
        }
    }
    private void saveToFile() {
        try {
            Files.createDirectories(storageFile.getParent());
            try (Writer writer = Files.newBufferedWriter(storageFile)) {
                Map<String, Transaction> toSave = new HashMap<>();
                transactionDatabase.forEach((transactionId, transaction) ->
                        toSave.put(transactionId.toString(), transaction)
                );
                gson.toJson(toSave, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save transactions", e);
        }
    }

    public void save(Transaction transaction) {
        transactionDatabase.put(transaction.getTransactionId(), transaction);
        saveToFile();
    }
    public Transaction findById(TransactionId transactionId) {
        Transaction transaction = transactionDatabase.get(transactionId);
        if (transaction == null) {
            throw new TransactionNotFoundException(transactionId);
        }
        return transaction;
    }
    public List<Transaction> findAll() {
        return List.copyOf(transactionDatabase.values());
    }
    public List<Transaction> findByAccountId(AccountId accountId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactionDatabase.values()) {
            if (transaction.getSourceAccount().equals(accountId) ||
                    transaction.getTargetAccount().equals(accountId)) {
                result.add(transaction);
            }
        }
        return result;
    }
    public List<Transaction> findByStatus(TransactionStatus transactionStatus) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactionDatabase.values()) {
            if (transaction.getTransactionStatus().equals(transactionStatus)) {
                result.add(transaction);
            }
        }
        return result;
    }
    public List<Transaction> findByDateRange(LocalDateTime from, LocalDateTime to) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactionDatabase.values()) {
            LocalDateTime createdAt = transaction.getCreatedAt();

            if ((createdAt.isEqual(from) || createdAt.isAfter(from))
                && (createdAt.isEqual(to) || createdAt.isBefore(to))) {
                result.add(transaction);
            }
        }
        return result;
    }
    public List<Transaction> findByDateRangeForAccountId(AccountId accountId,
                                                         LocalDateTime from,
                                                         LocalDateTime to) {

        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactionDatabase.values()) {
            LocalDateTime createdAt = transaction.getCreatedAt();

            if (transaction.getSourceAccount() == accountId
                    || transaction.getTargetAccount() == accountId) {
                if ((createdAt.isEqual(from) || createdAt.isAfter(from))
                        && (createdAt.isEqual(to) || createdAt.isBefore(to))) {
                    result.add(transaction);
                }
            }
        }
        return result;
    }
}
