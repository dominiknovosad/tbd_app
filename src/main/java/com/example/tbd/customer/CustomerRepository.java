package com.example.tbd.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    public List<Customer> findAllByName(String name);
    public List<Customer> findAllBySurname(String surname);
    Optional<Customer> findByEmail(String email);
    boolean existsById(Integer id); // Checks if a customer exists by their ID
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.roleId = 1")
    @Transactional(readOnly = true)
    long countUsersWithRoleUser();
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdAt >= :startTime")
    @Transactional(readOnly = true)
    long countUsersFrom(@Param("startTime") LocalDateTime startTime);
}
