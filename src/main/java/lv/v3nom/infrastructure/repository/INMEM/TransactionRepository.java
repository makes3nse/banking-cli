package lv.v3nom.infrastructure.repository.INMEM;

import lv.v3nom.domain.model.Transaction;
import lv.v3nom.domain.value.AccountId;
import lv.v3nom.domain.value.TransactionId;
import lv.v3nom.domain.value.TransactionStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository {
    void save(Transaction transaction);
    Transaction findById(TransactionId transactionId);
    List<Transaction> findAll();
    List<Transaction> findByAccountId(AccountId accountId);
    List<Transaction> findByStatus(TransactionStatus transactionStatus);
    List<Transaction> findByDateRange(LocalDateTime from, LocalDateTime to);
    List<Transaction> findByDateRangeForAccountId(AccountId accountId, LocalDateTime from, LocalDateTime to);
}
