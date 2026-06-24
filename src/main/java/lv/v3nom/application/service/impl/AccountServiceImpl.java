package lv.v3nom.application.service.impl;

import com.google.gson.Gson;
import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.*;
import lv.v3nom.application.mapper.AccountMapper;
import lv.v3nom.application.service.AccountService;
import lv.v3nom.application.service.AuthService;
import lv.v3nom.application.service.CustomerService;
import lv.v3nom.application.service.TransactionService;
import lv.v3nom.domain.model.Account;
import lv.v3nom.domain.value.*;
import lv.v3nom.infrastructure.repository.INMEM.AccountRepository;
import lv.v3nom.infrastructure.time.impl.SystemDateTimeProvider;

import java.util.ArrayList;
import java.util.List;

public class AccountServiceImpl implements AccountService {
    private final AuthService authService;
    private final CustomerService customerService;
    private final TransactionService transactionService;
    private final AccountRepository accountRepository;
    private final Gson gson;

    public AccountServiceImpl(AuthService authService,
                              CustomerService customerService,
                              TransactionService transactionService,
                              AccountRepository accountRepository,
                              Gson gson) {

        this.authService = authService;
        this.customerService = customerService;
        this.transactionService = transactionService;
        this.accountRepository = accountRepository;
        this.gson = gson;
    }
    @Override
    public AccountResponse openAccount(OpenAccountRequest request) {
        //      Validate session token    --> get authenticatedId CustomerId
        //      Check idempotency         --> return cached if exists
        //      Solve target CustomerId (from DTO)
        //      Authorize                 --> customer can open for self
        //      Create account via entity factory
        //      Save to repo
        //      Cache response
        //      Return response

        SystemDateTimeProvider time = new SystemDateTimeProvider();

        Currency currency = Currency.of(request.getCurrency());
        String sessionToken = request.getCurrentSessionToken();
        String idempotencyKey = request.getIdempotencyKey();
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));
        CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

        BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
        boolean isValidToken = tokenValidityResponse.value();
        if (authenticatedId == null || !isValidToken) {
            return AccountMapper.failureResponse(authenticatedId.toString(), OperationStatus.FAILURE);
        }

        CachedResponse cachedResponse = authService.getCachedResponse(
                new GetCachedResponseRequest(authenticatedId.getValue(), idempotencyKey));
        if (cachedResponse != null) {
            AccountResponse accountResponse = gson.fromJson(
                    cachedResponse.getResponseJson(),
                    AccountResponse.class
            );
            return accountResponse;
        }

        //  later add TOMCAT and separate services to different projects,
        //  establish comms over HTTP
        //  something like:
        //  private final RestTemplate restTemplate;
        //  String url = "http://localhost:8082/getCustomer";
        //  GetCustomerRequest request = new GetCustomerRequest(sessionToken);
        //  CustomerResponse customerResponse = restTemplate.postForObject(url, request, CustomerResponse.class);

        CustomerResponse customerResponse = customerService.getCustomer(new GetCustomerRequest(sessionToken));

        // WE DON'T NEED FULL CUSTOMER_ENTITY AS IT HAS PASSWORDS AND ALL THAT SHIT INSIDE,
        //  SO WE JUST NEED TO GET A GENERAL-PURPOSE CUSTOMER_RESPONSE WITHOUT SENSITIVE DATA,
        //  WE CAN RETRIEVE ALL NEEDED DATA FROM CUSTOMER_RESPONSE, SUCH AS _STATUS, _ID ETC.
        Account account = Account.open(
                authenticatedId,
                currency,
                CustomerStatus.of(customerResponse.getStatus()),
                time.now()
        );
        // CHANGE ALL IDEMPOTENCY MECHANISMS IN THIS SERVICE AND ALL OTHER SERVICES,
        //  SO THEY STORE AND RETRIEVE JSON STRINGS AND SERIALIZE AND DESERIALIZE THEM WHEN NEEDED USING GSON.
        accountRepository.save(account);

        AccountResponse response = AccountMapper.toResponse(account, OperationStatus.SUCCESS);

        SaveCachedResponseRequest saveCachedResponseRequest = new SaveCachedResponseRequest(
                authenticatedId.getValue(),
                request.getIdempotencyKey(),
                gson.toJson(response),
                response.getClass().getSimpleName()
        );

        authService.saveCachedResponse(saveCachedResponseRequest);
        // ALSO, CHANGE ALL TRANSACTION_REPOSITORY DIRECT CALLS TO TRANSACTION_SERVICE CALLS,
        //  IMPLEMENT ALL NEEDED INTERACTION IN RESPONSIBLE SERVICE, TALK VIA DTOs

        return response;
    }
    @Override
    public TransactionResponse deposit(DepositRequest request) {
        //      Validate token            --> get customer ID
        //      Check idempotency
        //      Load account by CustId
        //      Authorize (customer owns account)
        //      Create transaction (PENDING)
        //      Execute deposit on account entity
        //      Complete transaction
        //      Save account + transaction
        //      Cache response
        //      Return response

        SystemDateTimeProvider time = new SystemDateTimeProvider();

        String sessionToken = request.getCurrentSessionToken();
        TransactionType transactionType = TransactionType.DEPOSIT;
        Currency currency = Currency.of(request.getCurrency());
        Money amount = Money.of(request.getAmount(), currency);
        TransactionId transactionId = TransactionId.generate();
        AccountId accountId = AccountId.of(request.getAccountId());
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));
        CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

        BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
        boolean isValidToken = tokenValidityResponse.value();
        if (!isValidToken || authenticatedId == null) {
            String failureReason = String.format(
                    "Token: %s; Validity: %s; User: %s;",
                    request.getCurrentSessionToken(),
                    isValidToken,
                    authenticatedId
            );
            GetFailureTransactionRequest getFailureTransactionRequest = new GetFailureTransactionRequest(
                    transactionType.getTransactionName(),
                    failureReason,
                    OperationStatus.FAILURE.getValue()
            );

            return transactionService.getFailureResponse(getFailureTransactionRequest);
        }

        CachedResponse cachedResponse = authService.getCachedResponse(
                new GetCachedResponseRequest(authenticatedId.getValue(), request.getIdempotencyKey())
        );
        if (cachedResponse != null) {
            TransactionResponse accountResponse = gson.fromJson(
                    cachedResponse.getResponseJson(),
                    TransactionResponse.class
            );
            return accountResponse;
        }

        Account account = accountRepository.findById(accountId);

        boolean isExistingAccount = account != null;
        boolean isOwnedByAuthenticatedCustomer = account.getOwnerId().equals(authenticatedId);
        if (!isExistingAccount || !isOwnedByAuthenticatedCustomer) {
            String failureReason = String.format(
                    "Account: %s; Owned: %; Exists: %s; User: %s;",
                    request.getAccountId(),
                    isOwnedByAuthenticatedCustomer,
                    isExistingAccount,
                    authenticatedId
            );
            GetFailureTransactionRequest getFailureTransactionRequest = new GetFailureTransactionRequest(
                    transactionType.getTransactionName(),
                    failureReason,
                    OperationStatus.FAILURE.getValue()
            );

            return transactionService.getFailureResponse(getFailureTransactionRequest);
        }
        CreateTransactionRequest createPendingDepositTransactionRequest = new CreateTransactionRequest(
                transactionType.getTransactionName(),
                transactionId.getValue(),
                amount.getValue(),
                amount.getCurrency().value(),
                time.now().toString(),
                accountId.getValue(),
                accountId.getValue()
        );
        TransactionResponse pendingTransactionResponse =
                transactionService.createDepositTransaction(createPendingDepositTransactionRequest);

        account.deposit(amount, time.now());
        accountRepository.save(account);

        CompleteTransactionRequest completeTransactionRequest = new CompleteTransactionRequest(
                transactionId.getValue(), time.now().toString()
        );
        TransactionResponse finalTransactionResponse = transactionService.completeTransaction(
                completeTransactionRequest
        );
        SaveCachedResponseRequest saveCachedResponseRequest = new SaveCachedResponseRequest(
                authenticatedId.getValue(),
                request.getIdempotencyKey(),
                gson.toJson(finalTransactionResponse, TransactionResponse.class),
                finalTransactionResponse.getClass().getSimpleName()

        );

        authService.saveCachedResponse(saveCachedResponseRequest);

        return finalTransactionResponse;
    }
    @Override
    public TransactionResponse withdraw(WithdrawRequest request) {
        //      withdraw, same as deposit, but check sufficient balance

        SystemDateTimeProvider time = new SystemDateTimeProvider();

        TransactionType transactionType = TransactionType.WITHDRAW;
        IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
        String sessionToken = request.getCurrentSessionToken();
        Currency currency = Currency.of(request.getCurrency());
        Money amount = Money.of(request.getAmount(), currency);
        TransactionId transactionId = TransactionId.generate();
        AccountId accountId = AccountId.of(request.getAccountId());
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));
        CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

        BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
        boolean isValidToken = tokenValidityResponse.value();
        if (authenticatedId == null || !isValidToken) {
            String failureReason = String.format(
                    "Token: %s; Validity: %s; User: %s;",
                    request.getCurrentSessionToken(),
                    isValidToken,
                    authenticatedId
            );
            GetFailureTransactionRequest getFailureTransactionRequest = new GetFailureTransactionRequest(
                    transactionType.getTransactionName(),
                    failureReason,
                    OperationStatus.FAILURE.getValue()
            );

            return transactionService.getFailureResponse(getFailureTransactionRequest);
        }

        GetCachedResponseRequest getCachedResponseRequest = new GetCachedResponseRequest(
                authenticatedId.getValue(),
                idempotencyKey.getValue()
        );
        CachedResponse cachedResponse = authService.getCachedResponse(getCachedResponseRequest);
        if (cachedResponse != null) {
            TransactionResponse transactionResponse = gson.fromJson(
                    cachedResponse.getResponseJson(),
                    TransactionResponse.class
            );

            return transactionResponse;
        }

        Account account = accountRepository.findById(accountId);

        boolean isExistingAccount = account != null;
        boolean isOwnedByAuthenticatedCustomer = account.getOwnerId().equals(authenticatedId);
        if (!isExistingAccount || !isOwnedByAuthenticatedCustomer) {
            String failureReason = String.format(
                    "Account: %s; Owned: %; Exists: %s; User: %s;",
                    request.getAccountId(),
                    isOwnedByAuthenticatedCustomer,
                    isExistingAccount,
                    authenticatedId
            );
            GetFailureTransactionRequest getFailureTransactionRequest = new GetFailureTransactionRequest(
                    transactionType.getTransactionName(),
                    failureReason,
                    OperationStatus.FAILURE.getValue()
            );

            return transactionService.getFailureResponse(getFailureTransactionRequest);
        }

        CreateTransactionRequest createPendingWithdrawTransactionRequest = new CreateTransactionRequest(
                transactionType.getTransactionName(),
                transactionId.getValue(),
                amount.getValue(),
                amount.getCurrency().value(),
                time.now().toString(),
                accountId.getValue(),
                accountId.getValue()
        );
        TransactionResponse pendingTransactionResponse =
                transactionService.createWithdrawTransaction(createPendingWithdrawTransactionRequest);

        account.withdraw(amount, time.now());
        accountRepository.save(account);

        CompleteTransactionRequest completeTransactionRequest = new CompleteTransactionRequest(
                        transactionId.getValue(), time.now().toString()
        );
        TransactionResponse finalTransactionResponse =
                transactionService.completeTransaction(completeTransactionRequest);
        SaveCachedResponseRequest saveCachedResponseRequest = new SaveCachedResponseRequest(
                authenticatedId.getValue(),
                idempotencyKey.getValue(),
                gson.toJson(finalTransactionResponse),
                finalTransactionResponse.getClass().getSimpleName()
        );

        authService.saveCachedResponse(saveCachedResponseRequest);

        return finalTransactionResponse;
    }
    @Override
    public TransactionResponse transfer(TransferRequest request) {
        //      Validate token           --> get customer ID
        //      Check idempotency
        //      Load source account
        //      Authorize --> check if customer owns source acc
        //      Load target account + check if exists and active
        //      Create transaction (newborn with PENDING --> need to complete)
        //      Withdraw from source, deposit to target
        //      Complete transaction
        //      Save both accounts + transaction
        //      Cache response
        //      Return response

        SystemDateTimeProvider time = new SystemDateTimeProvider();

        TransactionType transactionType = TransactionType.TRANSFER;
        IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
        String sessionToken = request.getCurrentSessionToken();
        Currency currency = Currency.of(request.getCurrency());
        Money amount = Money.of(request.getAmount(), currency);
        TransactionId transactionId = TransactionId.generate();
        AccountId sourceAccountId = AccountId.of(request.getSourceAccountId());
        AccountId targetAccountId = AccountId.of(request.getTargetAccountId());
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));
        CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

        BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
        boolean isValidToken = tokenValidityResponse.value();
        if (authenticatedId == null || !isValidToken) {
            String failureReason = String.format(
                    "Token: %s; Validity: %s; User: %s;",
                    request.getCurrentSessionToken(),
                    isValidToken,
                    authenticatedId
            );
            GetFailureTransactionRequest getFailureTransactionRequest = new GetFailureTransactionRequest(
                    transactionType.getTransactionName(), failureReason, OperationStatus.FAILURE.getValue()
            );

            return transactionService.getFailureResponse(getFailureTransactionRequest);
        }

        GetCachedResponseRequest getCachedResponseRequest = new GetCachedResponseRequest(
                authenticatedId.getValue(), idempotencyKey.getValue()
        );
        CachedResponse cachedResponse = authService.getCachedResponse(getCachedResponseRequest);
        if (cachedResponse != null) {
            TransactionResponse transactionResponse = gson.fromJson(
                    cachedResponse.getResponseJson(), TransactionResponse.class
            );

            return transactionResponse;
        }

        Account sourceAccount = accountRepository.findById(sourceAccountId);
        boolean isExistingSourceAccount = sourceAccount != null;
        boolean isOwnedByAuthenticatedCustomer = sourceAccount.getOwnerId().equals(authenticatedId);
        if (!isExistingSourceAccount || !isOwnedByAuthenticatedCustomer) {
            String failureReason = String.format(
                    "Account: %s; Owned: %; Exists: %s; User: %s;",
                    request.getSourceAccountId(),
                    isOwnedByAuthenticatedCustomer,
                    isExistingSourceAccount,
                    authenticatedId
            );
            GetFailureTransactionRequest getFailureTransactionRequest = new GetFailureTransactionRequest(
                    transactionType.getTransactionName(), failureReason, OperationStatus.FAILURE.getValue()
            );

            return transactionService.getFailureResponse(getFailureTransactionRequest);
        }

        Account targetAccount = accountRepository.findById(targetAccountId);
        boolean isExistingTargetAccount = targetAccount != null;
        if (!isExistingTargetAccount) {
            String failureReason = String.format(
                    "Account: %s; Exists: %s; User: %s;",
                    request.getSourceAccountId(),
                    isExistingTargetAccount,
                    authenticatedId
            );
            GetFailureTransactionRequest getFailureTransactionRequest = new GetFailureTransactionRequest(
                    transactionType.getTransactionName(), failureReason, OperationStatus.FAILURE.getValue()
            );

            return transactionService.getFailureResponse(getFailureTransactionRequest);
        }

        CreateTransactionRequest createPendingTransferTransactionRequest = new CreateTransactionRequest(
                transactionType.getTransactionName(),
                transactionId.getValue(),
                amount.getValue(),
                amount.getCurrency().value(),
                time.now().toString(),
                sourceAccountId.getValue(),
                targetAccountId.getValue()
        );
        TransactionResponse pendingTransactionResponse = transactionService.createTransferTransaction(
                createPendingTransferTransactionRequest
        );

        sourceAccount.transferToAccount(targetAccount, amount, time.now());
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        CompleteTransactionRequest completeTransactionRequest = new CompleteTransactionRequest(
                transactionId.getValue(), time.now().toString()
        );
        TransactionResponse finalTransactionResponse =
                transactionService.completeTransaction(completeTransactionRequest);
        SaveCachedResponseRequest saveCachedResponseRequest = new SaveCachedResponseRequest(
                authenticatedId.getValue(),
                idempotencyKey.getValue(),
                gson.toJson(finalTransactionResponse),
                finalTransactionResponse.getClass().getSimpleName()
        );

        authService.saveCachedResponse(saveCachedResponseRequest);

        return finalTransactionResponse;
    }
    @Override
    public BalanceResponse getBalance(ViewBalanceRequest request) {
        //      Validate token            --> get customer ID
        //      Load account
        //      Authorize privs
        //      Return balance from account entity

        SystemDateTimeProvider time = new SystemDateTimeProvider();

        String sessionToken = request.getCurrentSessionToken();
        AccountId accountId = AccountId.of(request.getAccountId());
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));
        CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

        BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
        boolean isValidToken = tokenValidityResponse.value();
        if (authenticatedId == null || !isValidToken) {
            String failureReason = String.format(
                    "Token: %s; Validity: %s; User: %s;",
                    request.getCurrentSessionToken(),
                    isValidToken,
                    authenticatedId
            );

            return AccountMapper.failureResponseBalance(failureReason, OperationStatus.FAILURE);
        }

        Account account = accountRepository.findById(accountId);
        boolean isOwnedByAuthenticatedCustomer = account.getOwnerId().equals(authenticatedId);
        boolean isExistingAccount = account != null;
        if (!isExistingAccount || !isOwnedByAuthenticatedCustomer) {
            String failureReason = String.format(
                    "Account: %s; Owned: %; Exists: %s; User: %s;",
                    request.getAccountId(),
                    isOwnedByAuthenticatedCustomer,
                    isExistingAccount,
                    authenticatedId
            );

            return AccountMapper.failureResponseBalance(failureReason, OperationStatus.FAILURE);
        }

        BalanceResponse balanceResponse = AccountMapper.toBalanceResponse(account, OperationStatus.SUCCESS);

        return balanceResponse;
    }
    @Override
    public List<AccountResponse> getAccountsByCustomer(GetAccountsRequest request) {
        //      Validate token            --> get customer ID
        //      Determine target customer (self or admin)
        //      Authorize privs
        //      Fetch accounts from repository
        //      Return list of responses

        SystemDateTimeProvider time = new SystemDateTimeProvider();

        String sessionToken = request.getCurrentSessionToken();
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));
        CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

        BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
        boolean isValidToken = tokenValidityResponse.value();
        if (authenticatedId == null || !isValidToken) {
            String failureReason = String.format(
                    "Token: %s; Validity: %s; User: %s;",
                    request.getCurrentSessionToken(),
                    isValidToken,
                    authenticatedId
            );

            return List.of(AccountMapper.failureResponse(failureReason, OperationStatus.FAILURE));
        }

        List<Account> accounts = accountRepository.findByCustomerId(authenticatedId);
        List<AccountResponse> response = new ArrayList<>();
        for (Account account : accounts) {
            if (account.getOwnerId().equals(authenticatedId)) {
                response.add(AccountMapper.toResponse(account, OperationStatus.UNKNOWN));
            }
        }

        return response;
    }

    @Override
    public AccountStatusResponse closeAccount(CloseAccountRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        String sessionToken = request.getCurrentSessionToken();
        AccountId accountId = AccountId.of(request.getAccountId());
        IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));
        CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

        BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
        boolean isValidToken = tokenValidityResponse.value();
        if (authenticatedId == null || !isValidToken) {
            String failureReason = String.format(
                    "Token: %s; Validity: %s; User: %s;",
                    request.getCurrentSessionToken(),
                    isValidToken,
                    authenticatedId
            );

            return new AccountStatusResponse(
                    null,
                    OperationStatus.FAILURE.getValue(),
                    failureReason
            );
        }

        GetCachedResponseRequest getCachedResponseRequest = new GetCachedResponseRequest(
                authenticatedId.getValue(),
                idempotencyKey.getValue()
        );
        CachedResponse cachedResponse = authService.getCachedResponse(getCachedResponseRequest);
        if (cachedResponse != null) {
            AccountStatusResponse accountStatusResponse = gson.fromJson(
                    cachedResponse.getResponseJson(), AccountStatusResponse.class
            );

            return accountStatusResponse;
        }

        Account account = accountRepository.findById(accountId);
        boolean isExistingAccount = account != null;
        boolean isOwnedByAuthenticatedCustomer = account.getOwnerId().equals(authenticatedId);
        if (!isExistingAccount || !isOwnedByAuthenticatedCustomer) {
            String failureReason = String.format(
                    "Account: %s; Owned: %; Exists: %s; User: %s;",
                    request.getAccountId(),
                    isOwnedByAuthenticatedCustomer,
                    isExistingAccount,
                    authenticatedId
            );

            return new AccountStatusResponse(
                    null,
                    OperationStatus.FAILURE.getValue(),
                    failureReason
            );
        }

        account.close(time.now());
        accountRepository.save(account);

        AccountStatusResponse response = new AccountStatusResponse(
                account.getAccountStatus().getValue(),
                OperationStatus.SUCCESS.getValue(),
                null
        );
        SaveCachedResponseRequest saveCachedResponseRequest = new SaveCachedResponseRequest(
                authenticatedId.getValue(),
                idempotencyKey.getValue(),
                gson.toJson(response),
                response.getClass().getSimpleName()
        );

        authService.saveCachedResponse(saveCachedResponseRequest);

        return response;
    }
    @Override
    public AccountStatusResponse freezeAccount(FreezeAccountRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        String sessionToken = request.getCurrentSessionToken();
        AccountId accountId = AccountId.of(request.getAccountId());
        IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));
        CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

        BooleanResponse tokenValidityResponse =  authService.validateToken(new ValidateTokenRequest(sessionToken));
        boolean isValidToken = tokenValidityResponse.value();
        if (authenticatedId == null || !isValidToken) {
            String failureReason = String.format(
                    "Token: %s; Validity: %s; User: %s;",
                    request.getCurrentSessionToken(),
                    isValidToken,
                    authenticatedId
            );

            return new AccountStatusResponse(
                    null,
                    OperationStatus.FAILURE.getValue(),
                    failureReason
            );
        }

        GetCachedResponseRequest getCachedResponseRequest = new GetCachedResponseRequest(
                authenticatedId.getValue(), idempotencyKey.getValue()
        );
        CachedResponse cachedResponse = authService.getCachedResponse(getCachedResponseRequest);
        if (cachedResponse != null) {
            AccountStatusResponse accountStatusResponse = gson.fromJson(
                    cachedResponse.getResponseJson(), AccountStatusResponse.class
            );

            return accountStatusResponse;
        }

        Account account = accountRepository.findById(accountId);
        boolean isExistingAccount = account != null;
        boolean isOwnedByAuthenticatedCustomer = account.getOwnerId().equals(authenticatedId);
        if (!isExistingAccount || !isOwnedByAuthenticatedCustomer) {
            String failureReason = String.format(
                    "Account: %s; Owned: %; Exists: %s; User: %s;",
                    request.getAccountId(),
                    isOwnedByAuthenticatedCustomer,
                    isExistingAccount,
                    authenticatedId
            );

            return new AccountStatusResponse(
                    null,
                    OperationStatus.FAILURE.getValue(),
                    failureReason
            );
        }

        account.freeze(time.now());
        accountRepository.save(account);

        AccountStatusResponse response = new AccountStatusResponse(
                account.getAccountStatus().getValue(),
                OperationStatus.SUCCESS.getValue(),
                null
        );
        SaveCachedResponseRequest saveCachedResponseRequest = new SaveCachedResponseRequest(
                authenticatedId.getValue(),
                idempotencyKey.getValue(),
                gson.toJson(response),
                response.getClass().getSimpleName()
        );

        authService.saveCachedResponse(saveCachedResponseRequest);

        return response;
    }
    @Override
    public AccountStatusResponse unfreezeAccount(UnfreezeAccountRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        String sessionToken = request.getCurrentSessionToken();
        AccountId accountId = AccountId.of(request.getAccountId());
        IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));
        CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

        BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
        boolean isValidToken = tokenValidityResponse.value();
        if (authenticatedId == null || !isValidToken) {
            String failureReason = String.format(
                    "Token: %s; Validity: %s; User: %s;",
                    request.getCurrentSessionToken(),
                    isValidToken,
                    authenticatedId
            );

            return new AccountStatusResponse(
                    null,
                    OperationStatus.FAILURE.getValue(),
                    failureReason
            );
        }
        GetCachedResponseRequest getCachedResponseRequest = new GetCachedResponseRequest(
                authenticatedId.getValue(), idempotencyKey.getValue()
        );
        CachedResponse cachedResponse = authService.getCachedResponse(getCachedResponseRequest);
        if (cachedResponse != null) {
            AccountStatusResponse accountStatusResponse = gson.fromJson(
                    cachedResponse.getResponseJson(), AccountStatusResponse.class
            );

            return accountStatusResponse;
        }

        Account account = accountRepository.findById(accountId);
        boolean isExistingAccount = account != null;
        boolean isOwnedByAuthenticatedCustomer = account.getOwnerId().equals(authenticatedId);
        if (!isExistingAccount || !isOwnedByAuthenticatedCustomer) {
            String failureReason = String.format(
                    "Account: %s; Owned: %; Exists: %s; User: %s;",
                    request.getAccountId(),
                    isOwnedByAuthenticatedCustomer,
                    isExistingAccount,
                    authenticatedId
            );

            return new AccountStatusResponse(
                    null,
                    OperationStatus.FAILURE.getValue(),
                    failureReason
            );
        }

        account.unfreeze(time.now());
        accountRepository.save(account);

        AccountStatusResponse response = new AccountStatusResponse(
                account.getAccountStatus().getValue(),
                OperationStatus.SUCCESS.getValue(),
                null
        );
        SaveCachedResponseRequest saveCachedResponseRequest = new SaveCachedResponseRequest(
                authenticatedId.getValue(),
                idempotencyKey.getValue(),
                gson.toJson(response),
                response.getClass().getSimpleName()
        );

        authService.saveCachedResponse(saveCachedResponseRequest);

        return response;
    }
    @Override
    public AccountResponse getAccountDetails(GetAccountDetailsRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        String sessionToken = request.getCurrentSessionToken();
        AccountId accountId = AccountId.of(request.getAccountId());
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));
        CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

        BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
        boolean isValidToken = tokenValidityResponse.value();
        if (authenticatedId == null || !isValidToken) {
            String failureReason = String.format(
                    "Token: %s; Validity: %s; User: %s;",
                    request.getCurrentSessionToken(),
                    isValidToken,
                    authenticatedId
            );

            return AccountMapper.failureResponse(failureReason, OperationStatus.FAILURE);
        }

        Account account = accountRepository.findById(accountId);
        boolean isOwnedByAuthenticatedCustomer = account.getOwnerId().equals(authenticatedId);
        boolean isExistingAccount = account != null;
        if (!isExistingAccount || !isOwnedByAuthenticatedCustomer) {
            String failureReason = String.format(
                    "Account: %s; Owned: %; Exists: %s; User: %s;",
                    request.getAccountId(),
                    isOwnedByAuthenticatedCustomer,
                    isExistingAccount,
                    authenticatedId
            );

            return AccountMapper.failureResponse(failureReason, OperationStatus.FAILURE);
        }

        AccountResponse response = AccountMapper.toResponse(account, OperationStatus.SUCCESS);

        return response;
    }
}
