package com.example.tbd.company;

import com.example.tbd.customer.CustomerRepository; // Import repository pre prácu so zákazníkmi
import org.slf4j.Logger; // Import loggera pre logovanie informácií
import org.slf4j.LoggerFactory; // Import na vytvorenie inštancie loggera
import org.springframework.beans.factory.annotation.Autowired; // Import pre automatické injektovanie závislostí
import org.springframework.security.crypto.password.PasswordEncoder; // Import pre šifrovanie hesiel
import org.springframework.stereotype.Service; // Anotácia pre označenie triedy ako Spring služby

import java.util.List; // Import pre prácu so zoznamami

@Service // Anotácia označujúca triedu ako Spring službu, ktorú je možné injektovať do iných komponentov
public class CompanyService {

    private final CompanyRepository repository; // Repository pre prístup k databáze firiem
    private final PasswordEncoder passwordEncoder; // PasswordEncoder na šifrovanie hesiel

    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class); // Logger na logovanie informácií, chýb a varovaní

    @Autowired // Automatické injektovanie závislostí do konštruktora
    public CompanyService(CompanyRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository; // Inicializácia repository pre prístup k dátam
        this.passwordEncoder = passwordEncoder; // Inicializácia passwordEncoder pre šifrovanie hesiel
    }

    // Metóda na vytvorenie novej spoločnosti
    public Company createCompany(Company company) {
        // Šifrovanie hesla pred uložením do databázy
        String encodedPassword = passwordEncoder.encode(company.getPassword());
        company.setPassword(encodedPassword); // Nastavenie šifrovaného hesla do objektu

        // Uloženie spoločnosti do databázy
        Company savedCompany = repository.save(company);
        logger.info("Nová spoločnosť bola úspešne vytvorená: {}", savedCompany); // Logovanie úspešného vytvorenia spoločnosti

        return savedCompany; // Vrátenie uloženého objektu spoločnosti
    }

    // Metóda na získanie spoločnosti podľa ID
    public Company getCompanyById(Integer id) {
        // Vylepšené: Pri neexistujúcej firme vyhodiť výnimku s popisom chyby
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Firma s ID " + id + " neexistuje"));
    }

    // Metóda na získanie všetkých firiem
    public List<Company> getAllCompany() {
        logger.info("Načítavam všetky firmy"); // Logovanie pred načítaním všetkých firiem
        return repository.findAll(); // Vrátenie zoznamu všetkých firiem
    }

    // Metóda na získanie všetkých firiem podľa názvu
    public List<Company> findAllByCompanyName(String companyName) {
        logger.info("Načítavam firmy podľa názvu: {}", companyName); // Logovanie pred načítaním firiem podľa názvu
        return repository.findAllByCompanyName(companyName); // Vrátenie zoznamu firiem s daným názvom
    }

    // Metóda na získanie firiem podľa IČO
    public List<Company> findAllByIco(Integer ico) {
        if (ico == null) { // Kontrola platnosti IČO, nemôže byť null
            logger.warn("IČO je neplatné (null)"); // Logovanie varovania pri neplatnom IČO
            throw new IllegalArgumentException("IČO nemôže byť null"); // Vyhodenie výnimky s chybovou správou
        }
        logger.info("Načítavam firmy podľa IČO: {}", ico); // Logovanie pred načítaním firiem podľa IČO
        return repository.findAllByIco(ico); // Vrátenie zoznamu firiem s daným IČO
    }

    // Metóda na získanie spoločnosti podľa IČO
    public Company getCompanyByIco(Integer ico) {
        return repository.findByIco(ico).orElseThrow(() -> new RuntimeException("Firma s IČO " + ico + " nenájdená.")); // Nájde firmu podľa IČO alebo vyhodí výnimku ak neexistuje
    }
}
