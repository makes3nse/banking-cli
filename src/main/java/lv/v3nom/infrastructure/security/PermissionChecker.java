package lv.v3nom.infrastructure.security;

import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.value.Role;

public class PermissionChecker {
    public static boolean hasLowElevatedRights(Customer customer) {
        return customer.getRole() == Role.AUDITOR ||
                customer.getRole() == Role.SUPPORT_AGENT;
    }
    public static boolean hasHighElevatedRights(Customer customer) {
        return customer.getRole() == Role.TECHNICAL_ADMINISTRATOR;
    }
    public static boolean canReturnOrRejectTransaction(Customer customer) {
        return customer.getRole() == Role.TECHNICAL_ADMINISTRATOR ||
                customer.getRole() == Role.SUPPORT_AGENT;
    }
    public static boolean canChangeCustomerStatus(Customer customer) {
        return customer.getRole() == Role.TECHNICAL_ADMINISTRATOR;
    }
    public static boolean canChangeAccountStatus(Customer customer) {
        return customer.getRole() == Role.TECHNICAL_ADMINISTRATOR;
    }

    //  TODO
}
