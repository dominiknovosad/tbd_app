package com.example.tbd.company;

import com.example.tbd.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company")
@CrossOrigin(origins = "http://localhost:55555/")
@Tag(name = "Company Controller", description = "API pre správu firiem")
public class CompanyController {

    private final CompanyService companyService;
    private final CompanyRepository companyRepository;  // Ensure companyRepository is declared here
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public CompanyController(CompanyService companyService,
                             CompanyRepository companyRepository,  // Make sure companyRepository is injected
                             AuthenticationManager authenticationManager,
                             JwtTokenUtil jwtTokenUtil) {
        this.companyService = companyService;
        this.companyRepository = companyRepository;  // Inject companyRepository properly
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "Prihlásenie firmy", description = "Autentifikácia firmy na základe IČO a hesla.")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("DEBUG: Prijatý LoginRequest - IČO: " + loginRequest.getIco() + ", Password: " + loginRequest.getPassword());

        // Validácia vstupných údajov
        if (loginRequest.getIco() == null || loginRequest.getIco().isEmpty() || loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            System.out.println("DEBUG: Chýbajúce prihlasovacie údaje!");
            return ResponseEntity.badRequest().body("Chýbajúce prihlasovacie údaje!");
        }

        try {
            // Autentifikácia cez IČO a heslo
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getIco(), loginRequest.getPassword()
                    )
            );

            // Vyhľadanie firmy na základe IČO
            Company company = companyRepository.findByIco(Integer.parseInt(loginRequest.getIco()))
                    .orElseThrow(() -> new RuntimeException("Firma nenájdená!"));

            // Generovanie JWT tokenu
            String token = jwtTokenUtil.generateToken(company.getIco().toString(), company.getId(), company.getIco().toString());

            System.out.println("DEBUG: Vygenerovaný token pre firmu: " + company.getIco());

            // Vrátenie tokenu ako odpoveď
            return ResponseEntity.ok(new LoginResponse(token, company.getId()));

        } catch (Exception e) {
            System.out.println("DEBUG: Chyba autentifikácie: " + e.getMessage());
            return ResponseEntity.status(401).body("Neplatné prihlasovacie údaje!");
        }
    }

    // DTO pre odpoveď s JWT tokenom a ID firmy
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

    // Endpointy na získanie firiem

    @GetMapping("/{id}")
    @Operation(summary = "Získa firmu podľa ID", description = "Vráti detail firmy na základe jej ID.")
    public ResponseEntity<Company> getCompany(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));  // Ensure companyService is used here
    }

    @GetMapping("/all")
    @Operation(summary = "Získa všetky firmy", description = "Vráti zoznam všetkých firiem.")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompany());  // Ensure companyService is used here
    }

    @GetMapping("/allbyname/{name}")
    @Operation(summary = "Získa firmy podľa mena", description = "Nájde všetky firmy so zadaným názvom.")
    public ResponseEntity<List<Company>> getAllByCompanyName(@PathVariable("name") String companyName) {
        return ResponseEntity.ok(companyService.findAllByCompanyName(companyName));  // Ensure companyService is used here
    }

    @GetMapping("/byico/{ico}")
    @Operation(summary = "Získa firmy podľa IČO", description = "Nájde všetky firmy podľa IČO.")
    public ResponseEntity<List<Company>> getAllByIco(@PathVariable("ico") Integer ico) {
        return ResponseEntity.ok(companyService.findAllByIco(ico));  // Ensure companyService is used here
    }

    @PostMapping("/register")
    @Operation(summary = "Registruje novú firmu", description = "Uloží novú firmu do systému.")
    public ResponseEntity<Company> saveCompany(@RequestBody Company company) {
        System.out.println("DEBUG: Pokus o registráciu firmy - Meno: " + company.getCompanyName() + ", IČO: " + company.getIco());
        Company savedCompany = companyService.createCompany(company);  // Ensure companyService is used here
        System.out.println("DEBUG: Firma úspešne registrovaná s ID: " + savedCompany.getId());
        return ResponseEntity.ok(savedCompany);  // Return the saved company
    }
}
