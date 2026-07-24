package lv.v3nom.infrastructure.repository.INMEM.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lv.v3nom.application.dto.storage.TransactionStorageDTO;
import lv.v3nom.application.mapper.StorageMapper;
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
    private final Gson gson;

    public TransactionRepositoryImpl(Gson gson) {
        this.gson = gson;
        try {
            Files.createDirectories(storageFile.getParent());
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }
        loadFromFile();
    }

    private void loadFromFile() {
        try {
            if (!Files.exists(storageFile) || Files.size(storageFile) == 0) {
                System.out.println("No transactions data found.");
                return;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        try (Reader reader = Files.newBufferedReader(storageFile)) {
            Type listType = new TypeToken<List<TransactionStorageDTO>>() {}.getType();
            List<TransactionStorageDTO> loaded = gson.fromJson(reader, listType);

            if (loaded != null) {
                for (TransactionStorageDTO transaction : loaded) {
                    Transaction transactionReconstructed = StorageMapper.fromStorageTransaction(transaction);
                    transactionDatabase.put(transactionReconstructed.getTransactionId(), transactionReconstructed);
                    System.out.println("  Loaded transaction: " + transactionReconstructed.getTransactionId().getValue());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load transactions", e);

        } catch (JsonSyntaxException e) {
            System.err.println("Warning: Transaction data file is corrupted. Starting with empty database.");

            try {
                Path backup = storageFile.getParent().resolve("transactions_backup_" + System.currentTimeMillis() + ".json");
                Files.copy(storageFile, backup);
                Files.delete(storageFile);  // ← ADD THIS
                System.err.println("Corrupted file backed up and deleted: " + backup);

            } catch (IOException ioException) {
                System.err.println("Failed to backup corrupted file: " + ioException.getMessage());
            }
        }
    }

    private void saveToFile() {
        try {
            Files.createDirectories(storageFile.getParent());
            try (Writer writer = Files.newBufferedWriter(storageFile)) {
                List<Transaction> transactions = new ArrayList<>(transactionDatabase.values());
                List<TransactionStorageDTO> tDTOs = new ArrayList<>();
                for (Transaction t : transactions) {
                    tDTOs.add(StorageMapper.toStorageTransaction(t));
                }

                System.out.println("Saving " + transactions.size() + " transactions to file.");
                System.out.println("TrxRepo: saveToFile() -> List<Transaction> toSave = " + transactions);

                gson.toJson(tDTOs, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save transactions", e);
        }
    }

    public void save(Transaction transaction) {
        System.out.println("TrxRepo: save() -> getTransactionId(): " + transaction.getTransactionId().getValue());
        System.out.println("TrxRepo: save() -> getTransactionType(): " + transaction.getTransactionType().getTransactionName());
        System.out.println("TrxRepo: save() -> getAmount(): " + transaction.getAmount().getValue().toString());
        System.out.println("TrxRepo: save() -> getCurrency(): " + transaction.getCurrency().value());
        System.out.println("TrxRepo: save() -> getCompletedAt(): " + transaction.getCompletedAt());
        System.out.println("TrxRepo: save() -> getCreatedAt(): " + transaction.getCreatedAt());
        System.out.println("TrxRepo: save() -> getSourceAccount(): " + transaction.getSourceAccount().getValue());
        System.out.println("TrxRepo: save() -> getTargetAccount(): " + transaction.getTargetAccount().getValue());
        System.out.println("TrxRepo: save() -> getTransactionStatus(): " + transaction.getTransactionStatus().getValue());
        System.out.println("TrxRepo: save() -> getFailureReason(): " + transaction.getFailureReason());
        System.out.println("TrxRepo: save() -> getRejectReason(): " + transaction.getRejectReason());
        System.out.println("TrxRepo: save() -> getReturnReason(): " + transaction.getReturnReason());

        transactionDatabase.put(transaction.getTransactionId(), transaction);
        saveToFile();
    }
    public Transaction findById(TransactionId transactionId) {
        Transaction transaction = transactionDatabase.get(transactionId);
        System.out.println("Looking for transaction: " + transactionId.getValue());
        System.out.println("Found: " + (transaction != null ? transaction.getTransactionStatus() : "null"));
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

            if (transaction.getSourceAccount().equals(accountId)
                    || transaction.getTargetAccount().equals(accountId)) {
                if ((createdAt.isEqual(from) || createdAt.isAfter(from))
                        && (createdAt.isEqual(to) || createdAt.isBefore(to))) {
                    result.add(transaction);
                }
            }
        }
        return result;
    }
}
