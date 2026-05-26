package lv.v3nom.infrastructure.repository.INMEM;

import lv.v3nom.domain.model.Account;
import lv.v3nom.domain.value.AccountId;
import lv.v3nom.domain.value.CustomerId;

import java.util.List;

public interface AccountRepository {
    public void save(Account account);
    public Account findById(AccountId accountId);
    public List<Account> findAll();
    public List<Account> findByCustomerId(CustomerId customerId);
}
