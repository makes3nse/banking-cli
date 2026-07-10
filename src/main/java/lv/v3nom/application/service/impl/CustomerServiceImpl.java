package lv.v3nom.application.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lv.v3nom.application.dto.requests.*;
import lv.v3nom.application.dto.responses.*;
import lv.v3nom.application.mapper.CustomerMapper;
import lv.v3nom.application.service.AuthService;
import lv.v3nom.application.service.CustomerService;
import lv.v3nom.domain.exception.CustomerNotFoundException;
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

        try {
            IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
            EmailAddress emailAddress = EmailAddress.of(request.getEmail());

            GetCachedResponseFromEmailRequest getCachedResponseFromEmailRequest = new GetCachedResponseFromEmailRequest(
                    emailAddress.getValue(), idempotencyKey.getValue()
            );
            CachedResponse cachedResponse = authService.getCachedResponseFromEmail(getCachedResponseFromEmailRequest);
            if (cachedResponse.getErrorMessage() != null) {
                System.err.println("Cache retrieval failed: " + cachedResponse.getErrorMessage());
            }
            if (cachedResponse.getErrorMessage() == null && cachedResponse.getResponseJson() != null) {
                CustomerResponse customerResponse = gson.fromJson(
                        cachedResponse.getResponseJson(),
                        CustomerResponse.class
                );

                return customerResponse;
            }

            Customer customer = Customer.register(
                    request.getName(),
                    emailAddress.getValue(),
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

        } catch (IllegalArgumentException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    true,
                    e.getMessage()
            );
            CustomerResponse customerResponse = CustomerMapper.failureResponse(
                    null,
                    operationStatus
            );

            return customerResponse;
        }
    }
    @Override
    public ChangeNameResponse changeName(ChangeNameRequest request) {
        String sessionToken = request.getCurrentSessionToken();
        String nameCurrent = request.getCurrentName();
        String nameNew = request.getNewName();
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));

        try {
            IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

            BooleanResponse isValidTokenResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
            boolean isValidToken = isValidTokenResponse.value();
            if (!isValidToken) {
                String failureReason = String.format(
                        "Token: %s; Validity: %s; User: %s;",
                        request.getCurrentSessionToken(),
                        isValidToken,
                        authenticatedId
                );
                ChangeNameResponse changeNameResponse = new ChangeNameResponse(
                        nameCurrent,
                        OperationStatus.FAILURE.getValue(),
                        failureReason
                );

                return changeNameResponse;
            }

            GetCachedResponseFromIdRequest getCachedResponseFromIdRequest = new GetCachedResponseFromIdRequest(
                    authenticatedId.getValue(), idempotencyKey.getValue()
            );
            CachedResponse cachedResponse = authService.getCachedResponseFromId(getCachedResponseFromIdRequest);
            if (cachedResponse.getErrorMessage() != null) {
                System.err.println("Cache retrieval failed: " + cachedResponse.getErrorMessage());
            }
            if (cachedResponse.getErrorMessage() == null && cachedResponse.getResponseJson() != null) {
                ChangeNameResponse changeNameResponse = gson.fromJson(
                        cachedResponse.getResponseJson(), ChangeNameResponse.class
                );

                return changeNameResponse;
            }

            Customer customer = customerRepository.findById(authenticatedId);
            customer.changeName(nameNew);
            customerRepository.save(customer);

            ChangeNameResponse changeNameResponse = new ChangeNameResponse(
                    nameNew,
                    OperationStatus.SUCCESS.getValue(),
                    OperationStatus.SUCCESS.getDescription()
            );
            SaveCachedResponseFromIdRequest saveCachedResponseFromIdRequest = new SaveCachedResponseFromIdRequest(
                    authenticatedId.getValue(),
                    idempotencyKey.getValue(),
                    gson.toJson(changeNameResponse),
                    changeNameResponse.getClass().getSimpleName()
            );
            authService.saveCachedResponseFromId(saveCachedResponseFromIdRequest);

            return changeNameResponse;

        } catch (IllegalArgumentException | CustomerNotFoundException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    true,
                    e.getMessage()
            );
            ChangeNameResponse changeNameResponse = new ChangeNameResponse(
                    nameCurrent,
                    operationStatus.getValue(),
                    operationStatus.getDescription()
            );

            return changeNameResponse;
        }
    }
    @Override
    public ChangeEmailResponse changeEmail(ChangeEmailRequest request) {
        String sessionToken = request.getCurrentSessionToken();
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));

        try {
            IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
            EmailAddress emailCurrent = EmailAddress.of(request.getCurrentEmail());
            EmailAddress emailNew = EmailAddress.of(request.getNewEmail());
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

            BooleanResponse isValidTokenResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
            boolean isValidToken = isValidTokenResponse.value();
            if (!isValidToken) {
                String failureReason = String.format(
                        "Token: %s; Validity: %s; User: %s;",
                        request.getCurrentSessionToken(),
                        isValidToken,
                        authenticatedId
                );
                ChangeEmailResponse changeEmailResponse = new ChangeEmailResponse(
                        emailCurrent.getValue(),
                        OperationStatus.FAILURE.getValue(),
                        failureReason
                );

                return changeEmailResponse;
            }

            GetCachedResponseFromIdRequest getCachedResponseFromIdRequest = new GetCachedResponseFromIdRequest(
                    authenticatedId.getValue(), idempotencyKey.getValue()
            );
            CachedResponse cachedResponse = authService.getCachedResponseFromId(getCachedResponseFromIdRequest);
            if (cachedResponse.getErrorMessage() != null) {
                System.err.println("Cache retrieval failed: " + cachedResponse.getErrorMessage());
            }
            if (cachedResponse.getErrorMessage() == null && cachedResponse.getResponseJson() != null) {
                ChangeEmailResponse changeEmailResponse = gson.fromJson(
                        cachedResponse.getResponseJson(), ChangeEmailResponse.class
                );

                return changeEmailResponse;
            }

            Customer customer = customerRepository.findById(authenticatedId);
            customer.changeEmail(emailNew);
            customerRepository.save(customer);

            ChangeEmailResponse changeEmailResponse = new ChangeEmailResponse(
                    emailNew.getValue(), OperationStatus.SUCCESS.getValue(), OperationStatus.SUCCESS.getDescription()
            );
            SaveCachedResponseFromIdRequest saveCachedResponseFromIdRequest = new SaveCachedResponseFromIdRequest(
                    authenticatedId.getValue(),
                    idempotencyKey.getValue(),
                    gson.toJson(changeEmailResponse),
                    changeEmailResponse.getClass().getSimpleName()
            );
            authService.saveCachedResponseFromId(saveCachedResponseFromIdRequest);

            return changeEmailResponse;

        } catch (IllegalArgumentException | CustomerNotFoundException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    true,
                    e.getMessage()
            );
            ChangeEmailResponse changeEmailResponse = new ChangeEmailResponse(
                    request.getCurrentEmail(),
                    operationStatus.getValue(),
                    operationStatus.getDescription()
            );

            return changeEmailResponse;
        }
    }
    @Override
    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        String sessionToken = request.getCurrentSessionToken();
        String passwordRawCurrent = request.getCurrentPassword();
        String passwordRawNew = request.getNewPassword();
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));

        try {
            IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

            BooleanResponse isValidTokenResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
            boolean isValidToken = isValidTokenResponse.value();
            if (!isValidToken) {
                String failureReason = String.format(
                        "Token: %s; Validity: %s; User: %s;",
                        request.getCurrentSessionToken(),
                        isValidToken,
                        authenticatedId
                );
                ChangePasswordResponse changePasswordResponse = new ChangePasswordResponse(
                        OperationStatus.FAILURE.getValue(),
                        failureReason
                );

                return changePasswordResponse;
            }

            GetCachedResponseFromIdRequest getCachedResponseFromIdRequest = new GetCachedResponseFromIdRequest(
                    authenticatedId.getValue(), idempotencyKey.getValue()
            );
            CachedResponse cachedResponse = authService.getCachedResponseFromId(getCachedResponseFromIdRequest);
            if (cachedResponse.getErrorMessage() != null) {
                System.err.println("Cache retrieval failed: " + cachedResponse.getErrorMessage());
            }
            if (cachedResponse.getErrorMessage() == null && cachedResponse.getResponseJson() != null) {
                ChangePasswordResponse changePasswordResponse = gson.fromJson(
                        cachedResponse.getResponseJson(),
                        ChangePasswordResponse.class
                );

                return changePasswordResponse;
            }

            Customer customer = customerRepository.findById(authenticatedId);
            customer.changePassword(passwordRawCurrent, passwordRawNew, passwordHasher);
            customerRepository.save(customer);

            ChangePasswordResponse changePasswordResponse = new ChangePasswordResponse(
                    OperationStatus.SUCCESS.getValue(), OperationStatus.SUCCESS.getDescription()
            );
            SaveCachedResponseFromIdRequest saveCachedResponseFromIdRequest = new SaveCachedResponseFromIdRequest(
                    authenticatedId.getValue(),
                    idempotencyKey.getValue(),
                    gson.toJson(changePasswordResponse),
                    changePasswordResponse.getClass().getSimpleName()
            );
            authService.saveCachedResponseFromId(saveCachedResponseFromIdRequest);

            return changePasswordResponse;

        } catch (IllegalArgumentException | CustomerNotFoundException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    true,
                    e.getMessage()
            );
            ChangePasswordResponse changePasswordResponse = new ChangePasswordResponse(
                    operationStatus.getValue(),
                    operationStatus.getDescription()
            );

            return changePasswordResponse;
        }
    }
    @Override
    public ChangePhoneNumberResponse changePhone(ChangePhoneNumberRequest request) {
        String sessionToken = request.getCurrentSessionToken();
        AuthResponse authResponse = authService.authenticate(new AuthRequest(sessionToken));

        try {
            IdempotencyKey idempotencyKey = IdempotencyKey.of(request.getIdempotencyKey());
            PhoneNumber phoneNumberCurrent = PhoneNumber.of(request.getCurrentPhone());
            PhoneNumber phoneNumberNew = PhoneNumber.of(request.getNewPhone());
            CustomerId authenticatedId = CustomerId.of(authResponse.getCustomerId());

            BooleanResponse isValidTokenResponse = authService.validateToken(new ValidateTokenRequest(sessionToken));
            boolean isValidToken = isValidTokenResponse.value();
            if (!isValidToken) {
                String failureReason = String.format(
                        "Token: %s; Validity: %s; User: %s;",
                        request.getCurrentSessionToken(),
                        isValidToken,
                        authenticatedId
                );
                ChangePhoneNumberResponse changePhoneNumberResponse = new ChangePhoneNumberResponse(
                        phoneNumberCurrent.getValue(),
                        OperationStatus.FAILURE.getValue(),
                        failureReason
                );

                return changePhoneNumberResponse;
            }

            GetCachedResponseFromIdRequest getCachedResponseFromIdRequest = new GetCachedResponseFromIdRequest(
                    authenticatedId.getValue(), idempotencyKey.getValue()
            );
            CachedResponse cachedResponse = authService.getCachedResponseFromId(getCachedResponseFromIdRequest);
            if (cachedResponse.getErrorMessage() != null) {
                System.err.println("Cache retrieval failed: " + cachedResponse.getErrorMessage());
            }
            if (cachedResponse.getErrorMessage() == null && cachedResponse.getResponseJson() != null) {
                ChangePhoneNumberResponse changePhoneNumberResponse = gson.fromJson(
                        cachedResponse.getResponseJson(), ChangePhoneNumberResponse.class
                );

                return changePhoneNumberResponse;
            }

            Customer customer = customerRepository.findById(authenticatedId);
            customer.changePhoneNumber(phoneNumberNew);
            customerRepository.save(customer);

            ChangePhoneNumberResponse changePhoneNumberResponse = new ChangePhoneNumberResponse(
                    phoneNumberNew.getValue(), OperationStatus.SUCCESS.getValue(), OperationStatus.SUCCESS.getDescription()
            );
            SaveCachedResponseFromIdRequest saveCachedResponseFromIdRequest = new SaveCachedResponseFromIdRequest(
                    authenticatedId.getValue(),
                    idempotencyKey.getValue(),
                    gson.toJson(changePhoneNumberResponse),
                    changePhoneNumberResponse.getClass().getSimpleName()
            );
            authService.saveCachedResponseFromId(saveCachedResponseFromIdRequest);

            return changePhoneNumberResponse;

        } catch (IllegalArgumentException | CustomerNotFoundException | JsonSyntaxException e) {
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    true,
                    e.getMessage()
            );
            ChangePhoneNumberResponse changePhoneNumberResponse = new ChangePhoneNumberResponse(
                    null,
                    operationStatus.getValue(),
                    operationStatus.getDescription()
            );

            return changePhoneNumberResponse;
        }
    }
    @Override
    public CustomerResponse getCustomer(GetCustomerRequest request) {
            AuthRequest authRequest = new AuthRequest(request.getCurrentSessionToken());
            AuthResponse authResponse = authService.authenticate(authRequest);

        try {
            CustomerId customerId = CustomerId.of(authResponse.getCustomerId());

            Customer customer = customerRepository.findById(customerId);

            return CustomerMapper.toResponse(customer, OperationStatus.SUCCESS);

        } catch (CustomerNotFoundException | IllegalArgumentException e) {
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    true,
                    e.getMessage()
            );
            CustomerResponse customerResponse = CustomerMapper.failureResponse(
                    authResponse.getCustomerId(),
                    operationStatus
            );

            return customerResponse;
        }
    }
    @Override
    public CustomerResponse getCustomerByEmail(GetCustomerByEmailRequest request) {
        try {
            EmailAddress emailAddress = EmailAddress.of(request.getEmail());

            Customer customer = customerRepository.findByEmail(emailAddress);

            return CustomerMapper.toResponse(customer, OperationStatus.SUCCESS);

        } catch (IllegalArgumentException | CustomerNotFoundException e) {
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    true,
                    e.getMessage()
            );
            CustomerResponse customerResponse = CustomerMapper.failureResponse(
                    request.getEmail(),
                    operationStatus
            );

            return customerResponse;
        }
    }
    @Override
    public BooleanResponse canManipulateTransactions(CanCustomerManipulateTransactionsRequest request) {
        AuthRequest authRequest = new AuthRequest(request.getCurrentSessionToken());
        AuthResponse authResponse = authService.authenticate(authRequest);

        try {
            CustomerId customerId = CustomerId.of(authResponse.getCustomerId());

            Customer customer = customerRepository.findById(customerId);

            return new BooleanResponse(
                    PermissionChecker.canReturnOrRejectTransaction(customer),
                    null
            );

        } catch (CustomerNotFoundException | IllegalArgumentException e) {
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    true,
                    e.getMessage()
            );

            return new BooleanResponse(false, operationStatus.getDescription());
        }
    }

    @Override
    public BooleanResponse validateLoginCredentials(ValidateLoginCredentialsRequest request) {
        String rawPassword = request.getRawPassword();

        try {
            CustomerId customerId = CustomerId.of(request.getCustomerId());
            EmailAddress emailAddress = EmailAddress.of(request.getEmailAddress());

            Customer customer = customerRepository.findById(customerId);

            boolean isValidPassword = customer.getPassword().matches(rawPassword, passwordHasher);
            boolean emailMatches = customer.getEmail().equals(emailAddress);

            BooleanResponse booleanResponse = new BooleanResponse(
                    (isValidPassword && emailMatches),
                    null
            );

            return booleanResponse;

        } catch (IllegalArgumentException | CustomerNotFoundException e) {
            OperationStatus operationStatus = OperationStatus.of(
                    "FAILURE",
                    false,
                    true,
                    e.getMessage()
            );

            return new BooleanResponse(false, operationStatus.getDescription());
        }
    }
}
