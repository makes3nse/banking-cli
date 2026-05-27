package lv.v3nom.infrastructure.repository.INMEM;

import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.domain.value.EmailAddress;

import java.util.List;

public interface CustomerRepository {
    public void save(Customer customer);
    public Customer findById(CustomerId customerId);
    public List<Customer> findAll();
    public Customer findByEmail(EmailAddress emailAddress);
    public boolean existsByEmail(EmailAddress emailAddress);
}
