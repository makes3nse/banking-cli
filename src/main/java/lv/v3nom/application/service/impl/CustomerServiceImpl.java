package lv.v3nom.application.service.impl;

import com.google.gson.Gson;
import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.*;
import lv.v3nom.application.mapper.CustomerMapper;
import lv.v3nom.application.service.AuthService;
import lv.v3nom.application.service.CustomerService;
import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.security.PasswordHasher;
import lv.v3nom.domain.value.*;
import lv.v3nom.infrastructure.repository.INMEM.CustomerRepository;
import lv.v3nom.infrastructure.security.PermissionChecker;
import lv.v3nom.infrastructure.time.impl.SystemDateTimeProvider;

public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordHasher passwordHasher;
    private final AuthService authService;
    private final Gson gson;

    public CustomerServiceImpl(CustomerRepository customerRepository,
                               PasswordHasher passwordHasher,
                               AuthService authService,
                               Gson gson) {

        this.customerRepository = customerRepository;
        this.passwordHasher = passwordHasher;
        this.authService = authService;
        this.gson = gson;
    }

    @Override
    public CustomerResponse register(RegisterCustomerRequest request) {
        SystemDateTimeProvider time = new SystemDateTimeProvider();

        IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());

        GetCachedResponseFromEmailRequest getCachedResponseFromEmailRequest = new GetCachedResponseFromEmailRequest(
                request.getEmail(), idempotencyKey.getValue()
        );
        CachedResponse cachedResponse = authService.getCachedResponseFromEmail(getCachedResponseFromEmailRequest);
        if (cachedResponse != null) {
            CustomerResponse customerResponse = gson.fromJson(
                    cachedResponse.getResponseJson(),
                    CustomerResponse.class
            );

            return customerResponse;
        }

        Customer customer = Customer.register(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getRawPassword(),
                passwordHasher,
                time.now()
        );
        customerRepository.save(customer);
        CustomerResponse customerResponse = CustomerMapper.toResponse(customer, OperationStatus.SUCCESS);
        SaveCachedResponseFromEmailRequest saveCachedResponseFromEmailRequest = new SaveCachedResponseFromEmailRequest(
                customer.getEmail().getValue(),
                idempotencyKey.getValue(),
                gson.toJson(customerResponse),
                customerResponse.getClass().getSimpleName()
        );
        authService.saveCachedResponseFromEmail(saveCachedResponseFromEmailRequest);

        return customerResponse;
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
