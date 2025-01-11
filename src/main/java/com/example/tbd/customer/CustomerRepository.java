package com.example.tbd.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    public List<Customer> findAllByName(String name);
    public List<Customer> findAllBySurname(String surname);
    Optional<Customer> findByEmail(String email);
    boolean existsById(Integer id); // Checks if a customer exists by their ID
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.roleId = 1")
    @Modifying(clearAutomatically = true)
    long countUsersWithRoleUser(); // Metóda na pevno pre role_id = 1
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.createdAt >= :startTime")
    @Modifying(clearAutomatically = true)
    long countUsersFrom(@Param("startTime") LocalDateTime startTime);
}
