package com.example.tbd.company;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository repository;
    private final PasswordEncoder passwordEncoder;

    // Konštruktor na injekciu závislostí
    public CompanyService(CompanyRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // Nájde firmu podľa ID
    public Company getCompanyById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    // Získa všetky firmy
    public List<Company> getAllCompany() {
        return repository.findAll();
    }

    // Nájde všetky firmy podľa názvu
    public List<Company> findAllByCompanyName(String name) {
        return repository.findAllByCompanyName(name);
    }

    // Nájde všetky firmy podľa IČO
    public List<Company> findAllByIco(Integer ico) {
        return repository.findAllByIco(ico);
    }

    // Vytvorí novú firmu s voliteľným heslom
    public Company createCompany(Company company) {
        if (company.getPassword() != null && !company.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(company.getPassword());
            company.setPassword(encodedPassword);
        }
        return repository.save(company);
    }
}
