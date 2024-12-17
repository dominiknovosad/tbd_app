package com.example.tbd.company;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
    public List<Company> findAllByCompanyName(String company_name);
    public List<Company> findAllByIco(Integer ico);

}
