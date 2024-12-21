package com.example.tbd.customer;

import com.example.tbd.customer.LoginRequest; // Import novej triedy LoginRequest
import io.jsonwebtoken.Jwts; // Import pre JWT token
import io.jsonwebtoken.security.Keys; // Import pre generovanie bezpečného kľúča
import io.swagger.v3.oas.annotations.Operation; // Import pre anotácie OpenAPI
import io.swagger.v3.oas.annotations.tags.Tag; // Import pre tagy OpenAPI
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Import pre získanie hodnoty z application.properties
import org.springframework.http.ResponseEntity; // Import pre ResponseEntity, ktorý sa používa na vytváranie odpovedí
import org.springframework.security.authentication.AuthenticationManager; // Import pre autentifikáciu
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Import pre autentifikáciu s používateľským menom a heslom
import org.springframework.security.core.Authentication; // Import pre autentifikáciu
import org.springframework.web.bind.annotation.*; // Import pre vytváranie REST API

import java.security.Key; // Import pre bezpečný kľúč na šifrovanie JWT
import java.text.SimpleDateFormat; // Import pre formátovanie dátumu
import java.time.Instant; // Import pre získanie aktuálneho času
import java.time.LocalDate; // Import pre dátum
import java.time.format.DateTimeFormatter; // Import pre formátovanie dátumu
import java.util.List; // Import pre zoznam
import java.util.Date; // Import pre dátum
import java.time.ZoneId; // Import pre časovú zónu

@RestController // Označuje triedu ako REST kontrolér
@RequestMapping("/customer") // Definuje základnú URL pre všetky endpointy v tejto triede
@CrossOrigin(origins = "http://localhost:55555/") // Umožňuje prístup z front-end aplikácie na tomto portu
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
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes()); // Inicializuje kľúč na šifrovanie JWT
    }

    // Pomocná metóda na konverziu LocalDate na java.util.Date
    public static Date convertToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()); // Konvertuje LocalDate na java.util.Date
    }

    // Endpoint pre prihlásenie zákazníka
    @PostMapping("/login")
    @Operation(summary = "Prihlásenie zákazníka", description = "Autentifikácia zákazníka na základe e-mailu a hesla.")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        System.out.println("DEBUG: Prijatý LoginRequest - Username: " + loginRequest.getUsername() + ", Password: " + loginRequest.getPassword());

        // Validácia prichádzajúcich údajov
        if (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            System.out.println("DEBUG: Chýbajúce prihlasovacie údaje! Ukončujem spracovanie.");
            return ResponseEntity.badRequest().body("Chýbajúce prihlasovacie údaje!"); // Vráti chybu, ak sú údaje neúplné
        }

        try {
            // Vyhľadanie zákazníka podľa emailu
            Customer customer = repository.findByEmail(loginRequest.getUsername())
                    .orElseThrow(() -> {
                        System.out.println("DEBUG: Zákazník nenájdený pre e-mail: " + loginRequest.getUsername());
                        return new RuntimeException("Zákazník nenájdený!"); // Vráti chybu, ak zákazník neexistuje
                    });

            // Autentifikácia pomocou e-mailu a hesla
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            if (!authentication.isAuthenticated()) {
                System.out.println("DEBUG: Nesprávne prihlasovacie údaje pre e-mail: " + loginRequest.getUsername());
                return ResponseEntity.status(401).body("Nesprávne prihlasovacie údaje!"); // Vráti chybu pri nesprávnych údajoch
            }

            // Vytvorenie JWT tokenu s nastavením času vydania a vypršania platnosti
            Date issuedAt = Date.from(Instant.now()); // Nastaví čas vydania tokenu
            Date expiration = Date.from(Instant.now().plusMillis(86400000)); // Nastaví čas vypršania platnosti tokenu na 24 hodín

            String token = Jwts.builder()
                    .setSubject(customer.getEmail()) // Nastaví email zákazníka ako subject
                    .claim("customerId", customer.getId()) // Pridá customerId do claimu
                    .setIssuedAt(issuedAt) // Nastaví čas vydania
                    .setExpiration(expiration) // Nastaví vypršanie platnosti
                    .signWith(secretKey) // Podepíše token s tajným kľúčom
                    .compact(); // Skomprimuje a vygeneruje finálny token

            System.out.println("DEBUG: Vygenerovaný token pre zákazníka: " + customer.getEmail());
            return ResponseEntity.ok(new LoginResponse(token, customer.getId())); // Vráti token a ID zákazníka

        } catch (RuntimeException e) {
            System.out.println("DEBUG: Chyba pri prihlásení: " + e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage()); // Vráti chybu pri nesprávnom prihlásení
        } catch (Exception e) {
            System.out.println("DEBUG: Neznáma chyba pri prihlásení: " + e.getMessage());
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
