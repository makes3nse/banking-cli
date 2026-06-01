package lv.v3nom.application.service;

import lv.v3nom.application.dto.requests.AuthRequest;
import lv.v3nom.application.dto.requests.LogOutRequest;
import lv.v3nom.application.dto.responses.AuthResponse;
import lv.v3nom.domain.value.CustomerId;

public interface AuthService {
    public AuthResponse login(AuthRequest request);
    public void logout(LogOutRequest request);
    public CustomerId validateToken(String tokenValue); // Internal use only, not exposed to CLI
}
