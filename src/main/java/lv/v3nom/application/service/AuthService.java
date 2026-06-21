package lv.v3nom.application.service;

import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.AuthResponse;
import lv.v3nom.application.dto.responses.BooleanResponse;
import lv.v3nom.application.dto.responses.CachedResponse;
import lv.v3nom.application.dto.responses.LogInResponse;

public interface AuthService {
    LogInResponse login(LogInRequest request);
    BooleanResponse logout(LogOutRequest request);
    BooleanResponse validateToken(ValidateTokenRequest tokenValue);
    AuthResponse authenticate(AuthRequest tokenValue);
    CachedResponse getCachedResponse(GetCachedResponseRequest request);
    void saveCachedResponse(SaveCachedResponseRequest request);
}
