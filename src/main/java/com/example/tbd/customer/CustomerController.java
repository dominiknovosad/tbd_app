package com.example.tbd.customer;

import com.example.tbd.JwtTokenUtil;
import com.example.tbd.company.CompanyController;
import com.example.tbd.company.CompanyRepository;
import io.jsonwebtoken.security.Keys; // Import pre generovanie bezpečného kľúča
import io.swagger.v3.oas.annotations.Operation; // Import pre anotácie OpenAPI
import io.swagger.v3.oas.annotations.tags.Tag; // Import pre tagy OpenAPI
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Import pre získanie hodnoty z application.properties
import org.springframework.http.ResponseEntity; // Import pre ResponseEntity, ktorý sa používa na vytváranie odpovedí
import org.springframework.security.authentication.AuthenticationManager; // Import pre autentifikáciu
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*; // Import pre vytváranie REST API
import com.example.tbd.company.Company; // Import pre triedu Company
import java.security.Key; // Import pre bezpečný kľúč na šifrovanie JWT
import java.time.LocalDate; // Import pre dátum
import java.util.List; // Import pre zoznam
import java.util.Date; // Import pre dátum
import java.time.ZoneId; // Import pre časovú zónu
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController // Označuje triedu ako REST kontrolér
@RequestMapping("/customer") // Definuje základnú URL pre všetky endpointy v tejto triede
@Tag(name = "Customer Controller", description = "API pre správu zákazníkov a autentifikáciu") // OpenAPI anotácia pre generovanie dokumentácie
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);
    private final PasswordEncoder passwordEncoder;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;
    private final AuthenticationManager authenticationManager;
    private final Key secretKey;
    private final JwtTokenUtil jwtTokenUtil; // Pridané do konštruktora
    private final CompanyRepository companyRepository; // Pridané do konštruktora

    // Konštruktor s injekciou všetkých závislostí
    @Autowired
    public CustomerController(
            CustomerService service,
            AuthenticationManager authenticationManager,
            CustomerRepository repository,
            PasswordEncoder passwordEncoder,
            @Value("${jwt.secret}") String jwtSecret,
            JwtTokenUtil jwtTokenUtil, // Pridané
            CompanyRepository companyRepository) { // Pridané
        this.customerService = service;
        this.authenticationManager = authenticationManager;
        this.customerRepository = repository;
        this.passwordEncoder = passwordEncoder;
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtTokenUtil = jwtTokenUtil; // Inicializácia
        this.companyRepository = companyRepository; // Inicializácia
    }

    // Pomocná metóda na konverziu LocalDate na java.util.Date
    public static Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()); // Konvertuje LocalDate na java.util.Date
    }

    // Endpoint pre prihlásenie zákazníka

    @PostMapping("/login")
    @Operation(summary = "Prihlásenie zákazníka alebo firmy", description = "Autentifikácia používateľa na základe e-mailu/IČO a hesla.")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        logger.debug("Prijatý LoginRequest - Username: {}, Password: {}", loginRequest.getUsername(), loginRequest.getPassword());

        // Validácia vstupných údajov
        if (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            logger.warn("Chýbajúce prihlasovacie údaje!");
            return ResponseEntity.badRequest().body("Chýbajúce prihlasovacie údaje!");
        }

        try {
            // Rozhodovanie podľa formátu username (e-mail alebo IČO)
            if (loginRequest.getUsername().contains("@")) {
                // Prihlásenie zákazníka podľa e-mailu
                return handleCustomerLogin(loginRequest);
            } else {
                // Prihlásenie firmy podľa IČO
                return handleCompanyLogin(loginRequest);
            }
        } catch (RuntimeException e) {
            logger.error("Chyba pri prihlásení: {}", e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Neznáma chyba pri prihlásení: {}", e.getMessage());
            return ResponseEntity.status(500).body("Interná chyba servera!");
        }
    }

    private ResponseEntity<?> handleCustomerLogin(LoginRequest loginRequest) {
        try {
            // Hľadanie zákazníka podľa e-mailu
            Customer customer = customerRepository.findByEmail(loginRequest.getUsername())
                    .orElseThrow(() -> {
                        logger.warn("Zákazník nenájdený pre e-mail: {}", loginRequest.getUsername());
                        return new RuntimeException("Zákazník nenájdený!");
                    });

            // Overenie hesla
            if (!passwordEncoder.matches(loginRequest.getPassword(), customer.getPassword())) {
                logger.warn("Nesprávne prihlasovacie údaje pre e-mail: {}", loginRequest.getUsername());
                return ResponseEntity.status(401).body("Nesprávne prihlasovacie údaje!");
            }

            // Generovanie tokenu
            String token = jwtTokenUtil.generateToken(customer.getEmail(), customer.getId(), customer.getEmail());
            logger.info("Prihlásenie úspešné. Vygenerovaný token pre zákazníka: {}", customer.getEmail());

            return ResponseEntity.ok(new LoginResponse(token, customer.getId()));
        } catch (RuntimeException e) {
            logger.error("Chyba pri prihlásení zákazníka: {}", e.getMessage());
            throw e; // Výnimka sa spracuje v hlavnej metóde
        } catch (Exception e) {
            logger.error("Neznáma chyba pri prihlásení zákazníka: {}", e.getMessage());
            throw e; // Výnimka sa spracuje v hlavnej metóde
        }
    }

    private ResponseEntity<?> handleCompanyLogin(LoginRequest loginRequest) {
        try {
            // Hľadanie firmy podľa IČO
            Company company = companyRepository.findByIco(Integer.parseInt(loginRequest.getUsername()))
                    .orElseThrow(() -> {
                        logger.warn("Firma nenájdená pre IČO: {}", loginRequest.getUsername());
                        return new RuntimeException("Firma nenájdená!");
                    });

            // Overenie hesla
            if (!passwordEncoder.matches(loginRequest.getPassword(), company.getPassword())) {
                logger.warn("Nesprávne prihlasovacie údaje pre IČO: {}", loginRequest.getUsername());
                return ResponseEntity.status(401).body("Nesprávne prihlasovacie údaje!");
            }

            // Generovanie tokenu
            String token = jwtTokenUtil.generateToken(company.getIco().toString(), company.getId(), company.getIco().toString());
            logger.info("Prihlásenie úspešné. Vygenerovaný token pre firmu: {}", company.getIco());

            return ResponseEntity.ok(new LoginResponse(token, company.getId()));
        } catch (RuntimeException e) {
            logger.error("Chyba pri prihlásení firmy: {}", e.getMessage());
            throw e; // Výnimka sa spracuje v hlavnej metóde
        } catch (Exception e) {
            logger.error("Neznáma chyba pri prihlásení firmy: {}", e.getMessage());
            throw e; // Výnimka sa spracuje v hlavnej metóde
        }
    }


    // Trieda pre odpoveď pri prihlásení obsahujúca token a ID zákazníka
    static class LoginResponse {
        private final String token; // JWT token
        private final Long customerId; // ID zákazníka

        public LoginResponse(String token, Long customerId) {
            this.token = token;
            this.customerId = customerId;
        }

        public String getToken() {
            return token;
        }

        public Long getCustomerId() {
            return customerId;
        }
    }


    // Endpoint na registráciu nového zákazníka
    @PostMapping("/register")
    @Operation(summary = "Register a new customer", description = "Stores a new customer in the system.")
    public ResponseEntity<?> saveCustomer(@RequestBody Customer customer) {
        System.out.println("DEBUG: Attempting to register customer with email: " + customer.getEmail());

        try {
            // Validácia povinných polí
            if (customer.getName() == null || customer.getName().isEmpty() ||
                    customer.getSurname() == null || customer.getSurname().isEmpty() ||
                    customer.getCity() == null || customer.getCity().isEmpty() ||
                    customer.getTelephone() == null || customer.getTelephone().isEmpty() ||
                    customer.getEmail() == null || customer.getEmail().isEmpty() ||
                    customer.getPassword() == null || customer.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("Missing required customer fields!"); // Chyba ak chýbajú povinné údaje
            }

            // Dodatočné validácie
            if (customer.getBirthdate() == null) {
                return ResponseEntity.badRequest().body("Birthdate is required."); // Chyba ak chýba dátum narodenia
            }

            // Uloženie zákazníka do systému
            Customer savedCustomer = customerService.createCustomer(customer);
            System.out.println("DEBUG: Customer successfully registered with ID: " + savedCustomer.getId());

            return ResponseEntity.ok(savedCustomer); // Vráti uloženého zákazníka

        } catch (Exception e) {
            System.out.println("DEBUG: Error while registering customer: " + e.getMessage());
            return ResponseEntity.status(500).body("An error occurred while processing the request."); // Vráti chybu pri spracovaní požiadavky
        }
    }
    @PutMapping("/editprofile/{id}")
    public ResponseEntity<String> editProfile(@PathVariable Long id,
                                              @RequestBody @Validated UpdateProfileRequest editProfileRequest) {
        boolean isUpdated = customerService.updateCustomerProfile(id, editProfileRequest);

        if (isUpdated) {
            return ResponseEntity.ok("Profil bol úspešne aktualizovaný.");
        } else {
            return ResponseEntity.status(404).body("Zákazník s týmto ID neexistuje.");
        }
    }

    // Endpoint na získanie zákazníka podľa ID
    // Endpoint na získanie zákazníka podľa ID
    @GetMapping("/{id}")
    @Operation(summary = "Získa zákazníka podľa ID", description = "Vráti detail zákazníka na základe jeho ID.")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable("id") Long id) {
        System.out.println("DEBUG: Načítavanie zákazníka s ID: " + id);
        CustomerDTO customerDTO = customerService.getCustomerById(id);
        return ResponseEntity.ok(customerDTO); // Vráti zákazníka ako DTO
    }

    // Endpoint na získanie všetkých zákazníkov
    @GetMapping("/all")
    @Operation(summary = "Získa všetkých zákazníkov", description = "Vráti zoznam všetkých zákazníkov.")
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {
        System.out.println("DEBUG: Načítavanie všetkých zákazníkov");
        List<CustomerDTO> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            return ResponseEntity.noContent().build(); // Vráti 204 No Content, ak nie sú žiadni zákazníci
        }
        return ResponseEntity.ok(customers); // Vráti zoznam zákazníkov ako DTO
    }

    @GetMapping("/count")
    @Operation(summary = "Počet užívateľov s role_id = 1", description = "Zobrazí počet zákazníkov, ktorí majú role_id = 1.")
    public ResponseEntity<String> countCustomersWithRoleUser() {
        long count = customerService.countCustomersWithRoleUser();
        return ResponseEntity.ok("Celkový počet uživateľov: " + count);
    }
    @GetMapping("/count-last-24h")
    public ResponseEntity<String> countUsersLast24Hours() {
        long count = customerService.countUsersLast24Hours();
        return ResponseEntity.ok("Počet zákazníkov za posledných 24 hodín: " + count);
    }
    @GetMapping("/count-last-7d")
    public ResponseEntity<String> countUsersLast7Days() {
        long count = customerService.countUsersLast7Days();
        return ResponseEntity.ok("Počet zákazníkov za posledných 7 dní: " + count);
    }
    @GetMapping("/count-last-30d")
    public ResponseEntity<String> countUsersLast30Days() {
        long count = customerService.countUsersLast30Days();
        return ResponseEntity.ok("Počet zákazníkov za posledných 30 dní: " + count);
    }
    @GetMapping("/count-last-365d")
    public ResponseEntity<String> countUsersLast365Days() {
        long count = customerService.countUsersLast365Days();
        return ResponseEntity.ok("Počet zákazníkov za posledných 365 dní: " + count);
    }

}
