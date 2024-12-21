package com.example.tbd.company;

import com.example.tbd.JwtTokenUtil; // Importuje triedu na generovanie a validáciu JWT tokenov
import io.swagger.v3.oas.annotations.Operation; // Importuje anotáciu pre dokumentáciu OpenAPI
import io.swagger.v3.oas.annotations.tags.Tag; // Importuje anotáciu pre tagovanie v OpenAPI
import org.springframework.beans.factory.annotation.Autowired; // Importuje anotáciu na automatické injektovanie závislostí
import org.springframework.http.ResponseEntity; // Importuje ResponseEntity pre prácu s HTTP odpoveďami
import org.springframework.security.authentication.AuthenticationManager; // Importuje rozhranie na správu autentifikácie
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Importuje token na autentifikáciu používateľov
import org.springframework.security.core.Authentication; // Importuje rozhranie pre autentifikovanú identitu používateľa
import org.springframework.web.bind.annotation.*; // Importuje anotácie pre HTTP požiadavky (GET, POST atď.)

import java.util.List; // Importuje zoznam pre práci s kolekciami

@RestController // Anotácia, ktorá označuje triedu ako REST kontrolér, vracia odpovede vo formáte JSON
@RequestMapping("/company") // Anotácia pre mapovanie všetkých požiadaviek začínajúcich na "/company"
@CrossOrigin(origins = "http://localhost:55555/") // Povolenie CORS pre požiadavky z domény "http://localhost:55555/"
@Tag(name = "Company Controller", description = "API pre správu firiem") // Tag pre dokumentáciu OpenAPI
public class CompanyController {

    private final CompanyService companyService; // Služba na spracovanie logiky firiem
    private final CompanyRepository companyRepository; // Repozitár pre prístup k databáze firiem
    private final AuthenticationManager authenticationManager; // Manažér autentifikácie na overenie používateľov
    private final JwtTokenUtil jwtTokenUtil; // Trieda na generovanie a validáciu JWT tokenov

    // Konštruktor s injektovanými závislosťami
    @Autowired
    public CompanyController(CompanyService companyService,
                             CompanyRepository companyRepository,
                             AuthenticationManager authenticationManager,
                             JwtTokenUtil jwtTokenUtil) {
        this.companyService = companyService; // Injektovanie služby na správu firiem
        this.companyRepository = companyRepository; // Injektovanie repozitára pre prístup k databáze firiem
        this.authenticationManager = authenticationManager; // Injektovanie manažéra autentifikácie
        this.jwtTokenUtil = jwtTokenUtil; // Injektovanie nástroja na prácu s JWT tokenmi
    }

    // Endpoint na prihlásenie firmy (POST)
    @PostMapping("/login")
    @Operation(summary = "Prihlásenie firmy", description = "Autentifikácia firmy na základe IČO a hesla.") // Dokumentácia pre OpenAPI
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("DEBUG: Prijatý LoginRequest - IČO: " + loginRequest.getIco() + ", Password: " + loginRequest.getPassword());

        // Validácia prichádzajúcich údajov
        if (loginRequest.getIco() == null || loginRequest.getIco().isEmpty() || loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            System.out.println("DEBUG: Chýbajúce prihlasovacie údaje!");
            return ResponseEntity.badRequest().body("Chýbajúce prihlasovacie údaje!"); // Zlyhanie pri validácii
        }

        try {
            // Vyhľadanie firmy na základe IČO
            Company company = companyRepository.findByIco(Integer.parseInt(loginRequest.getIco()))
                    .orElseThrow(() -> {
                        System.out.println("DEBUG: Firma nenájdená pre IČO: " + loginRequest.getIco());
                        return new RuntimeException("Firma nenájdená!"); // Zlyhanie pri vyhľadávaní firmy
                    });

            // Overenie hesla a autentifikácia
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.getIco(),
                                loginRequest.getPassword()
                        )
                );

                if (!authentication.isAuthenticated()) {
                    System.out.println("DEBUG: Nesprávne prihlasovacie údaje pre IČO: " + loginRequest.getIco());
                    return ResponseEntity.status(401).body("Nesprávne prihlasovacie údaje!"); // Zlyhanie pri autentifikácii
                }
            } catch (Exception ex) {
                System.out.println("DEBUG: Autentifikácia zlyhala: " + ex.getMessage());
                return ResponseEntity.status(401).body("Nesprávne prihlasovacie údaje!"); // Zlyhanie pri autentifikácii
            }

            // Generovanie JWT tokenu
            String token = jwtTokenUtil.generateToken(company.getIco().toString(), company.getId(), company.getIco().toString());
            System.out.println("DEBUG: Vygenerovaný token pre firmu: " + company.getIco());

            // Vrátenie tokenu ako odpoveď
            return ResponseEntity.ok(new LoginResponse(token, company.getId()));

        } catch (RuntimeException e) {
            System.out.println("DEBUG: Chyba pri prihlásení: " + e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage()); // Chyba pri prihlásení firmy
        } catch (Exception e) {
            System.out.println("DEBUG: Neznáma chyba pri prihlásení: " + e.getMessage());
            return ResponseEntity.status(500).body("Interná chyba servera!"); // Neznáma chyba
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

    // Endpoint na získanie firmy podľa ID (GET)
    @GetMapping("/{id}")
    @Operation(summary = "Získa firmu podľa ID", description = "Vráti detail firmy na základe jej ID.")
    public ResponseEntity<Company> getCompany(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(companyService.getCompanyById(id)); // Používa companyService na získanie firmy podľa ID
    }

    // Endpoint na získanie všetkých firiem (GET)
    @GetMapping("/all")
    @Operation(summary = "Získa všetky firmy", description = "Vráti zoznam všetkých firiem.")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompany()); // Používa companyService na získanie zoznamu všetkých firiem
    }

    // Endpoint na získanie firiem podľa mena (GET)
    @GetMapping("/allbyname/{name}")
    @Operation(summary = "Získa firmy podľa mena", description = "Nájde všetky firmy so zadaným názvom.")
    public ResponseEntity<List<Company>> getAllByCompanyName(@PathVariable("name") String companyName) {
        return ResponseEntity.ok(companyService.findAllByCompanyName(companyName)); // Používa companyService na vyhľadanie firiem podľa názvu
    }

    // Endpoint na získanie firiem podľa IČO (GET)
    @GetMapping("/byico/{ico}")
    @Operation(summary = "Získa firmy podľa IČO", description = "Nájde všetky firmy podľa IČO.")
    public ResponseEntity<List<Company>> getAllByIco(@PathVariable("ico") Integer ico) {
        return ResponseEntity.ok(companyService.findAllByIco(ico)); // Používa companyService na vyhľadanie firiem podľa IČO
    }

    // Endpoint na registráciu novej firmy (POST)
    @PostMapping("/register")
    @Operation(summary = "Registruje novú firmu", description = "Uloží novú firmu do systému.")
    public ResponseEntity<Company> saveCompany(@RequestBody Company company) {
        System.out.println("DEBUG: Pokus o registráciu firmy - Meno: " + company.getCompanyName() + ", IČO: " + company.getIco());
        Company savedCompany = companyService.createCompany(company); // Ukladá novú firmu cez companyService
        System.out.println("DEBUG: Firma úspešne registrovaná s ID: " + savedCompany.getId());
        return ResponseEntity.ok(savedCompany); // Vráti uloženú firmu ako odpoveď
    }
}
