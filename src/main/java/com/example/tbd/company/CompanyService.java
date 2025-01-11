package com.example.tbd.company;

import com.example.tbd.customer.CustomerRepository; // Import repository pre prácu so zákazníkmi
import org.slf4j.Logger; // Import loggera pre logovanie informácií
import org.slf4j.LoggerFactory; // Import na vytvorenie inštancie loggera
import org.springframework.beans.factory.annotation.Autowired; // Import pre automatické injektovanie závislostí
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder; // Import pre šifrovanie hesiel
import org.springframework.stereotype.Service; // Anotácia pre označenie triedy ako Spring služby

import java.time.LocalDateTime;
import java.util.List; // Import pre prácu so zoznamami
import java.util.Optional;
import java.util.stream.Collectors;

@Service // Anotácia označujúca triedu ako Spring službu, ktorú je možné injektovať do iných komponentov
public class CompanyService {

    private final CompanyRepository companyRepository; // Repository pre prístup k databáze firiem
    private final PasswordEncoder passwordEncoder; // PasswordEncoder na šifrovanie hesiel

    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class); // Logger na logovanie informácií, chýb a varovaní
    public long countCompany() {
        return companyRepository.countCompany();
    }
    public long countCompanyLast24Hours() {
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        return companyRepository.countCompanyFrom(last24Hours);
    }
    public long countCompanyLast7Days() {
        LocalDateTime last7Days = LocalDateTime.now().minusDays(7);
        return companyRepository.countCompanyFrom(last7Days);
    }
    public long countCompanyLast30Days() {
        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);
        return companyRepository.countCompanyFrom(last30Days);
    }
    public long countCompanyLast365Days() {
        LocalDateTime last365Days = LocalDateTime.now().minusDays(365);
        return companyRepository.countCompanyFrom(last365Days);
    }
    @Autowired // Automatické injektovanie závislostí do konštruktora
    public CompanyService(CompanyRepository repository, PasswordEncoder passwordEncoder) {
        this.companyRepository = repository; // Inicializácia repository pre prístup k dátam
        this.passwordEncoder = passwordEncoder; // Inicializácia passwordEncoder pre šifrovanie hesiel
    }

    // Metóda na vytvorenie novej spoločnosti
    public Company createCompany(Company company) {

        // Šifrovanie hesla pred uložením do databázy
        String encodedPassword = passwordEncoder.encode(company.getPassword());
        company.setPassword(encodedPassword); // Nastavenie šifrovaného hesla do objektu

        // Uloženie spoločnosti do databázy
        Company savedCompany = companyRepository.save(company);
        logger.info("Nová spoločnosť bola úspešne vytvorená: {}", savedCompany); // Logovanie úspešného vytvorenia spoločnosti

        return savedCompany; // Vrátenie uloženého objektu spoločnosti
    }

    // Metóda na získanie všetkých firiem
    public List<CompanyOutputNoPW> getAllCompany() {
        logger.info("Načítavam všetky firmy:");
        List<Company> companies = companyRepository.findAll(); // Načítanie všetkých spoločností
        return companies.stream()
                .map(company -> {
                    CompanyOutputNoPW output = new CompanyOutputNoPW();
                    output.setId(company.getId());
                    output.setCompanyName(company.getCompanyName());
                    output.setIco(company.getIco());
                    output.setEmail(company.getEmail());
                    output.setTelephone(company.getTelephone());
                    output.setAddress(company.getAddress());
                    return output;
                })
                .collect(Collectors.toList());
    }

    public List<CompanyOutputNoPW> getByEmail(String email) {
        logger.info("Načítavam firmy s e-mailom: {}", email); // Logovanie pre načítanie firiem podľa e-mailu
        List<Company> companies = companyRepository.findByEmail(email);
        if (companies.isEmpty()) {
            logger.warn("Nebola nájdená žiadna firma s e-mailom: {}", email);
        } else {
            logger.info("Nájdené firmy: {}", companies);
        }
        return companies.stream()
                .map(company -> {
                    CompanyOutputNoPW output = new CompanyOutputNoPW();
                    output.setId(company.getId());
                    output.setCompanyName(company.getCompanyName());
                    output.setIco(company.getIco());
                    output.setEmail(company.getEmail());
                    output.setTelephone(company.getTelephone());
                    output.setAddress(company.getAddress());
                    return output;
                })
                .collect(Collectors.toList());
    }

    // Metóda na získanie všetkých firiem podľa názvu
    public List<CompanyOutputNoPW> getByCompanyName(String companyName) {
        logger.info("Načítavam firmy podľa názvu: {}", companyName); // Logovanie pred načítaním firiem podľa názvu
        List<Company> companies = companyRepository.findByCompanyName(companyName);
        if (companies.isEmpty()) {
            logger.warn("Nebola nájdená žiadna firma s názovom: {}", companyName);
        } else {
            logger.info("Nájdené firmy: {}", companies);
        }
        return companies.stream()
                .map(company -> {
                    CompanyOutputNoPW output = new CompanyOutputNoPW();
                    output.setId(company.getId());
                    output.setCompanyName(company.getCompanyName());
                    output.setIco(company.getIco());
                    output.setEmail(company.getEmail());
                    output.setTelephone(company.getTelephone());
                    output.setAddress(company.getAddress());
                    return output;
                })
                .collect(Collectors.toList());
    }

    // Metóda na získanie spoločnosti podľa IČO
    public List<CompanyOutputNoPW> getCompanyByIco(Integer ico) {
        logger.info("Načítavam firmy podľa IČO: {}", ico); // Logovanie pred načítaním firiem
        List<Company> companies = companyRepository.findAllByIco(ico);
        if (companies.isEmpty()) {
            logger.warn("Nebola nájdená žiadna firma s IČO: {}", ico);
        } else {
            for (Company company : companies) {
                logger.info("Nájdená firma: IČO: {}, Názov: {}", company.getIco(), company.getCompanyName());
            }
        }
        return companies.stream()
                .map(company -> {
                    CompanyOutputNoPW output = new CompanyOutputNoPW();
                    output.setId(company.getId());
                    output.setCompanyName(company.getCompanyName());
                    output.setIco(company.getIco());
                    output.setEmail(company.getEmail());
                    output.setTelephone(company.getTelephone());
                    output.setAddress(company.getAddress());
                    return output;
                })
                .collect(Collectors.toList());
    }


}