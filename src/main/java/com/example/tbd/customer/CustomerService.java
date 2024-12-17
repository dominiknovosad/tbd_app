package com.example.tbd.customer;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository repository;
    private final PasswordEncoder passwordEncoder;

    // Konštruktor na injekciu závislostí
    public CustomerService(CustomerRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // Nájde zákazníka podľa ID
    public Customer getCustomerById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    // Nájde všetkých zákazníkov
    public List<Customer> getAll() {
        return repository.findAll();
    }

    // Nájde všetkých zákazníkov podľa mena
    public List<Customer> findByAllName(String name) {
        return repository.findAllByName(name);
    }

    // Nájde všetkých zákazníkov podľa priezviska
    public List<Customer> findByAllSurname(String surname) {
        return repository.findAllBySurname(surname);
    }

    // Vytvorí nového zákazníka s šifrovaným heslom
    public Customer createCustomer(Customer customer) {
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword);
        return repository.save(customer);
    }
}
