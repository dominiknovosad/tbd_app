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
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setCompanyName(company.getCompanyName());
        dto.setIco(company.getIco());
        dto.setEmail(company.getEmail());
        dto.setTelephone(company.getTelephone());
        dto.setAddress(company.getAddress());

        return ResponseEntity.ok(dto);
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
        private final Long companyId;

        public LoginResponse(String token, Long companyId) {
            this.token = token;
            this.companyId = companyId;
        }
        public String getToken() {
            return token;
        }
        public Long getCompanyId() {
            return companyId;
        }
    }

    @GetMapping("/count")
    @Operation(summary = "Počet firiem", description = "Zobrazí počet firiem")
    public ResponseEntity<String> countCompany() {
        long count = companyService.countCompany();
        return ResponseEntity.ok("Celkový počet firiem: " + count);
    }
    @GetMapping("/count-last-24h")
    public ResponseEntity<String> countUsersLast24Hours() {
        long count = companyService.countCompanyLast24Hours();
        return ResponseEntity.ok("Počet zákazníkov za posledných 24 hodín: " + count);
    }
    @GetMapping("/count-last-7d")
    public ResponseEntity<String> countUsersLast7Days() {
        long count = companyService.countCompanyLast7Days();
        return ResponseEntity.ok("Počet zákazníkov za posledných 7 dní: " + count);
    }
    @GetMapping("/count-last-30d")
    public ResponseEntity<String> countUsersLast30Days() {
        long count = companyService.countCompanyLast30Days();
        return ResponseEntity.ok("Počet zákazníkov za posledných 30 dní: " + count);
    }
    @GetMapping("/count-last-365d")
    public ResponseEntity<String> countUsersLast365Days() {
        long count = companyService.countCompanyLast365Days();
        return ResponseEntity.ok("Počet zákazníkov za posledných 365 dní: " + count);
    }
    @GetMapping("/all")
    @Operation(summary = "Získa všetky firmy", description = "Vráti zoznam všetkých firiem bez hesla.")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        List<CompanyDTO> companies = companyService.getAllCompany();
        if (companies.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content, ak firma neexistuje
        }
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/byemail")
    @Operation(summary = "Zobrazí firmu podľa emailu", description = "Zobrazí firmu podľa emailu.")
    public ResponseEntity<List<CompanyDTO>> getByEmail(@RequestParam String email) {
        List<CompanyDTO> companies = companyService.getByEmail(email);
        if (companies.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content, ak firma neexistuje
        }
        return ResponseEntity.ok(companies); // 200 OK, vráti zoznam firiem
    }

    @GetMapping("/bycompanyname")
    @Operation(summary = "Zobrazí firmy podľa názvu", description = "Zobrazí firmy podľa názvu.")
    public ResponseEntity<List<CompanyDTO>> getByCompanyName(@RequestParam String companyName) {
        List<CompanyDTO> companies = companyService.getByCompanyName(companyName);
        if (companies.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content, ak firma neexistuje
        }
        return ResponseEntity.ok(companies); // 200 OK, vráti zoznam firiem
    }

    @GetMapping("/byico")
    @Operation(summary = "Zobrazí firmu podľa Ičo", description = "Zobrazí firmu podľa Ičo.")
    public ResponseEntity<List<CompanyDTO>> getCompanyByIco(@RequestParam Integer ico) {
        List<CompanyDTO> companies = companyService.getCompanyByIco(ico);
        if (companies.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content, ak firma neexistuje
        }
        return ResponseEntity.ok(companies); // 200 OK, vráti zoznam firiem
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
