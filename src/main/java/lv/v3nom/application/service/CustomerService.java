package lv.v3nom.application.service;

import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.*;

public interface CustomerService {
    CustomerResponse register(RegisterCustomerRequest request);
    ChangeNameResponse changeName(ChangeNameRequest request);
    ChangeEmailResponse changeEmail(ChangeEmailRequest request);
    ChangePasswordResponse changePassword(ChangePasswordRequest request);
    ChangePhoneNumberResponse changePhone(ChangePhoneNumberRequest request);
    CustomerResponse getCustomer(GetCustomerRequest request); // fetch id from token --> fetch repo
    BooleanResponse canManipulateTransactions(CanCustomerManipulateTransactionsRequest request);
}
