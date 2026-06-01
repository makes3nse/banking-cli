package lv.v3nom.application.service;

import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.*;

public interface CustomerService {
    public CustomerResponse register(RegisterCustomerRequest request);
    public ChangeNameResponse changeName(ChangeNameRequest request);
    public ChangeEmailResponse changeEmail(ChangeEmailRequest request);
    public ChangePasswordResponse changePassword(ChangePasswordRequest request);
    public ChangePhoneNumberResponse changePhone(ChangePhoneNumberRequest request);
    public CustomerResponse getCustomer(GetCustomerRequest request); // fetch id from token --> fetch repo
}
