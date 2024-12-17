package com.example.tbd.company;

import com.example.tbd.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
    public List<Company> findAllByCompanyName(String company_name);
    public List<Company> findAllByIco(Integer ico);
    Optional<Company> findByIco(Integer ico);
}
