package lv.v3nom.application.service.impl;

import lv.v3nom.application.dto.requests.DepositRequest;
import lv.v3nom.application.dto.requests.OpenAccountRequest;
import lv.v3nom.application.dto.responses.AccountResponse;
import lv.v3nom.application.dto.responses.TransactionResponse;
import lv.v3nom.application.mapper.AccountMapper;
import lv.v3nom.application.mapper.TransactionMapper;
import lv.v3nom.application.service.AccountService;
import lv.v3nom.domain.model.Account;
import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.model.Transaction;
import lv.v3nom.domain.value.*;
import lv.v3nom.infrastructure.idempotency.IdempotencyStore;
import lv.v3nom.infrastructure.repository.INMEM.AccountRepository;
import lv.v3nom.infrastructure.repository.INMEM.CustomerRepository;
import lv.v3nom.infrastructure.repository.INMEM.TransactionRepository;
import lv.v3nom.infrastructure.repository.INMEM.impl.AccountRepositoryImpl;
import lv.v3nom.infrastructure.repository.INMEM.impl.CustomerRepositoryImpl;
import lv.v3nom.infrastructure.repository.INMEM.impl.TransactionRepositoryImpl;
import lv.v3nom.infrastructure.security.PermissionChecker;
import lv.v3nom.infrastructure.security.TokenStore;
import lv.v3nom.infrastructure.time.impl.SystemDateTimeProvider;

public class AccountServiceImpl implements AccountService {
    private final TokenStore tokenStore;
    private final IdempotencyStore idempotencyStore;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountServiceImpl(TokenStore tokenStore,
                              IdempotencyStore idempotencyStore,
                              CustomerRepository customerRepository,
                              AccountRepository accountRepository,
                              TransactionRepository transactionRepository) {

        this.tokenStore = tokenStore;
        this.idempotencyStore = idempotencyStore;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    //    Validate session token    --> get authenticated CustomerId
    //    Check idempotency         --> return cached if exists
    //    Solve target CustomerId (from DTO)
    //    Authorize                 --> customer can open for self
    //    Create account via entity factory
    //    Save to repo
    //    Cache response
    //    Return response
    @Override
    public AccountResponse openAccount(OpenAccountRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        CustomerId authenticatedId = tokenStore.getCustomerId(request.getCurrentSessionToken());
        if (!tokenStore.isValid(request.getCurrentSessionToken(), time.now())
                || authenticatedId == null) {
            return AccountMapper.failureResponse(authenticatedId.toString(), OperationStatus.FAILURE);
        }

        IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
        Object cachedResponse = idempotencyStore.retrieve(authenticatedId, idempotencyKey);
        if (cachedResponse != null) {
            return (AccountResponse) cachedResponse;
        }

        CustomerId targetCustomerId = CustomerId.of(request.getCustomerId());
        Customer customer = customerRepository.findById(authenticatedId);
        if (!PermissionChecker.hasHighElevatedRights(customer)
                && !authenticatedId.equals(targetCustomerId)) {
            OperationStatus badOperationStatusOfPermissions = OperationStatus.of(
                    "FAILURE",
                    false,
                    String.format(
                            "Cannot open account for other users: %s. Role: %s",
                            targetCustomerId.getValue(), customer.getRole())
            );
            return AccountMapper.failureResponse(targetCustomerId.toString(), badOperationStatusOfPermissions);
        }

        OperationStatus goodOperationStatus = OperationStatus.SUCCESS;
        Account account = AccountMapper.toDomain(request, customer, time.now());
        accountRepository.save(account);
        AccountResponse response = AccountMapper.toResponse(account, goodOperationStatus);
        idempotencyStore.store(authenticatedId, idempotencyKey, response);

        return response;
    }

    //    TODO
    //    Validate token            --> get customer ID
    //    Check idempotency
    //    Load account by CustId
    //    Authorize (customer owns account)
    //    Create transaction (PENDING)
    //    Execute deposit on account entity
    //    Complete transaction
    //    Save account + transaction
    //    Cache response
    //    Return response
    @Override
    public TransactionResponse deposit(DepositRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();
        TransactionType transactionType = TransactionType.DEPOSIT;
        Currency currency = Currency.of(request.getCurrency());
        Money amount = Money.of(request.getAmount(), currency);
        TransactionId transactionId = TransactionId.generate();
        IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());

        CustomerId authenticated = tokenStore.getCustomerId(request.getCurrentSessionToken());
        boolean isValidToken = tokenStore.isValid(request.getCurrentSessionToken(), time.now());
        if (!isValidToken || authenticated == null) {
            String failureReason = String.format(
                    "Token: %s; Validity: %s; User: %s;",
                    request.getCurrentSessionToken(),
                    isValidToken,
                    authenticated
            );

            return TransactionMapper.failureResponse(
                    transactionType,
                    failureReason,
                    OperationStatus.FAILURE);
        }
        Object cachedResponse = idempotencyStore.retrieve(authenticated, idempotencyKey);
        if (cachedResponse != null) {
            return (TransactionResponse) cachedResponse;
        }

        Account account = accountRepository.findById(AccountId.of(request.getAccountId()));
        boolean isExistingAccount = account != null;
        boolean isOwnedByAuthenticatedCustomer = account.getOwnerId().equals(authenticated);
        if (!isExistingAccount || !isOwnedByAuthenticatedCustomer) {
            String failureReason = String.format(
                    "Account: %s; Exists: %s; User: %s;",
                    request.getCurrentSessionToken(),
                    isExistingAccount,
                    authenticated
            );
            return TransactionMapper.failureResponse(
                    transactionType,
                    failureReason,
                    OperationStatus.FAILURE
            );
        }

        Transaction depositTransaction = Transaction.createDepositRecord(
                transactionId,
                amount,
                account.getAccountId(),
                time.now()
        );
        account.deposit(amount, time.now());
        depositTransaction.complete(time.now());
        accountRepository.save(account);
        transactionRepository.save(depositTransaction);
        TransactionResponse response = TransactionMapper.toResponse(depositTransaction, OperationStatus.SUCCESS);
        idempotencyStore.store(authenticated, idempotencyKey, response);

        return response;
    }

    //    withdraw TODO
    //    Same as deposit, but check sufficient balance

    //    transfer TODO
    //    Validate token           --> get customer ID
    //    Check idempotency
    //    Load source account
    //    Authorize --> check if customer owns source acc
    //    Load target account + check if exists and active
    //    Create transaction (newborn with PENDING --> need to complete)
    //    Withdraw from source, deposit to target
    //    Complete transaction
    //    Save both accounts + transaction
    //    Cache response
    //    Return response

    //    getBalance TODO
    //    Validate token            --> get customer ID
    //    Load account
    //    Authorize privs
    //    Return balance from account entity

    //    getAccountsByCustomer TODO
    //    Validate token            --> get customer ID
    //    Determine target customer (self or admin)
    //    Authorize privs
    //    Fetch accounts from repository
    //    Return list of responses
}
