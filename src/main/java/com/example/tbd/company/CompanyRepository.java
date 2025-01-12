package com.example.tbd.company;

import com.example.tbd.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
    public List<Company> findByCompanyName(String company_name);
    Optional<Company> findByIco(Integer ico);
    public List<Company>findAllByIco(Integer ico);
    public List<Company> findByEmail(String email);
    Optional<Company> findById(Integer id);
    @Query("SELECT COUNT(c) FROM Company c WHERE c.id is not null")
    long countCompany();
    @Query("SELECT COUNT(c) FROM Company c WHERE c.createdAt >= :startTime")
    @Transactional(readOnly = true)
    long countCompanyFrom(@Param("startTime") LocalDateTime startTime);
}
