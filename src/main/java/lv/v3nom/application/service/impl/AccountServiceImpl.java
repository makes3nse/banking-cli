package lv.v3nom.application.service.impl;

import lv.v3nom.application.dto.requests.DepositRequest;
import lv.v3nom.application.dto.requests.OpenAccountRequest;
import lv.v3nom.application.dto.responses.AccountResponse;
import lv.v3nom.application.dto.responses.TransactionResponse;
import lv.v3nom.application.mapper.AccountMapper;
import lv.v3nom.application.service.AccountService;
import lv.v3nom.domain.model.Account;
import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.domain.value.IdempotencyKey;
import lv.v3nom.domain.value.OperationStatus;
import lv.v3nom.infrastructure.idempotency.IdempotencyEntry;
import lv.v3nom.infrastructure.idempotency.IdempotencyStore;
import lv.v3nom.infrastructure.repository.INMEM.impl.AccountRepositoryImpl;
import lv.v3nom.infrastructure.repository.INMEM.impl.CustomerRepositoryImpl;
import lv.v3nom.infrastructure.security.PermissionChecker;
import lv.v3nom.infrastructure.security.TokenStore;
import lv.v3nom.infrastructure.time.TimeRules;
import lv.v3nom.infrastructure.time.impl.SystemDateTimeProvider;

public class AccountServiceImpl implements AccountService {
    private final TokenStore tokenStore;
    private final IdempotencyStore idempotencyStore;
    private final CustomerRepositoryImpl customerRepository;
    private final AccountRepositoryImpl accountRepository;

    public AccountServiceImpl(TokenStore tokenStore,
                              IdempotencyStore idempotencyStore,
                              CustomerRepositoryImpl customerRepository,
                              AccountRepositoryImpl accountRepository) {
        this.tokenStore = tokenStore;
        this.idempotencyStore = idempotencyStore;
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
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
                && authenticatedId != null) {
            return AccountMapper.failureResponse(authenticatedId.toString(), OperationStatus.FAILURE);
        }

        IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
        Object cachedResponse = idempotencyStore.retrive(authenticatedId, idempotencyKey);
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
        return null;
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
