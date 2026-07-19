package lv.v3nom.application.mapper;

import lv.v3nom.application.dto.requests.RegisterCustomerRequest;
import lv.v3nom.application.dto.responses.AccountResponse;
import lv.v3nom.application.dto.responses.CustomerResponse;
import lv.v3nom.application.dto.responses.RegisterCustomerResponse;
import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.security.PasswordHasher;
import lv.v3nom.domain.value.OperationStatus;
import lv.v3nom.domain.value.Token;

import java.time.LocalDateTime;

public class CustomerMapper {
    public static Customer toDomain(RegisterCustomerRequest request, PasswordHasher hasher, LocalDateTime now) {
        return Customer.register(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getRawPassword(),
                hasher,
                now
        );
    }
    public static CustomerResponse toResponse(Customer customer, OperationStatus operationStatus) {
        return new CustomerResponse(
                customer.getId().getValue(),
                customer.getName(),
                customer.getEmail().getValue(),
                customer.getPhoneNumber().getValue(),
                customer.getCustomerStatus().getValue(),
                operationStatus.getValue(),
                operationStatus.getDescription()
        );
    }
    public static RegisterCustomerResponse toRegisterResponse(Customer customer,
                                                              String sessionToken,
                                                              OperationStatus operationStatus) {

        return new RegisterCustomerResponse(
                customer.getId().getValue(),
                sessionToken,
                customer.getName(),
                customer.getEmail().getValue(),
                customer.getPhoneNumber().getValue(),
                customer.getCustomerStatus().getValue(),
                operationStatus.getValue(),
                operationStatus.getDescription()
        );
    }
    // helpers
    public static CustomerResponse failureResponse(String customerId, OperationStatus operationStatus) {
        return new CustomerResponse(
                customerId,
                null,
                null,
                null,
                null,
                operationStatus.getValue(),
                operationStatus.getDescription()
        );
    }
    public static RegisterCustomerResponse failureRegisterResponse(String customerId, OperationStatus operationStatus) {
        return new RegisterCustomerResponse(
                customerId,
                null,
                null,
                null,
                null,
                null,
                operationStatus.getValue(),
                operationStatus.getDescription()
        );
    }
}
