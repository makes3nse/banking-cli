package lv.v3nom.infrastructure.repository.INMEM;

import lv.v3nom.domain.model.Transaction;
import lv.v3nom.domain.value.AccountId;
import lv.v3nom.domain.value.TransactionId;
import lv.v3nom.domain.value.TransactionStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {
    public void save(Transaction transaction);
    public Transaction findById(TransactionId transactionId);
    public List<Transaction> findAll();
    public List<Transaction> findByAccountId(AccountId accountId);
    public List<Transaction> findByStatus(TransactionStatus transactionStatus);
    public List<Transaction> findByDateRange(LocalDateTime from, LocalDateTime to);
}
