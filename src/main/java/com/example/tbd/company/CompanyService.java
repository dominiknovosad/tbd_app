package com.example.tbd.company;

import com.example.tbd.customer.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository repository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

    @Autowired
    public CompanyService(CompanyRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public Company createCompany(Company company) {
        String encodedPassword = passwordEncoder.encode(company.getPassword());
        company.setPassword(encodedPassword);

        Company savedCompany = repository.save(company);
        logger.info("Nová spoločnosť bola úspešne vytvorená: {}", savedCompany);

        return savedCompany;
    }

    public Company getCompanyById(Integer id) {
        // Vylepšené: Pri neexistujúcej firme vyhodiť výnimku
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Firma s ID " + id + " neexistuje"));
    }

    public List<Company> getAllCompany() {
        logger.info("Načítavam všetky firmy");
        return repository.findAll();
    }

    public List<Company> findAllByCompanyName(String companyName) {
        logger.info("Načítavam firmy podľa názvu: {}", companyName);
        return repository.findAllByCompanyName(companyName);
    }

    public List<Company> findAllByIco(Integer ico) {
        if (ico == null) {
            logger.warn("IČO je neplatné (null)");
            throw new IllegalArgumentException("IČO nemôže byť null");
        }
        logger.info("Načítavam firmy podľa IČO: {}", ico);
        return repository.findAllByIco(ico);
    }

    public Company getCompanyByIco(Integer ico) {
        return repository.findByIco(ico).orElseThrow(() -> new RuntimeException("Firma s IČO " + ico + " nenájdená."));
    }
}
