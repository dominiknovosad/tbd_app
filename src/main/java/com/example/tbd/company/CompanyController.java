package com.example.tbd.company;

import com.example.tbd.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/company")
@Tag(name = "Company Controller", description = "API pre správu firiem")
public class CompanyController {

    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class); // Define the logger here

    private final CompanyService companyService;
    private final CompanyRepository companyRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * Získanie údajov o spoločnosti podľa ID.
     *
     * @param id ID spoločnosti
     * @return ResponseEntity obsahujúca údaje spoločnosti alebo chybovú správu
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCompanyById(@PathVariable Integer id) {
        Optional<Company> companyOptional = companyRepository.findById(id);
        if (companyOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Spoločnosť s poskytnutým ID neexistuje.");
        }

        Company company = companyOptional.get();

        // Prevod entity Company na CompanyOutputDTO
        CompanyOutputNoPW dto = new CompanyOutputNoPW();
        dto.setId(company.getId());
        dto.setCompanyName(company.getCompanyName());
        dto.setIco(company.getIco());
        dto.setEmail(company.getEmail());
        dto.setTelephone(company.getTelephone());
        dto.setAddress(company.getAddress());

        return ResponseEntity.ok(dto);
    }

    @Autowired
    public CompanyController(CompanyService companyService,
                             CompanyRepository companyRepository,
                             AuthenticationManager authenticationManager,
                             JwtTokenUtil jwtTokenUtil,
                             PasswordEncoder passwordEncoder) {
        this.companyService = companyService;
        this.companyRepository = companyRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    @Operation(summary = "Prihlásenie firmy", description = "Autentifikácia firmy na základe IČO a hesla.")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        logger.debug("Prijatý LoginRequest - IČO: {}, Password: {}", loginRequest.getIco(), loginRequest.getPassword());

        if (loginRequest.getIco() == null || loginRequest.getIco().isEmpty() || loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            logger.warn("Chýbajúce prihlasovacie údaje pre IČO: {}", loginRequest.getIco());
            return ResponseEntity.badRequest().body("Chýbajúce prihlasovacie údaje!");
        }

        try {
            Company company = companyRepository.findByIco(Integer.parseInt(loginRequest.getIco()))
                    .orElseThrow(() -> {
                        logger.warn("Firma nenájdená pre IČO: {}", loginRequest.getIco());
                        return new RuntimeException("Firma nenájdená!");
                    });

            if (!passwordEncoder.matches(loginRequest.getPassword(), company.getPassword())) {
                logger.warn("Nesprávne prihlasovacie údaje pre IČO: {}", loginRequest.getIco());
                return ResponseEntity.status(401).body("Nesprávne prihlasovacie údaje!");
            }

            String token = jwtTokenUtil.generateToken(company.getIco().toString(), company.getId(), company.getIco().toString());
            logger.info("Prihlásenie úspešné. Vygenerovaný token pre firmu: {}", company.getIco());

            return ResponseEntity.ok(new LoginResponse(token, company.getId()));

        } catch (RuntimeException e) {
            logger.error("Chyba pri prihlásení pre IČO: {} - {}", loginRequest.getIco(), e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Neznáma chyba pri prihlásení pre IČO: {} - {}", loginRequest.getIco(), e.getMessage());
            return ResponseEntity.status(500).body("Interná chyba servera!");
        }
    }

    static class LoginResponse {
        private final String token;
        private final Integer companyId;

        public LoginResponse(String token, Integer companyId) {
            this.token = token;
            this.companyId = companyId;
        }

        public String getToken() {
            return token;
        }

        public Integer getCompanyId() {
            return companyId;
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Získa všetky firmy", description = "Vráti zoznam všetkých firiem.")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompany());
    }

    @PostMapping("/register")
    @Operation(summary = "Registruje novú firmu", description = "Uloží novú firmu do systému.")
    public ResponseEntity<Company> saveCompany(@RequestBody Company company) {
        logger.info("DEBUG: Pokus o registráciu firmy - Meno: {} IČO: {}", company.getCompanyName(), company.getIco());
        Company savedCompany = companyService.createCompany(company);
        logger.info("DEBUG: Firma úspešne registrovaná s ID: {}", savedCompany.getId());
        return ResponseEntity.ok(savedCompany);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCompany(@PathVariable Integer id, @RequestBody Company updatedCompany) {
        logger.debug("Prijatý požiadavka na aktualizáciu údajov spoločnosti s ID {}", id);

        // Validácia ID
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("Neplatné ID spoločnosti!");
        }

        // Kontrola, či spoločnosť s daným ID existuje
        Optional<Company> existingCompanyOptional = companyRepository.findById(id);
        if (existingCompanyOptional.isEmpty()) {
            logger.warn("Spoločnosť s ID {} neexistuje.", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Spoločnosť s poskytnutým ID neexistuje.");
        }

        try {
            Company existingCompany = existingCompanyOptional.get();

            // Aktualizácia údajov spoločnosti
            if (updatedCompany.getCompanyName() != null && !updatedCompany.getCompanyName().isEmpty()) {
                existingCompany.setCompanyName(updatedCompany.getCompanyName());
            }
            if (updatedCompany.getIco() != null) {
                existingCompany.setIco(updatedCompany.getIco());
            }
            if (updatedCompany.getEmail() != null && !updatedCompany.getEmail().isEmpty()) {
                existingCompany.setEmail(updatedCompany.getEmail());
            }
            if (updatedCompany.getTelephone() != null && !updatedCompany.getTelephone().isEmpty()) {
                existingCompany.setTelephone(updatedCompany.getTelephone());
            }
            if (updatedCompany.getAddress() != null && !updatedCompany.getAddress().isEmpty()) {
                existingCompany.setAddress(updatedCompany.getAddress());
            }
            if (updatedCompany.getPassword() != null && !updatedCompany.getPassword().isEmpty()) {
                existingCompany.setPassword(updatedCompany.getPassword());
            }

            // Uloženie zmien do databázy
            companyRepository.save(existingCompany);

            logger.info("Údaje spoločnosti s ID {} boli úspešne aktualizované.", id);
            return ResponseEntity.ok("Údaje spoločnosti boli úspešne aktualizované.");
        } catch (Exception e) {
            logger.error("Chyba pri aktualizácii spoločnosti s ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Chyba pri spracovaní požiadavky!");
        }
    }
}
