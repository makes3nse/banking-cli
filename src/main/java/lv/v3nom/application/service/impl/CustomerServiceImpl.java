package lv.v3nom.application.service.impl;

import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.*;
import lv.v3nom.application.mapper.CustomerMapper;
import lv.v3nom.application.service.AuthService;
import lv.v3nom.application.service.CustomerService;
import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.security.PasswordHasher;
import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.domain.value.EmailAddress;
import lv.v3nom.domain.value.OperationStatus;
import lv.v3nom.domain.value.Password;
import lv.v3nom.infrastructure.repository.INMEM.CustomerRepository;
import lv.v3nom.infrastructure.security.PermissionChecker;
import lv.v3nom.infrastructure.time.impl.SystemDateTimeProvider;

public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordHasher passwordHasher;
    private final AuthService authService;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               PasswordHasher passwordHasher,
                               AuthService authService) {

        this.customerRepository = customerRepository;
        this.passwordHasher = passwordHasher;
        this.authService = authService;
    }

    @Override
    public CustomerResponse register(RegisterCustomerRequest request) {
        // TODO
        //  create and save CUSTOMER entity
        return null;
    }
    @Override
    public ChangeNameResponse changeName(ChangeNameRequest request) {
        // TODO
        return null;
    }
    @Override
    public ChangeEmailResponse changeEmail(ChangeEmailRequest request) {
        // TODO
        return null;
    }
    @Override
    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        // TODO
        return null;
    }
    @Override
    public ChangePhoneNumberResponse changePhone(ChangePhoneNumberRequest request) {
        // TODO
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
    public CustomerResponse getCustomerByEmail(GetCustomerByEmailRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        EmailAddress emailAddress = EmailAddress.of(request.getEmail());
        Customer customer = customerRepository.findByEmail(emailAddress);

        if (customer == null) {
            return CustomerMapper.failureResponse(emailAddress.getValue(), OperationStatus.FAILURE);
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

    @Override
    public BooleanResponse validateLoginCredentials(ValidateLoginCredentialsRequest request) {
        CustomerId customerId = CustomerId.of(request.getCustomerId());
        String rawPassword = request.getRawPassword();
        EmailAddress emailAddress = EmailAddress.of(request.getEmailAddress());
        Customer customer = customerRepository.findById(customerId);

        if (customer == null) {
            return new BooleanResponse(false);
        }

        boolean isValidPassword = customer.getPassword().matches(rawPassword, passwordHasher);
        boolean emailMatches = customer.getEmail().equals(emailAddress);

        BooleanResponse booleanResponse = new BooleanResponse(isValidPassword && emailMatches);

        return booleanResponse;
    }
}
