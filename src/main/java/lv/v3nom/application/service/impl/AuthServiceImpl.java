package lv.v3nom.application.service.impl;

import com.google.gson.Gson;
import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.AuthResponse;
import lv.v3nom.application.dto.responses.BooleanResponse;
import lv.v3nom.application.dto.responses.CachedResponse;
import lv.v3nom.application.dto.responses.LogInResponse;
import lv.v3nom.application.service.AuthService;
import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.domain.value.IdempotencyKey;
import lv.v3nom.infrastructure.idempotency.IdempotencyStore;
import lv.v3nom.infrastructure.security.TokenStore;
import lv.v3nom.infrastructure.time.impl.SystemDateTimeProvider;

public class AuthServiceImpl implements AuthService {
    private final TokenStore tokenStore;
    private final IdempotencyStore idempotencyStore;
    private final Gson gson;

    public AuthServiceImpl(TokenStore tokenStore,
                           IdempotencyStore idempotencyStore,
                           Gson gson) {

        this.tokenStore = tokenStore;
        this.idempotencyStore = idempotencyStore;
        this.gson = gson;
    }

    @Override
    public LogInResponse login(AuthRequest request) {
        return null;
    }
    @Override
    public void logout(LogOutRequest request) {

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
    public CachedResponse getCachedResponse(GetCachedResponseRequest request) {
        Object cachedResponse = idempotencyStore.retrieve(
                CustomerId.of(request.getCustomerId()),
                IdempotencyKey.of(request.getIdempotencyKey())
        );
        if (cachedResponse == null) {
            return null;
        }
        return new CachedResponse(
                gson.toJson(cachedResponse),
                cachedResponse.getClass().getSimpleName()
        );
    }
    @Override
    public void saveCachedResponse(SaveCachedResponseRequest request) {
        idempotencyStore.storeRaw(
                CustomerId.of(request.getCustomerId()),
                IdempotencyKey.of(request.getIdempotencyKey()),
                request.getResponseJson()
        );
    }
}
