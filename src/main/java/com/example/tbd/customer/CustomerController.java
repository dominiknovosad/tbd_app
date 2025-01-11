package com.example.tbd.customer;

import com.example.tbd.JwtTokenUtil;
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

    private final CustomerService service; // Služba na spracovanie logiky pre zákazníkov
    private final CustomerRepository repository; // Repository pre komunikáciu s databázou
    private final AuthenticationManager authenticationManager; // Manažér autentifikácie na autentifikovanie používateľov

    private final Key secretKey; // Kľúč pre šifrovanie JWT tokenu

    // Konštruktor triedy, injektuje závislosti
    @Autowired
    public CustomerController(
            CustomerService service,
            AuthenticationManager authenticationManager,
            CustomerRepository repository,
            @Value("${jwt.secret}") String jwtSecret) {
        this.service = service;
        this.authenticationManager = authenticationManager;
        this.repository = repository;
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes()); // Inicializuje kľúč pre generovanie tokenu
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;  // Injektovanie JwtTokenUtil

    @Autowired
    private CompanyRepository companyRepository;  // Injektovanie repository pre firmy
    @Autowired
    private CustomerService customerService;

    // Pomocná metóda na konverziu LocalDate na java.util.Date
    public static Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()); // Konvertuje LocalDate na java.util.Date
    }

    // Endpoint pre prihlásenie zákazníka

    @PostMapping("/login")
    @Operation(summary = "Prihlásenie zákazníka alebo firmy", description = "Autentifikácia používateľa na základe e-mailu/ičo a hesla.")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Logger logger = LoggerFactory.getLogger(CustomerController.class); // Definícia logera

        logger.debug("Prijatý LoginRequest - Username: {}, Password: {}", loginRequest.getUsername(), loginRequest.getPassword());

        // Validácia prichádzajúcich údajov
        if (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            logger.warn("Chýbajúce prihlasovacie údaje! Ukončujem spracovanie.");
            return ResponseEntity.badRequest().body("Chýbajúce prihlasovacie údaje!"); // Vráti chybu, ak sú údaje neúplné
        }

        try {
            Customer customer = null;
            Company company = null;

            // Skontroluj, či je email alebo IČO platný a podľa toho vykonaj autentifikáciu
            if (loginRequest.getUsername().contains("@")) {
                // Prihlásenie zákazníka podľa emailu
                customer = repository.findByEmail(loginRequest.getUsername())
                        .orElseThrow(() -> {
                            logger.warn("Zákazník nenájdený pre e-mail: {}", loginRequest.getUsername());
                            return new RuntimeException("Zákazník nenájdený!"); // Vráti chybu, ak zákazník neexistuje
                        });

                // Autentifikácia pomocou e-mailu a hesla
                if (!passwordEncoder.matches(loginRequest.getPassword(), customer.getPassword())) {
                    logger.warn("Nesprávne prihlasovacie údaje pre e-mail: {}", loginRequest.getUsername());
                    return ResponseEntity.status(401).body("Nesprávne prihlasovacie údaje!"); // Nesprávne heslo
                }

                // Vytvorenie JWT tokenu pre zákazníka
                String token = jwtTokenUtil.generateToken(customer.getEmail(), customer.getId(), customer.getEmail());
                logger.info("Prihlásenie úspešné. Vygenerovaný token pre zákazníka: {}", customer.getEmail());
                return ResponseEntity.ok(new LoginResponse(token, customer.getId()));

            } else {
                // Prihlásenie firmy podľa IČO
                company = companyRepository.findByIco(Integer.parseInt(loginRequest.getUsername()))
                        .orElseThrow(() -> {
                            logger.warn("Firma nenájdená pre IČO: {}", loginRequest.getUsername());
                            return new RuntimeException("Firma nenájdená!"); // Vráti chybu, ak firma neexistuje
                        });

                // Autentifikácia pomocou IČO a hesla
                if (!passwordEncoder.matches(loginRequest.getPassword(), company.getPassword())) {
                    logger.warn("Nesprávne prihlasovacie údaje pre IČO: {}", loginRequest.getUsername());
                    return ResponseEntity.status(401).body("Nesprávne prihlasovacie údaje!"); // Nesprávne heslo
                }

                // Vytvorenie JWT tokenu pre firmu
                String token = jwtTokenUtil.generateToken(company.getIco().toString(), company.getId(), company.getIco().toString());
                logger.info("Prihlásenie úspešné. Vygenerovaný token pre firmu: {}", company.getIco());
                return ResponseEntity.ok(new LoginResponse(token, company.getId()));
            }

        } catch (RuntimeException e) {
            logger.error("Chyba pri prihlásení: {}", e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage()); // Vráti chybu pri nesprávnom prihlásení
        } catch (Exception e) {
            logger.error("Neznáma chyba pri prihlásení: {}", e.getMessage());
            return ResponseEntity.status(500).body("Interná chyba servera!"); // Vráti internú chybu servera
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
            Customer savedCustomer = service.createCustomer(customer);
            System.out.println("DEBUG: Customer successfully registered with ID: " + savedCustomer.getId());

            return ResponseEntity.ok(savedCustomer); // Vráti uloženého zákazníka

        } catch (Exception e) {
            System.out.println("DEBUG: Error while registering customer: " + e.getMessage());
            return ResponseEntity.status(500).body("An error occurred while processing the request."); // Vráti chybu pri spracovaní požiadavky
        }
    }

    // Endpoint na získanie zákazníka podľa ID
    @GetMapping("/{id}")
    @Operation(summary = "Získa zákazníka podľa ID", description = "Vráti detail zákazníka na základe jeho ID.")
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") Integer id) {
        System.out.println("DEBUG: Načítavanie zákazníka s ID: " + id);
        return ResponseEntity.ok(service.getCustomerById(id)); // Vráti zákazníka podľa ID
    }

    // Endpoint na získanie všetkých zákazníkov
    @GetMapping("/all")
    @Operation(summary = "Získa všetkých zákazníkov", description = "Vráti zoznam všetkých zákazníkov.")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        System.out.println("DEBUG: Načítavanie všetkých zákazníkov");
        return ResponseEntity.ok(service.getAll()); // Vráti zoznam všetkých zákazníkov
    }

    @PutMapping("/editprofile/{id}")
    public ResponseEntity<String> editProfile(@PathVariable Long id,
                                              @RequestBody @Validated UpdateProfileRequest editProfileRequest) {
        boolean isUpdated = customerService.updateCustomerProfile(Math.toIntExact(id), editProfileRequest);

        if (isUpdated) {
            return ResponseEntity.ok("Profil bol úspešne aktualizovaný.");
        } else {
            return ResponseEntity.status(404).body("Zákazník s týmto ID neexistuje.");
        }
    }
    // Trieda pre odpoveď pri prihlásení obsahujúca token a ID zákazníka
    static class LoginResponse {
        private final String token; // JWT token
        private final Integer customerId; // ID zákazníka

        public LoginResponse(String token, Integer customerId) {
            this.token = token;
            this.customerId = customerId;
        }

        public String getToken() {
            return token;
        }

        public Integer getCustomerId() {
            return customerId;
        }
    }
}
