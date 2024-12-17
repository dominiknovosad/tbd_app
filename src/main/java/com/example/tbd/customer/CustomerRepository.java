package com.example.tbd.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    public List<Customer> findAllByName(String name);
    public List<Customer> findAllBySurname(String surname);
    Optional<Customer> findByEmail(String email);

}
