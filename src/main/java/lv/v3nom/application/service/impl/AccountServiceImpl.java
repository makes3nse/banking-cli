package lv.v3nom.application.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.*;
import lv.v3nom.application.mapper.AccountMapper;
import lv.v3nom.application.service.AccountService;
import lv.v3nom.application.service.AuthService;
import lv.v3nom.application.service.CustomerService;
import lv.v3nom.application.service.TransactionService;
import lv.v3nom.domain.exception.AccountNotFoundException;
import lv.v3nom.domain.model.Account;
import lv.v3nom.domain.value.*;
import lv.v3nom.infrastructure.repository.INMEM.AccountRepository;
import lv.v3nom.infrastructure.time.impl.SystemDateTimeProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class AccountServiceImpl implements AccountService {
    private final AuthService authService;
    private final CustomerService customerService;
    private final Supplier<TransactionService> transactionServiceFactory;
    private final AccountRepository accountRepository;
    private final Gson gson;

    public AccountServiceImpl(AuthService authService,
                              CustomerService customerService,
                              Supplier<TransactionService> transactionServiceFactory,
                              AccountRepository accountRepository,
                              Gson gson) {

        this.authService = authService;
        this.customerService = customerService;
        this.transactionServiceFactory = transactionServiceFactory;
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

        String sessionToken = request.getCurrentSessionToken();
        String idempotencyKey = request.getIdempotencyKey();
        System.out.println("=== OPEN ACCOUNT START ===");
        System.out.println("Session token: " + sessionToken);
        System.out.println("Currency: " + request.getCurrency());
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));
        System.out.println("Auth successful for customer: " + authResponse.getCustomerId());

        try {
            Currency currency = Currency.of(request.getCurrency());
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());
            System.out.println("Authenticated ID: " + authenticatedId.getValue());

            BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
            boolean isValidToken = tokenValidityResponse.value();
            System.out.println("Token valid: " + isValidToken);
            if (!isValidToken) {
                String failureReason = String.format(
                        "Token: %s; IsValidToken: %s; User: %s;",
                        request.getCurrentSessionToken(),
                        isValidToken,
                        authenticatedId // is not null, cause if it would be, CustomerId.of() would throw IllegalArgumentException
                );
                OperationStatus operationStatus = OperationStatus.failure(failureReason);

                return AccountMapper.failureResponse(authResponse.getCustomerId(), operationStatus);
            }

            System.out.println("Checking cache for idempotency key: " + idempotencyKey);
            GetCachedResponseFromIdRequest getCachedResponseFromIdRequest = new GetCachedResponseFromIdRequest(
                    authenticatedId.getValue(), idempotencyKey
            );
            CachedResponse cachedResponse = authService.getCachedResponseFromId(getCachedResponseFromIdRequest);
            if (cachedResponse.getErrorMessage() != null) {

                System.err.println("Cache retrieval failed: " + cachedResponse.getErrorMessage());
            }
            if (cachedResponse.getErrorMessage() == null && cachedResponse.getResponseJson() != null) {
                System.out.println("Using cached response");
                AccountResponse accountResponse = gson.fromJson(
                        cachedResponse.getResponseJson(),
                        AccountResponse.class
                );
                System.out.println("Cached account response - ID: " + accountResponse.getAccountId());
                return accountResponse;
            } else {
                System.out.println("No cached response found");
            }

            //  later add TOMCAT and separate services to different projects,
            //  establish comms over HTTP
            //  something like:
            //  private final RestTemplate restTemplate;
            //  String url = "http://localhost:8082/getCustomer";
            //  GetCustomerRequest request = new GetCustomerRequest(sessionToken);
            //  CustomerResponse customerResponse = restTemplate.postForObject(url, request, CustomerResponse.class);

            System.out.println("Getting customer data...");
            CustomerResponse customerResponse = customerService.getCustomer(new GetCustomerRequest(sessionToken));
            System.out.println("Customer status: " + customerResponse.getStatus());
            // WE DON'T NEED FULL CUSTOMER_ENTITY AS IT HAS PASSWORDS AND ALL THAT SHIT INSIDE,
            //  SO WE JUST NEED TO GET A GENERAL-PURPOSE CUSTOMER_RESPONSE WITHOUT SENSITIVE DATA,
            //  WE CAN RETRIEVE ALL NEEDED DATA FROM CUSTOMER_RESPONSE, SUCH AS _STATUS, _ID ETC.
            System.out.println("Creating account...");
            System.out.println(customerResponse);
            Account account = Account.open(
                    authenticatedId,
                    currency,
                    CustomerStatus.of(customerResponse.getStatus()),
                    time.now()
            );
            // CHANGE ALL IDEMPOTENCY MECHANISMS IN THIS SERVICE AND ALL OTHER SERVICES,
            //  SO THEY STORE AND RETRIEVE JSON STRINGS AND SERIALIZE AND DESERIALIZE THEM WHEN NEEDED USING GSON.
            System.out.println("Account created - ID: " + account.getAccountId().getValue());
            System.out.println("Account currency: " + account.getCurrency().value());
            System.out.println("Account status: " + account.getAccountStatus().getValue());
            System.out.println("Account balance: " + account.getBalance().getValue());

            // Save account
            System.out.println("Saving account to repository...");
            accountRepository.save(account);
            System.out.println("Account saved id: " + account.getAccountId());
            // Map to response
            System.out.println("Mapping to response...");
            AccountResponse response = AccountMapper.toResponse(account, OperationStatus.SUCCESS);
            System.out.println("Account response - ID: " + response.getAccountId());
            System.out.println("Account response - Status: " + response.getStatus());
            System.out.println("Account response - Currency: " + response.getCurrency());
            System.out.println("Account response - Balance: " + response.getBalance());
            // Cache response
            System.out.println("Caching response...");
            SaveCachedResponseFromIdRequest saveCachedResponseFromIdRequest = new SaveCachedResponseFromIdRequest(
                    authenticatedId.getValue(),
                    request.getIdempotencyKey(),
                    gson.toJson(response),
                    response.getClass().getSimpleName()
            );

            BooleanResponse saved = authService.saveCachedResponseFromId(saveCachedResponseFromIdRequest);
            if (!saved.value()) {
                System.err.println("Cache save failed: " + saved.getErrorMessage());
            } else {
                System.out.println("Response cached successfully");
            }
            System.out.println("=== OPEN ACCOUNT COMPLETE ===");
            return response;

        } catch (IllegalArgumentException | IllegalStateException | JsonSyntaxException e) {
            System.err.println("=== OPEN ACCOUNT FAILED ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            OperationStatus operationStatus = OperationStatus.failure(e.getMessage());

            return AccountMapper.failureResponse(authResponse.getCustomerId(), operationStatus);
        }
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
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));
        
        try {

            TransactionType transactionType = TransactionType.DEPOSIT;
            Currency currency = Currency.of(request.getCurrency());
            Money amount = Money.of(request.getAmount(), currency);
            TransactionId transactionId = TransactionId.generate();
            AccountId accountId = AccountId.of(request.getAccountId());
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

            BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
            boolean isValidToken = tokenValidityResponse.value();
            if (!isValidToken) {
                String failureReason = String.format(
                        "Token: %s; IsValidToken: %s; User: %s;",
                        request.getCurrentSessionToken(),
                        isValidToken,
                        authenticatedId
                );
                GetFailureTransactionRequest getFailureTransactionRequest = new GetFailureTransactionRequest(
                        transactionType.getTransactionName(),
                        failureReason,
                        OperationStatus.FAILURE.getValue()
                );

                return transactionServiceFactory.get().getFailureResponse(getFailureTransactionRequest);
            }

            GetCachedResponseFromIdRequest getCachedResponseFromIdRequest = new GetCachedResponseFromIdRequest(
                    authenticatedId.getValue(), request.getIdempotencyKey()
            );
            CachedResponse cachedResponse = authService.getCachedResponseFromId(getCachedResponseFromIdRequest);
            if (cachedResponse.getErrorMessage() != null) {
                System.err.println("Cache retrieval failed: " + cachedResponse.getErrorMessage());
            }
            if (cachedResponse.getErrorMessage() == null && cachedResponse.getResponseJson() != null) {
                TransactionResponse accountResponse = gson.fromJson(
                        cachedResponse.getResponseJson(),
                        TransactionResponse.class
                );
                return accountResponse;
            }

            Account account = accountRepository.findById(accountId);
            System.out.println("AccSvc: accRepo.findById " + account.getAccountId().getValue());

            boolean isOwnedByAuthenticatedCustomer = account.getOwnerId().equals(authenticatedId);
            if (!isOwnedByAuthenticatedCustomer) {
                String failureReason = String.format(
                        "Account: %s; Owned: %; User: %s;",
                        request.getAccountId(),
                        isOwnedByAuthenticatedCustomer,
                        authenticatedId
                );
                GetFailureTransactionRequest getFailureTransactionRequest = new GetFailureTransactionRequest(
                        transactionType.getTransactionName(),
                        failureReason,
                        OperationStatus.FAILURE.getValue()
                );

                return transactionServiceFactory.get().getFailureResponse(getFailureTransactionRequest);
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
                    transactionServiceFactory.get().createDepositTransaction(createPendingDepositTransactionRequest);

            account.deposit(amount, time.now());
            accountRepository.save(account);

            CompleteTransactionRequest completeTransactionRequest = new CompleteTransactionRequest(
                    pendingTransactionResponse.getTransactionId(), time.now().toString()
            );
            TransactionResponse finalTransactionResponse = transactionServiceFactory.get().completeTransaction(
                    completeTransactionRequest
            );
            SaveCachedResponseFromIdRequest saveCachedResponseFromIdRequest = new SaveCachedResponseFromIdRequest(
                    authenticatedId.getValue(),
                    request.getIdempotencyKey(),
                    gson.toJson(finalTransactionResponse, TransactionResponse.class),
                    finalTransactionResponse.getClass().getSimpleName()

            );

            BooleanResponse saved = authService.saveCachedResponseFromId(saveCachedResponseFromIdRequest);
            if (!saved.value()) {
                System.err.println("Cache save failed: " + saved.getErrorMessage());
            }

            return finalTransactionResponse;

        } catch (IllegalArgumentException | IllegalStateException | AccountNotFoundException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.failure(e.getMessage());
            GetFailureTransactionRequest  getFailureTransactionRequest = new GetFailureTransactionRequest(
                    TransactionType.DEPOSIT.getTransactionName(),
                    operationStatus.getDescription(),
                    operationStatus.getValue()
            );

            return transactionServiceFactory.get().getFailureResponse(getFailureTransactionRequest);
        }
    }
    @Override
    public TransactionResponse withdraw(WithdrawRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        String sessionToken = request.getCurrentSessionToken();
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));

        try {
            TransactionType transactionType = TransactionType.WITHDRAW;
            IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
            Currency currency = Currency.of(request.getCurrency());
            Money amount = Money.of(request.getAmount(), currency);
            TransactionId transactionId = TransactionId.generate();
            AccountId accountId = AccountId.of(request.getAccountId());
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

            BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
            boolean isValidToken = tokenValidityResponse.value();
            if (!isValidToken) {
                String failureReason = String.format(
                        "Token: %s; IsValidToken: %s; User: %s;",
                        request.getCurrentSessionToken(),
                        isValidToken,
                        authenticatedId
                );
                GetFailureTransactionRequest getFailureTransactionRequest = new GetFailureTransactionRequest(
                        transactionType.getTransactionName(),
                        failureReason,
                        OperationStatus.FAILURE.getValue()
                );

                return transactionServiceFactory.get().getFailureResponse(getFailureTransactionRequest);
            }

            GetCachedResponseFromIdRequest getCachedResponseFromIdRequest = new GetCachedResponseFromIdRequest(
                    authenticatedId.getValue(),
                    idempotencyKey.getValue()
            );
            CachedResponse cachedResponse = authService.getCachedResponseFromId(getCachedResponseFromIdRequest);
            if (cachedResponse.getErrorMessage() != null) {
                System.err.println("Cache retrieval failed: " + cachedResponse.getErrorMessage());
            }
            if (cachedResponse.getErrorMessage() == null && cachedResponse.getResponseJson() != null) {
                TransactionResponse transactionResponse = gson.fromJson(
                        cachedResponse.getResponseJson(),
                        TransactionResponse.class
                );

                return transactionResponse;
            }

            Account account = accountRepository.findById(accountId);

            boolean isOwnedByAuthenticatedCustomer = account.getOwnerId().equals(authenticatedId);
            if (!isOwnedByAuthenticatedCustomer) {
                String failureReason = String.format(
                        "Account: %s; Owned: %; User: %s;",
                        request.getAccountId(),
                        isOwnedByAuthenticatedCustomer,
                        authenticatedId
                );
                GetFailureTransactionRequest getFailureTransactionRequest = new GetFailureTransactionRequest(
                        transactionType.getTransactionName(),
                        failureReason,
                        OperationStatus.FAILURE.getValue()
                );

                return transactionServiceFactory.get().getFailureResponse(getFailureTransactionRequest);
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
                    transactionServiceFactory.get().createWithdrawTransaction(createPendingWithdrawTransactionRequest);

            account.withdraw(amount, time.now());
            accountRepository.save(account);

            CompleteTransactionRequest completeTransactionRequest = new CompleteTransactionRequest(
                            pendingTransactionResponse.getTransactionId(), time.now().toString()
            );
            TransactionResponse finalTransactionResponse =
                    transactionServiceFactory.get().completeTransaction(completeTransactionRequest);
            SaveCachedResponseFromIdRequest saveCachedResponseFromIdRequest = new SaveCachedResponseFromIdRequest(
                    authenticatedId.getValue(),
                    idempotencyKey.getValue(),
                    gson.toJson(finalTransactionResponse),
                    finalTransactionResponse.getClass().getSimpleName()
            );

            BooleanResponse saved = authService.saveCachedResponseFromId(saveCachedResponseFromIdRequest);
            if (!saved.value()) {
                System.err.println("Cache save failed: " + saved.getErrorMessage());
            }

            return finalTransactionResponse;

        } catch (IllegalArgumentException | IllegalStateException | AccountNotFoundException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.failure(e.getMessage());
            GetFailureTransactionRequest  getFailureTransactionRequest = new GetFailureTransactionRequest(
                    TransactionType.WITHDRAW.getTransactionName(),
                    operationStatus.getDescription(),
                    operationStatus.getValue()
            );

            return transactionServiceFactory.get().getFailureResponse(getFailureTransactionRequest);
        }
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

        String sessionToken = request.getCurrentSessionToken();
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));

        try {
            TransactionType transactionType = TransactionType.TRANSFER;
            IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
            Currency currency = Currency.of(request.getCurrency());
            Money amount = Money.of(request.getAmount(), currency);
            TransactionId transactionId = TransactionId.generate();
            AccountId sourceAccountId = AccountId.of(request.getSourceAccountId());
            AccountId targetAccountId = AccountId.of(request.getTargetAccountId());
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

            BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
            boolean isValidToken = tokenValidityResponse.value();
            if (!isValidToken) {
                String failureReason = String.format(
                        "Token: %s; IsValidToken: %s; User: %s;",
                        request.getCurrentSessionToken(),
                        isValidToken,
                        authenticatedId
                );
                GetFailureTransactionRequest getFailureTransactionRequest = new GetFailureTransactionRequest(
                        transactionType.getTransactionName(), failureReason, OperationStatus.FAILURE.getValue()
                );

                return transactionServiceFactory.get().getFailureResponse(getFailureTransactionRequest);
            }

            GetCachedResponseFromIdRequest getCachedResponseFromIdRequest = new GetCachedResponseFromIdRequest(
                    authenticatedId.getValue(), idempotencyKey.getValue()
            );
            CachedResponse cachedResponse = authService.getCachedResponseFromId(getCachedResponseFromIdRequest);
            if (cachedResponse.getErrorMessage() != null) {
                System.err.println("Cache retrieval failed: " + cachedResponse.getErrorMessage());
            }
            if (cachedResponse.getErrorMessage() == null && cachedResponse.getResponseJson() != null) {
                TransactionResponse transactionResponse = gson.fromJson(
                        cachedResponse.getResponseJson(), TransactionResponse.class
                );

                return transactionResponse;
            }

            Account sourceAccount = accountRepository.findById(sourceAccountId);

            boolean isOwnedByAuthenticatedCustomer = sourceAccount.getOwnerId().equals(authenticatedId);
            if (!isOwnedByAuthenticatedCustomer) {
                String failureReason = String.format(
                        "Account: %s; Owned: %; User: %s;",
                        request.getSourceAccountId(),
                        isOwnedByAuthenticatedCustomer,
                        authenticatedId
                );
                GetFailureTransactionRequest getFailureTransactionRequest = new GetFailureTransactionRequest(
                        transactionType.getTransactionName(), failureReason, OperationStatus.FAILURE.getValue()
                );

                return transactionServiceFactory.get().getFailureResponse(getFailureTransactionRequest);
            }

            Account targetAccount = accountRepository.findById(targetAccountId);

            CreateTransactionRequest createPendingTransferTransactionRequest = new CreateTransactionRequest(
                    transactionType.getTransactionName(),
                    transactionId.getValue(),
                    amount.getValue(),
                    amount.getCurrency().value(),
                    time.now().toString(),
                    sourceAccountId.getValue(),
                    targetAccountId.getValue()
            );
            TransactionResponse pendingTransactionResponse = transactionServiceFactory.get().createTransferTransaction(
                    createPendingTransferTransactionRequest
            );

            sourceAccount.transferToAccount(targetAccount, amount, time.now());
            accountRepository.save(sourceAccount);
            accountRepository.save(targetAccount);

            CompleteTransactionRequest completeTransactionRequest = new CompleteTransactionRequest(
                    pendingTransactionResponse.getTransactionId(), time.now().toString()
            );
            TransactionResponse finalTransactionResponse =
                    transactionServiceFactory.get().completeTransaction(completeTransactionRequest);
            SaveCachedResponseFromIdRequest saveCachedResponseFromIdRequest = new SaveCachedResponseFromIdRequest(
                    authenticatedId.getValue(),
                    idempotencyKey.getValue(),
                    gson.toJson(finalTransactionResponse),
                    finalTransactionResponse.getClass().getSimpleName()
            );

            BooleanResponse saved = authService.saveCachedResponseFromId(saveCachedResponseFromIdRequest);
            if (!saved.value()) {
                System.err.println("Cache save failed: " + saved.getErrorMessage());
            }

            return finalTransactionResponse;

        } catch (IllegalArgumentException | IllegalStateException | AccountNotFoundException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.failure(e.getMessage());
            GetFailureTransactionRequest  getFailureTransactionRequest = new GetFailureTransactionRequest(
                    TransactionType.WITHDRAW.getTransactionName(),
                    operationStatus.getDescription(),
                    operationStatus.getValue()
            );

            return transactionServiceFactory.get().getFailureResponse(getFailureTransactionRequest);
        }
    }
    @Override
    public BalanceResponse getBalance(ViewBalanceRequest request) {
        //      Validate token            --> get customer ID
        //      Load account
        //      Authorize privs
        //      Return balance from account entity
        String sessionToken = request.getCurrentSessionToken();
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));

        try {
            AccountId accountId = AccountId.of(request.getAccountId());
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

            BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
            boolean isValidToken = tokenValidityResponse.value();
            if (!isValidToken) {
                String failureReason = String.format(
                        "Token: %s; IsValidToken: %s; User: %s;",
                        request.getCurrentSessionToken(),
                        isValidToken,
                        authenticatedId
                );

                return AccountMapper.failureResponseBalance(failureReason, OperationStatus.FAILURE);
            }

            Account account = accountRepository.findById(accountId);

            boolean isOwnedByAuthenticatedCustomer = account.getOwnerId().equals(authenticatedId);
            if (!isOwnedByAuthenticatedCustomer) {
                String failureReason = String.format(
                        "Account: %s; Owned: %; User: %s;",
                        request.getAccountId(),
                        isOwnedByAuthenticatedCustomer,
                        authenticatedId
                );

                return AccountMapper.failureResponseBalance(failureReason, OperationStatus.FAILURE);
            }

            BalanceResponse balanceResponse = AccountMapper.toBalanceResponse(account, OperationStatus.SUCCESS);

            return balanceResponse;

        } catch (IllegalArgumentException | AccountNotFoundException e) {
            OperationStatus operationStatus = OperationStatus.failure(e.getMessage());

            return AccountMapper.failureResponseBalance(operationStatus.getDescription(), operationStatus);
        }
    }
    @Override
    public AccountListResponse getAccountsByCustomer(GetAccountsRequest request) {
        //      Validate token            --> get customer ID
        //      Determine target customer (self or admin)
        //      Authorize privs
        //      Fetch accounts from repository
        //      Return list of responses
        String sessionToken = request.getCurrentSessionToken();
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));

        try {
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

            BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
            boolean isValidToken = tokenValidityResponse.value();
            if (!isValidToken) {
                String failureReason = String.format(
                        "Token: %s; IsValidToken: %s; User: %s;",
                        request.getCurrentSessionToken(),
                        isValidToken,
                        authenticatedId
                );
                OperationStatus operationStatus = OperationStatus.failure(failureReason);
                AccountListResponse accountListResponse = new AccountListResponse(
                        List.of(AccountMapper.failureResponse(authResponse.getCustomerId(), operationStatus)),
                        0,
                        operationStatus.getValue(),
                        operationStatus.getDescription()
                );

                return accountListResponse;
            }

            List<Account> accounts = accountRepository.findByCustomerId(authenticatedId);
            List<AccountResponse> accountResponses = new ArrayList<>();
            int totalCount = 0;
            AccountListResponse accountListResponse = new AccountListResponse(
                    accountResponses,
                    totalCount,
                    OperationStatus.UNKNOWN.getValue(),
                    OperationStatus.UNKNOWN.getDescription()
            );
            for (Account account : accounts) {
                if (account.getOwnerId().equals(authenticatedId)) {
                    accountResponses.add(AccountMapper.toResponse(account, OperationStatus.UNKNOWN));
                    totalCount++;
                }
            }

            return accountListResponse;

        } catch (IllegalArgumentException | AccountNotFoundException e) {
            OperationStatus operationStatus = OperationStatus.failure(e.getMessage());
            AccountListResponse accountListResponse = new AccountListResponse(
                    List.of(AccountMapper.failureResponse(authResponse.getCustomerId(), operationStatus)),
                    0,
                    operationStatus.getValue(),
                    operationStatus.getDescription()
            );

            return accountListResponse;
        }
    }

    @Override
    public AccountStatusResponse closeAccount(CloseAccountRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        String sessionToken = request.getCurrentSessionToken();
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));

        try {
            AccountId accountId = AccountId.of(request.getAccountId());
            IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

            BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
            boolean isValidToken = tokenValidityResponse.value();
            if (!isValidToken) {
                String failureReason = String.format(
                        "Token: %s; IsValidToken: %s; User: %s;",
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

            GetCachedResponseFromIdRequest getCachedResponseFromIdRequest = new GetCachedResponseFromIdRequest(
                    authenticatedId.getValue(),
                    idempotencyKey.getValue()
            );
            CachedResponse cachedResponse = authService.getCachedResponseFromId(getCachedResponseFromIdRequest);
            if (cachedResponse.getErrorMessage() != null) {
                System.err.println("Cache retrieval failed: " + cachedResponse.getErrorMessage());
            }
            if (cachedResponse.getErrorMessage() == null && cachedResponse.getResponseJson() != null) {
                AccountStatusResponse accountStatusResponse = gson.fromJson(
                        cachedResponse.getResponseJson(), AccountStatusResponse.class
                );

                return accountStatusResponse;
            }

            Account account = accountRepository.findById(accountId);

            boolean isOwnedByAuthenticatedCustomer = account.getOwnerId().equals(authenticatedId);
            if (!isOwnedByAuthenticatedCustomer) {
                String failureReason = String.format(
                        "Account: %s; Owned: %; User: %s;",
                        request.getAccountId(),
                        isOwnedByAuthenticatedCustomer,
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
            SaveCachedResponseFromIdRequest saveCachedResponseFromIdRequest = new SaveCachedResponseFromIdRequest(
                    authenticatedId.getValue(),
                    idempotencyKey.getValue(),
                    gson.toJson(response),
                    response.getClass().getSimpleName()
            );

            BooleanResponse saved = authService.saveCachedResponseFromId(saveCachedResponseFromIdRequest);
            if (!saved.value()) {
                System.err.println("Cache save failed: " + saved.getErrorMessage());
            }

            return response;

        } catch (IllegalStateException | AccountNotFoundException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.failure(e.getMessage());
            AccountStatusResponse accountStatusResponse = new AccountStatusResponse(
                    null,
                    operationStatus.getValue(),
                    operationStatus.getDescription()
            );

            return accountStatusResponse;
        }
    }
    @Override
    public AccountStatusResponse freezeAccount(FreezeAccountRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        String sessionToken = request.getCurrentSessionToken();
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));

        try {
            AccountId accountId = AccountId.of(request.getAccountId());
            IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

            BooleanResponse tokenValidityResponse =  authService.validateToken(new ValidateTokenRequest(sessionToken));
            boolean isValidToken = tokenValidityResponse.value();
            if (!isValidToken) {
                String failureReason = String.format(
                        "Token: %s; IsValidToken: %s; User: %s;",
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

            GetCachedResponseFromIdRequest getCachedResponseFromIdRequest = new GetCachedResponseFromIdRequest(
                    authenticatedId.getValue(), idempotencyKey.getValue()
            );
            CachedResponse cachedResponse = authService.getCachedResponseFromId(getCachedResponseFromIdRequest);
            if (cachedResponse.getErrorMessage() != null) {
                System.err.println("Cache retrieval failed: " + cachedResponse.getErrorMessage());
            }
            if (cachedResponse.getErrorMessage() == null && cachedResponse.getResponseJson() != null) {
                AccountStatusResponse accountStatusResponse = gson.fromJson(
                        cachedResponse.getResponseJson(), AccountStatusResponse.class
                );

                return accountStatusResponse;
            }

            Account account = accountRepository.findById(accountId);

            boolean isOwnedByAuthenticatedCustomer = account.getOwnerId().equals(authenticatedId);
            if (!isOwnedByAuthenticatedCustomer) {
                String failureReason = String.format(
                        "Account: %s; Owned: %; User: %s;",
                        request.getAccountId(),
                        isOwnedByAuthenticatedCustomer,
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
            SaveCachedResponseFromIdRequest saveCachedResponseFromIdRequest = new SaveCachedResponseFromIdRequest(
                    authenticatedId.getValue(),
                    idempotencyKey.getValue(),
                    gson.toJson(response),
                    response.getClass().getSimpleName()
            );

            BooleanResponse saved = authService.saveCachedResponseFromId(saveCachedResponseFromIdRequest);
            if (!saved.value()) {
                System.err.println("Cache save failed: " + saved.getErrorMessage());
            }

            return response;

        } catch (IllegalStateException | IllegalArgumentException | AccountNotFoundException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.failure(e.getMessage());
            AccountStatusResponse accountStatusResponse = new AccountStatusResponse(
                    null,
                    operationStatus.getValue(),
                    operationStatus.getDescription()
            );

            return accountStatusResponse;
        }
    }
    @Override
    public AccountStatusResponse unfreezeAccount(UnfreezeAccountRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        String sessionToken = request.getCurrentSessionToken();
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));

        try {
            AccountId accountId = AccountId.of(request.getAccountId());
            IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

            BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
            boolean isValidToken = tokenValidityResponse.value();
            if (!isValidToken) {
                String failureReason = String.format(
                        "Token: %s; IsValidToken: %s; User: %s;",
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
            GetCachedResponseFromIdRequest getCachedResponseFromIdRequest = new GetCachedResponseFromIdRequest(
                    authenticatedId.getValue(), idempotencyKey.getValue()
            );
            CachedResponse cachedResponse = authService.getCachedResponseFromId(getCachedResponseFromIdRequest);
            if (cachedResponse.getErrorMessage() != null) {
                System.err.println("Cache retrieval failed: " + cachedResponse.getErrorMessage());
            }
            if (cachedResponse.getErrorMessage() == null && cachedResponse.getResponseJson() != null) {
                AccountStatusResponse accountStatusResponse = gson.fromJson(
                        cachedResponse.getResponseJson(), AccountStatusResponse.class
                );

                return accountStatusResponse;
            }

            Account account = accountRepository.findById(accountId);

            boolean isOwnedByAuthenticatedCustomer = account.getOwnerId().equals(authenticatedId);
            if (!isOwnedByAuthenticatedCustomer) {
                String failureReason = String.format(
                        "Account: %s; Owned: %; User: %s;",
                        request.getAccountId(),
                        isOwnedByAuthenticatedCustomer,
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
            SaveCachedResponseFromIdRequest saveCachedResponseFromIdRequest = new SaveCachedResponseFromIdRequest(
                    authenticatedId.getValue(),
                    idempotencyKey.getValue(),
                    gson.toJson(response),
                    response.getClass().getSimpleName()
            );

            BooleanResponse saved = authService.saveCachedResponseFromId(saveCachedResponseFromIdRequest);
            if (!saved.value()) {
                System.err.println("Cache save failed: " + saved.getErrorMessage());
            }

            return response;

        } catch (IllegalStateException | IllegalArgumentException | AccountNotFoundException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.failure(e.getMessage());
            AccountStatusResponse accountStatusResponse = new AccountStatusResponse(
                    null,
                    operationStatus.getValue(),
                    operationStatus.getDescription()
            );

            return accountStatusResponse;
        }
    }
    @Override
    public AccountResponse getAccountDetails(GetAccountDetailsRequest request) {
        String sessionToken = request.getCurrentSessionToken();
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));

        try {
            AccountId accountId = AccountId.of(request.getAccountId());
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

            BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
            boolean isValidToken = tokenValidityResponse.value();
            if (!isValidToken) {
                String failureReason = String.format(
                        "Token: %s; IsValidToken: %s; User: %s;",
                        request.getCurrentSessionToken(),
                        isValidToken,
                        authenticatedId
                );
                OperationStatus operationStatus = OperationStatus.failure(failureReason);

                return AccountMapper.failureResponse(authResponse.getCustomerId(), operationStatus);
            }

            Account account = accountRepository.findById(accountId);

            boolean isOwnedByAuthenticatedCustomer = account.getOwnerId().equals(authenticatedId);
            if (!isOwnedByAuthenticatedCustomer) {
                String failureReason = String.format(
                        "Account: %s; Owned: %; User: %s;",
                        request.getAccountId(),
                        isOwnedByAuthenticatedCustomer,
                        authenticatedId
                );

                return AccountMapper.failureResponse(failureReason, OperationStatus.FAILURE);
            }

            AccountResponse response = AccountMapper.toResponse(account, OperationStatus.SUCCESS);

            return response;

        } catch (IllegalArgumentException | AccountNotFoundException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.failure(e.getMessage());

            return AccountMapper.failureResponse(authResponse.getCustomerId(), operationStatus);
        }
    }
}
