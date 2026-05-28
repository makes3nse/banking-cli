package lv.v3nom.infrastructure.repository.INMEM.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lv.v3nom.domain.exception.CustomerNotFoundException;
import lv.v3nom.domain.model.Account;
import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.value.AccountId;
import lv.v3nom.domain.value.CustomerId;
import lv.v3nom.domain.value.EmailAddress;
import lv.v3nom.infrastructure.repository.INMEM.CustomerRepository;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerRepositoryImpl implements CustomerRepository {
    private final Map<CustomerId, Customer> customerDatabase = new HashMap<>();
    private final Path storageFile = Paths.get("data", "customers.json");
    private final Gson gson = new Gson();

    public CustomerRepositoryImpl() {
        loadFromFile();
    }

    private void loadFromFile() {
        if (!Files.exists(storageFile)) return;
        try (Reader reader = Files.newBufferedReader(storageFile)) {
            Type type = new TypeToken<Map<String, Customer>>() {}.getType();
            Map<String, Customer> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                loaded.forEach((idString, customer) ->
                        customerDatabase.put(CustomerId.of(idString), customer)
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load customers", e);
        }
    }
    private void saveToFile() {
        try {
            Files.createDirectories(storageFile.getParent());
            try (Writer writer = Files.newBufferedWriter(storageFile)) {
                Map<String, Customer> toSave = new HashMap<>();
                customerDatabase.forEach((customerId, customer) ->
                        toSave.put(customerId.toString(), customer)
                );
                gson.toJson(toSave, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save customers", e);
        }
    }

    @Override
    public void save(Customer customer) {
        customerDatabase.put(customer.getId(), customer);
        saveToFile();
    }
    @Override
    public Customer findById(CustomerId customerId) {
        Customer customer = customerDatabase.get(customerId);
        if (customer == null) throw new CustomerNotFoundException(customerId);
        return customer;
    }
    @Override
    public List<Customer> findAll() {
        return List.copyOf(customerDatabase.values());
    }
    @Override
    public Customer findByEmail(EmailAddress emailAddress) {
        for (Customer customer : customerDatabase.values()) {
            if (customer.getEmail().equals(emailAddress)) {
                return customer;
            }
        }
        throw new CustomerNotFoundException(emailAddress);
    }
    @Override
    public boolean existsByEmail(EmailAddress emailAddress) {
        return customerDatabase.values()
                .stream()
                .anyMatch(
                        customer -> customer.getEmail().equals(emailAddress));
    }
}
