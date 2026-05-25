package lv.v3nom.infrastructure.security;

import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.domain.value.Token;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

public class TokenProvider {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;
    private static final long TOKEN_VALIDITY_HOURS = 24;

    public Token generateToken(CustomerId customerId, LocalDateTime now) {
        byte[] randomBytes = new byte[TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(randomBytes);

        String tokenValue = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        LocalDateTime expiry = now.plusHours(TOKEN_VALIDITY_HOURS);

        return Token.create(tokenValue, expiry, customerId, now);
    }
}
