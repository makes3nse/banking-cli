package lv.v3nom.infrastructure.repository.INMEM.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import lv.v3nom.application.dto.storage.CustomerStorageDTO;
import lv.v3nom.application.mapper.StorageMapper;
import lv.v3nom.domain.exception.CustomerNotFoundException;
import lv.v3nom.domain.model.Account;
import lv.v3nom.domain.model.Customer;
import lv.v3nom.domain.security.PasswordHasher;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerRepositoryImpl implements CustomerRepository {
    private final Map<CustomerId, Customer> customerDatabase = new HashMap<>();
    private final Path storageFile = Paths.get("data", "customers.json");
    private final PasswordHasher hasher;
    private final Gson gson;

    public CustomerRepositoryImpl(Gson gson, PasswordHasher hasher) {
        this.gson = gson;
        this.hasher = hasher;
        try {
            Files.createDirectories(storageFile.getParent());
        } catch (IOException e) {
            System.err.println("Failed to create data directory: " + e.getMessage());
        }
        loadFromFile();
        debugPrintDatabase();
    }

    private void loadFromFile() {
        try {
            if (!Files.exists(storageFile) || Files.size(storageFile) == 0) {
                System.out.println("No customer data found.");
                return;
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        try (Reader reader = Files.newBufferedReader(storageFile)) {
            Type listType = new TypeToken<List<CustomerStorageDTO>>() {}.getType();
            List<CustomerStorageDTO> loadedDTOs = gson.fromJson(reader, listType);

            // TODO -> adapt to new Storage Mappers
            if (loadedDTOs != null) {
                System.out.println("Loading " + loadedDTOs.size() + " customers from file.");

                for (CustomerStorageDTO customer : loadedDTOs) {
                    Customer customerReconstructed = StorageMapper.fromStorageCustomer(customer, hasher);
                    customerDatabase.put(CustomerId.of(customer.getId()), customerReconstructed);
                    System.out.println("  Loaded customer: " + customerReconstructed.getId().getValue() + " -> " + customerReconstructed.getEmail().getValue());
                }
                System.out.println("Total customers loaded: " + customerDatabase.size());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load customers", e);

        } catch (JsonSyntaxException e) {
            System.err.println("Warning: Customer data file is corrupted. Starting with empty database.");

            try {
                Path backup = storageFile.getParent().resolve("customers_backup_" + System.currentTimeMillis() + ".json");
                Files.copy(storageFile, backup);
                System.err.println("Corrupted file backed up to: " + backup);
                Files.delete(storageFile);

            } catch (IOException ioException) {
                System.err.println("Failed to backup corrupted file: " + ioException.getMessage());
            }
        }
    }

    private void saveToFile() {
        try {
            Files.createDirectories(storageFile.getParent());
            try (Writer writer = Files.newBufferedWriter(storageFile)) {
                List<Customer> customers = new ArrayList<>(customerDatabase.values());
                List<CustomerStorageDTO> cDTOs = new ArrayList<>();
                for (Customer c : customers) {
                    cDTOs.add(StorageMapper.toStorageCustomer(c));
                }

                System.out.println("Saving " + customers.size() + " customers to file.");
                System.out.println("CusRepo: saveToFile() -> List<Customer> toSave = " + customers);

                gson.toJson(cDTOs, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save customers", e);
        }
    }

    private void debugPrintDatabase() {
        System.out.println("=== Current Database Contents ===");
        if (customerDatabase.isEmpty()) {
            System.out.println("Database is empty.");
        } else {
            for (Map.Entry<CustomerId, Customer> entry : customerDatabase.entrySet()) {
                System.out.println("  ID: " + entry.getKey().getValue() +
                        " -> Email: " + entry.getValue().getEmail().getValue());
            }
        }
        System.out.println("=== End Database Contents ===");
    }

    @Override
    public void save(Customer customer) {
        System.out.println("Saving customer with ID: " + customer.getId().getValue() +
                " and email: " + customer.getEmail().getValue());
        customerDatabase.put(customer.getId(), customer);
        System.out.println("Database now has " + customerDatabase.size() + " customers.");
        saveToFile();
        debugPrintDatabase();
    }
    @Override
    public Customer findById(CustomerId customerId) {
        System.out.println("Looking for customer by ID: " + customerId.getValue());
        debugPrintDatabase();
        Customer customer = customerDatabase.get(customerId);
        if (customer == null) {
            System.out.println("Customer not found with ID: " + customerId.getValue());
            throw new CustomerNotFoundException(customerId);
        }
        System.out.println("Found customer: " + customer.getEmail().getValue());
        return customer;
    }
    @Override
    public List<Customer> findAll() {
        return List.copyOf(customerDatabase.values());
    }
    @Override
    public Customer findByEmail(EmailAddress emailAddress) {
        System.out.println("Looking for customer by email: " + emailAddress.getValue());
        for (Customer customer : customerDatabase.values()) {
            if (customer.getEmail().equals(emailAddress)) {
                System.out.println("Found customer by email: " + customer.getId().getValue());
                return customer;
            }
        }
        System.out.println("Customer not found by email: " + emailAddress.getValue());
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
