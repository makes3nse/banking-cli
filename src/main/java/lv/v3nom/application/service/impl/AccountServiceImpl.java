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

        String sessionToken = request.getCurrentSessionToken();
        String idempotencyKey = request.getIdempotencyKey();
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));

        try {
            Currency currency = Currency.of(request.getCurrency());
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

            BooleanResponse tokenValidityResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
            boolean isValidToken = tokenValidityResponse.value();
            if (!isValidToken) {
                String failureReason = String.format(
                        "Token: %s; IsValidToken: %s; User: %s;",
                        request.getCurrentSessionToken(),
                        isValidToken,
                        authenticatedId // is not null, cause if it would be, CustomerId.of() would throw IllegalArgumentException
                );
                OperationStatus operationStatus = OperationStatus.of(
                        "FAILURE",
                        false,
                        true,
                        failureReason
                );

                return AccountMapper.failureResponse(authResponse.getCustomerId(), operationStatus);
            }

            GetCachedResponseFromIdRequest getCachedResponseFromIdRequest = new GetCachedResponseFromIdRequest(
                    authenticatedId.getValue(), idempotencyKey
            );
            CachedResponse cachedResponse = authService.getCachedResponseFromId(getCachedResponseFromIdRequest);
            if (cachedResponse.getErrorMessage() != null) {
                System.err.println("Cache retrieval failed: " + cachedResponse.getErrorMessage());
            }
            if (cachedResponse.getErrorMessage() == null && cachedResponse.getResponseJson() != null) {
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
            SaveCachedResponseFromIdRequest saveCachedResponseFromIdRequest = new SaveCachedResponseFromIdRequest(
                    authenticatedId.getValue(),
                    request.getIdempotencyKey(),
                    gson.toJson(response),
                    response.getClass().getSimpleName()
            );

            BooleanResponse saved = authService.saveCachedResponseFromId(saveCachedResponseFromIdRequest);
            if (!saved.value()) {
                System.err.println("Cache save failed: " + saved.getErrorMessage());
            }

            return response;

        } catch (IllegalArgumentException | IllegalStateException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    false,
                    e.getMessage()
            );

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

                return transactionService.getFailureResponse(getFailureTransactionRequest);
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
                    pendingTransactionResponse.getTransactionId(), time.now().toString()
            );
            TransactionResponse finalTransactionResponse = transactionService.completeTransaction(
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
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    false,
                    e.getMessage()
            );
            GetFailureTransactionRequest  getFailureTransactionRequest = new GetFailureTransactionRequest(
                    TransactionType.DEPOSIT.getTransactionName(),
                    operationStatus.getDescription(),
                    operationStatus.getValue()
            );

            return transactionService.getFailureResponse(getFailureTransactionRequest);
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

                return transactionService.getFailureResponse(getFailureTransactionRequest);
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
                            pendingTransactionResponse.getTransactionId(), time.now().toString()
            );
            TransactionResponse finalTransactionResponse =
                    transactionService.completeTransaction(completeTransactionRequest);
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
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    false,
                    e.getMessage()
            );
            GetFailureTransactionRequest  getFailureTransactionRequest = new GetFailureTransactionRequest(
                    TransactionType.WITHDRAW.getTransactionName(),
                    operationStatus.getDescription(),
                    operationStatus.getValue()
            );

            return transactionService.getFailureResponse(getFailureTransactionRequest);
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

                return transactionService.getFailureResponse(getFailureTransactionRequest);
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

                return transactionService.getFailureResponse(getFailureTransactionRequest);
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
            TransactionResponse pendingTransactionResponse = transactionService.createTransferTransaction(
                    createPendingTransferTransactionRequest
            );

            sourceAccount.transferToAccount(targetAccount, amount, time.now());
            accountRepository.save(sourceAccount);
            accountRepository.save(targetAccount);

            CompleteTransactionRequest completeTransactionRequest = new CompleteTransactionRequest(
                    pendingTransactionResponse.getTransactionId(), time.now().toString()
            );
            TransactionResponse finalTransactionResponse =
                    transactionService.completeTransaction(completeTransactionRequest);
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
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    false,
                    e.getMessage()
            );
            GetFailureTransactionRequest  getFailureTransactionRequest = new GetFailureTransactionRequest(
                    TransactionType.WITHDRAW.getTransactionName(),
                    operationStatus.getDescription(),
                    operationStatus.getValue()
            );

            return transactionService.getFailureResponse(getFailureTransactionRequest);
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
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    false,
                    e.getMessage()
            );

            return AccountMapper.failureResponseBalance(operationStatus.getDescription(), operationStatus);
        }
    }
    @Override
    public List<AccountResponse> getAccountsByCustomer(GetAccountsRequest request) {
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
                OperationStatus operationStatus = OperationStatus.of(
                        "FAILURE",
                        false,
                        true,
                        failureReason
                );

                return List.of(AccountMapper.failureResponse(
                        authResponse.getCustomerId(),
                        operationStatus)
                );
            }

            List<Account> accounts = accountRepository.findByCustomerId(authenticatedId);
            List<AccountResponse> response = new ArrayList<>();
            for (Account account : accounts) {
                if (account.getOwnerId().equals(authenticatedId)) {
                    response.add(AccountMapper.toResponse(account, OperationStatus.UNKNOWN));
                }
            }

            return response;

        } catch (IllegalArgumentException | AccountNotFoundException e) {
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    true,
                    e.getMessage()
            );

            return List.of(AccountMapper.failureResponse(
                    authResponse.getCustomerId(),
                    operationStatus)
            );
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
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    false,
                    e.getMessage()
            );
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
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    false,
                    e.getMessage()
            );
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
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    false,
                    e.getMessage()
            );
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
                OperationStatus operationStatus = OperationStatus.of(
                        "FAILURE",
                        false,
                        true,
                        failureReason
                );

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
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    false,
                    e.getMessage()
            );

            return AccountMapper.failureResponse(authResponse.getCustomerId(), operationStatus);
        }
    }
}
