package com.example.tbd.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    CustomerRepository repository;

    // doplnovanie a prepisovanie medzi sebou
    @Autowired
    public void CustomerRepository(CustomerRepository customerRepository) {
        this.repository = customerRepository;
    }
    // service pre vypisanie person podľa ID
    public Customer getCustomerById(Integer id) {
        return repository.findById(id).orElse(null);
    }
    //service pre vypísanie všeho
    public List<Customer> getAll() {
        return repository.findAll();
    }
    public List<Customer> findByAllName(String name) {
        return repository.findAllByName(name);
    }
    public List<Customer> findByAllSurname(String surname) {
        return repository.findAllBySurname(surname);
    }
    public Customer createCustomer(Customer customer) {
        return repository.save(customer);
    }

}
