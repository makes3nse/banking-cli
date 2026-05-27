package lv.v3nom.infrastructure.repository.INMEM.impl;

import lv.v3nom.domain.exception.AccountNotFoundException;
import lv.v3nom.domain.exception.CustomerNotFoundException;
import lv.v3nom.domain.model.Account;
import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.value.AccountId;
import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.infrastructure.repository.INMEM.AccountRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountRepositoryImpl implements AccountRepository {
    private final Map<AccountId, Account> accountDatabase = new HashMap<>();

    // TODO implement JSON DB, loadFromFIle(), saveToFile() methods

    public void save(Account account) {
        accountDatabase.put(account.getAccountId(), account);
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
