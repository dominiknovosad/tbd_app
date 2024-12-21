package com.example.tbd.company;

import com.example.tbd.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/company")
@CrossOrigin(origins = "http://localhost:55555/")
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

    @GetMapping("/{id}")
    @Operation(summary = "Získa firmu podľa ID", description = "Vráti detail firmy na základe jej ID.")
    public ResponseEntity<Company> getCompany(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
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
}
