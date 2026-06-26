package lv.v3nom.application.service.impl;

import com.google.gson.Gson;
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

        IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
        EmailAddress emailAddress = EmailAddress.of(request.getEmail());

        CachedResponse cachedResponse = getCachedResponseFromId(
                new GetCachedResponseFromIdRequest(emailAddress.getValue(), idempotencyKey.getValue())
        );
        if (cachedResponse != null) {
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
        if (!isAuthenticated) {
            String failureReason = String.format(
                    "Credentials does not match for customer: %s", customerResponse.getCustomerId()
            );

            return new LogInResponse(
                    customerResponse.getCustomerId(),
                    null,
                    null,
                    null,
                    OperationStatus.FAILURE.getValue(),
                    OperationStatus.FAILURE.isOperational(),
                    failureReason
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
                customerResponse.getStatus(),
                customerResponse.getName(),
                OperationStatus.SUCCESS.getValue(),
                OperationStatus.SUCCESS.isOperational(),
                OperationStatus.SUCCESS.getDescription()
        );

        return logInResponse;
    }
    @Override
    public BooleanResponse logout(LogOutRequest request) {
        //  1. Check idempotency
        //  2. Remove token from TokenStore
        //  3. Notify CustomerService about logout
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        String sessionToken = request.getCurrentSessionToken();
        IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
        AuthResponse authResponse = authenticate(new AuthRequest(sessionToken));
        CustomerId customerId = CustomerId.of(authResponse.getCustomerId());

        CachedResponse cachedResponse = getCachedResponseFromId(
                new GetCachedResponseFromIdRequest(customerId.getValue(), idempotencyKey.getValue())
        );
        if (cachedResponse != null) {
            BooleanResponse booleanResponse = gson.fromJson(
                    cachedResponse.getResponseJson(),
                    BooleanResponse.class
            );

            return booleanResponse;
        }
        tokenStore.cleanExpired(time.now());
        tokenStore.invalidate(sessionToken);

        BooleanResponse booleanResponse = new BooleanResponse(OperationStatus.SUCCESS.isOperational());

        return booleanResponse;
    }
    @Override
    public BooleanResponse validateToken(ValidateTokenRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();
        return new BooleanResponse(tokenStore.isValid(request.getTokenValue(), time.now()));
    }
    @Override
    public AuthResponse authenticate(AuthRequest request) {
        // returns CustomerId if token is ok
        CustomerId customerId = tokenStore.getCustomerId(request.getTokenValue());
        if (customerId != null) {
            return new AuthResponse(customerId.getValue());
        }
        return null;
    }
    @Override
    public CachedResponse getCachedResponseFromId(GetCachedResponseFromIdRequest request) {
        String cachedResponseJSON = idempotencyStore.retrieve(
                CustomerId.of(request.getCustomerId()),
                IdempotencyKey.of(request.getIdempotencyKey())
        );
        if (cachedResponseJSON == null) {
            return null;
        }
        return new CachedResponse(
                gson.toJson(cachedResponseJSON),
                cachedResponseJSON.getClass().getSimpleName()
        );
    }
    @Override
    public CachedResponse getCachedResponseFromEmail(GetCachedResponseFromEmailRequest request) {
        String cachedResponseJSON = idempotencyStore.retrieve(
                EmailAddress.of(request.getEmail()),
                IdempotencyKey.of(request.getIdempotencyKey())
        );
        if (cachedResponseJSON == null) {
            return null;
        }
        return new CachedResponse(
                gson.toJson(cachedResponseJSON),
                cachedResponseJSON.getClass().getSimpleName()
        );
    }
    @Override
    public void saveCachedResponseFromId(SaveCachedResponseFromIdRequest request) {
        idempotencyStore.storeRaw(
                CustomerId.of(request.getCustomerId()),
                IdempotencyKey.of(request.getIdempotencyKey()),
                request.getResponseJson()
        );
    }
    @Override
    public void saveCachedResponseFromEmail(SaveCachedResponseFromEmailRequest request) {
        idempotencyStore.storeRaw(
                CustomerId.of(request.getEmail()),
                IdempotencyKey.of(request.getIdempotencyKey()),
                request.getResponseJson()
        );
    }
}
