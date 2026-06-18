package lv.v3nom.application.service.impl;

import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.*;
import lv.v3nom.application.mapper.CustomerMapper;
import lv.v3nom.application.service.AuthService;
import lv.v3nom.application.service.CustomerService;
import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.domain.value.OperationStatus;
import lv.v3nom.infrastructure.repository.INMEM.CustomerRepository;
import lv.v3nom.infrastructure.security.PermissionChecker;

public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final AuthService authService;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               AuthService authService) {

        this.customerRepository = customerRepository;
        this.authService = authService;
    }

    @Override
    public CustomerResponse register(RegisterCustomerRequest request) {
        return null;
    }
    @Override
    public ChangeNameResponse changeName(ChangeNameRequest request) {
        return null;
    }
    @Override
    public ChangeEmailResponse changeEmail(ChangeEmailRequest request) {
        return null;
    }
    @Override
    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        return null;
    }
    @Override
    public ChangePhoneNumberResponse changePhone(ChangePhoneNumberRequest request) {
        return null;
    }
    @Override
    public CustomerResponse getCustomer(GetCustomerRequest request) {
        AuthRequest authRequest = new AuthRequest(request.getCurrentSessionToken());
        AuthResponse authResponse = authService.authenticate(authRequest);
        CustomerId customerId = CustomerId.of(authResponse.getCustomerId());
        Customer customer = customerRepository.findById(customerId);

        if (customer == null) {
            return CustomerMapper.failureResponse(authResponse.getCustomerId(), OperationStatus.FAILURE);
        }
        return CustomerMapper.toResponse(customer, OperationStatus.SUCCESS);
    }
    @Override
    public BooleanResponse canManipulateTransactions(CanCustomerManipulateTransactionsRequest request) {
        AuthRequest authRequest = new AuthRequest(request.getCurrentSessionToken());
        AuthResponse authResponse = authService.authenticate(authRequest);
        CustomerId customerId = CustomerId.of(authResponse.getCustomerId());
        Customer customer = customerRepository.findById(customerId);

        if (customer == null) {
            return new BooleanResponse(false);
        }
        return new BooleanResponse(PermissionChecker.canReturnOrRejectTransaction(customer));
    }
}
