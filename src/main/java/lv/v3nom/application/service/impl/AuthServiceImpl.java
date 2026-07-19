package lv.v3nom.application.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.*;
import lv.v3nom.application.service.AuthService;
import lv.v3nom.application.service.CustomerService;
import lv.v3nom.domain.value.*;
import lv.v3nom.infrastructure.idempotency.IdempotencyStore;
import lv.v3nom.infrastructure.security.TokenProvider;
import lv.v3nom.infrastructure.security.TokenStore;
import lv.v3nom.infrastructure.time.impl.SystemDateTimeProvider;

public class AuthServiceImpl implements AuthService {
    private final CustomerService customerService;
    private final TokenProvider tokenProvider;
    private final TokenStore tokenStore;
    private final IdempotencyStore idempotencyStore;
    private final Gson gson;

    public AuthServiceImpl(CustomerService customerService,
                           TokenProvider tokenProvider,
                           TokenStore tokenStore,
                           IdempotencyStore idempotencyStore,
                           Gson gson) {

        this.tokenProvider = tokenProvider;
        this.customerService = customerService;
        this.tokenStore = tokenStore;
        this.idempotencyStore = idempotencyStore;
        this.gson = gson;
    }

    @Override
    public LogInResponse login(LogInRequest request) {
        //  1. Check idempotency (prevent duplicate logins)
        //  2. Find customer by email via CustomerService
        //  3. Validate credentials (email + password)
        //  4. Generate new session token
        //  5. Store token in TokenStore
        //  6. Return LogInResponse (token + customer details)
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        try {
            IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
            EmailAddress emailAddress = EmailAddress.of(request.getEmail());

            GetCachedResponseFromEmailRequest getCachedResponseFromEmailRequest = new GetCachedResponseFromEmailRequest(
                    emailAddress.getValue(), idempotencyKey.getValue()
            );
            CachedResponse cachedResponse = getCachedResponseFromEmail(getCachedResponseFromEmailRequest);
            if (cachedResponse.getErrorMessage() != null) {
                System.err.println("Cache retrieval failed: " + cachedResponse.getErrorMessage());
            }
            if (cachedResponse.getErrorMessage() == null || cachedResponse.getResponseJson() != null) {
                LogInResponse logInResponse = gson.fromJson(
                        cachedResponse.getResponseJson(),
                        LogInResponse.class);

                return  logInResponse;
            }

            GetCustomerByEmailRequest getCustomerByEmailRequest = new GetCustomerByEmailRequest(
                    emailAddress.getValue()
            );
            CustomerResponse customerResponse = customerService.getCustomerByEmail(getCustomerByEmailRequest);

            ValidateLoginCredentialsRequest validateLoginCredentialsRequest = new ValidateLoginCredentialsRequest(
                    customerResponse.getCustomerId(),
                    request.getPassword(),
                    emailAddress.getValue()
            );
            BooleanResponse validateLoginResponse = customerService.validateLoginCredentials(validateLoginCredentialsRequest);
            boolean isAuthenticated = validateLoginResponse.value();
            if (!isAuthenticated || validateLoginResponse.getErrorMessage() != null) {
                return new LogInResponse(
                        customerResponse.getCustomerId(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        OperationStatus.FAILURE.getValue(),
                        OperationStatus.FAILURE.isOperational(),
                        validateLoginResponse.getErrorMessage()
                );
            }

            Token sessionToken = tokenProvider.generateToken(
                    CustomerId.of(customerResponse.getCustomerId()), time.now()
            );
            tokenStore.cleanExpired(time.now());
            tokenStore.store(sessionToken);

            LogInResponse logInResponse = new LogInResponse(
                    customerResponse.getCustomerId(),
                    sessionToken.getValue(),
                    customerResponse.getName(),
                    customerResponse.getEmail(),
                    customerResponse.getPhone(),
                    customerResponse.getStatus(),
                    OperationStatus.SUCCESS.getValue(),
                    OperationStatus.SUCCESS.isOperational(),
                    OperationStatus.SUCCESS.getDescription()
            );

            SaveCachedResponseFromEmailRequest saveCachedResponseFromEmailRequest = new SaveCachedResponseFromEmailRequest(
                    emailAddress.getValue(),
                    idempotencyKey.getValue(),
                    gson.toJson(logInResponse),
                    logInResponse.getClass().getSimpleName()
            );
            BooleanResponse saved = saveCachedResponseFromEmail(saveCachedResponseFromEmailRequest);
            if (!saved.value()) {
                System.err.println("Cache save failed: " + saved.getErrorMessage());
            }

            return logInResponse;

        } catch (IllegalArgumentException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    true,
                    e.getMessage()
            );
            LogInResponse logInResponse = new LogInResponse(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    operationStatus.getValue(),
                    false,
                    operationStatus.getDescription()
            );

            return logInResponse;
        }
    }
    @Override
    public BooleanResponse logout(LogOutRequest request) {
        //  1. Check idempotency
        //  2. Remove token from TokenStore
        //  3. Notify CustomerService about logout
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        String sessionToken = request.getCurrentSessionToken();
        AuthResponse authResponse = authenticate(new AuthRequest(sessionToken));

        try {
            IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

            GetCachedResponseFromIdRequest getCachedResponseFromIdRequest = new GetCachedResponseFromIdRequest(
                    authenticatedId.getValue(), idempotencyKey.getValue()
            );
            CachedResponse cachedResponse = getCachedResponseFromId(getCachedResponseFromIdRequest);
            if (cachedResponse.getErrorMessage() != null) {
                System.err.println("Cache retrieval failed: " + cachedResponse.getErrorMessage());
            }
            if (cachedResponse.getErrorMessage() == null || cachedResponse.getResponseJson() != null) {
                BooleanResponse booleanResponse = gson.fromJson(
                        cachedResponse.getResponseJson(),
                        BooleanResponse.class
                );

                return booleanResponse;
            }

            tokenStore.cleanExpired(time.now());
            tokenStore.invalidate(sessionToken);

            BooleanResponse booleanResponse = new BooleanResponse(
                    OperationStatus.SUCCESS.isOperational(),
                    null
            );
            SaveCachedResponseFromIdRequest saveCachedResponseFromIdRequest = new SaveCachedResponseFromIdRequest(
                    authenticatedId.getValue(),
                    idempotencyKey.getValue(),
                    gson.toJson(booleanResponse),
                    booleanResponse.getClass().getSimpleName()
            );
            BooleanResponse isCachedResponseSaved = saveCachedResponseFromId(saveCachedResponseFromIdRequest);

            return isCachedResponseSaved.getErrorMessage() == null ? booleanResponse : isCachedResponseSaved;

        } catch (IllegalArgumentException | JsonSyntaxException e) {
            BooleanResponse booleanResponse = new BooleanResponse(
                    false,
                    e.getMessage()
            );

            return booleanResponse;
        }
    }
    @Override
    public SessionTokenResponse generateToken(GenerateSessionTokenRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        Token sessionToken = tokenProvider.generateToken(
                CustomerId.of(request.getCustomerId()), time.now()
        );

        return new SessionTokenResponse(sessionToken.getValue());
    }
    @Override
    public BooleanResponse validateToken(ValidateTokenRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();
        boolean isValidToken = tokenStore.isValid(request.getTokenValue(), time.now());
        return new BooleanResponse(isValidToken, null);
    }
    @Override
    public AuthResponse authenticate(AuthRequest request) {
        CustomerId customerId = tokenStore.getCustomerId(request.getTokenValue());
        return new AuthResponse(customerId != null ? customerId.getValue() : null);
    }
    @Override
    public CachedResponse getCachedResponseFromId(GetCachedResponseFromIdRequest request) {
        try {
            String cachedResponseJSON = idempotencyStore.retrieve(
                    CustomerId.of(request.getCustomerId()),
                    IdempotencyKey.of(request.getIdempotencyKey())
            );
            if (cachedResponseJSON == null) {
                return null;
            }

            return new CachedResponse(
                    gson.toJson(cachedResponseJSON),
                    cachedResponseJSON.getClass().getSimpleName(),
                    null
            );

        } catch (IllegalArgumentException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    true,
                    e.getMessage()
            );
            CachedResponse cachedResponse = new CachedResponse(
                    null,
                    null,
                    operationStatus.getDescription()
            );

            return cachedResponse;
        }
    }
    @Override
    public CachedResponse getCachedResponseFromEmail(GetCachedResponseFromEmailRequest request) {
        try {
            String cachedResponseJSON = idempotencyStore.retrieve(
                    EmailAddress.of(request.getEmail()),
                    IdempotencyKey.of(request.getIdempotencyKey())
            );
            if (cachedResponseJSON == null) {
                return null;
            }

            return new CachedResponse(
                    gson.toJson(cachedResponseJSON),
                    cachedResponseJSON.getClass().getSimpleName(),
                    null
            );

        } catch (IllegalArgumentException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    true,
                    e.getMessage()
            );
            CachedResponse cachedResponse = new CachedResponse(
                    null,
                    null,
                    operationStatus.getDescription()
            );

            return cachedResponse;
        }
    }
    @Override
    public BooleanResponse saveCachedResponseFromId(SaveCachedResponseFromIdRequest request) {
        try {
            idempotencyStore.storeRaw(
                    CustomerId.of(request.getCustomerId()),
                    IdempotencyKey.of(request.getIdempotencyKey()),
                    request.getResponseJson()
            );

            return new BooleanResponse(true, null);

        } catch (IllegalArgumentException e) {
            return new BooleanResponse(false, e.toString());
        }
    }
    @Override
    public BooleanResponse saveCachedResponseFromEmail(SaveCachedResponseFromEmailRequest request) {
        try {
            idempotencyStore.storeRaw(
                    CustomerId.of(request.getEmail()),
                    IdempotencyKey.of(request.getIdempotencyKey()),
                    request.getResponseJson()
            );

            return new BooleanResponse(true, null);

        } catch (IllegalArgumentException e) {
            return new BooleanResponse(false, e.toString());
        }
    }
}
