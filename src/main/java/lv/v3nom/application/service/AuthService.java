package lv.v3nom.application.service;

import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.*;

public interface AuthService {
    LogInResponse login(LogInRequest request);
    BooleanResponse logout(LogOutRequest request);
    SessionTokenResponse generateToken(GenerateSessionTokenRequest request);
    BooleanResponse validateToken(ValidateTokenRequest tokenValue);
    AuthResponse authenticate(AuthRequest tokenValue);
    CachedResponse getCachedResponseFromId(GetCachedResponseFromIdRequest request);
    CachedResponse getCachedResponseFromEmail(GetCachedResponseFromEmailRequest request);
    BooleanResponse saveCachedResponseFromId(SaveCachedResponseFromIdRequest request);
    BooleanResponse saveCachedResponseFromEmail(SaveCachedResponseFromEmailRequest request);
}
