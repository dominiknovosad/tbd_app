package com.example.tbd.customer;

import com.example.tbd.customer.LoginRequest; // Import novej triedy
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys; // Import pre Keys
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Key; // Import pre bezpečný kľúč
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/customer")
@CrossOrigin(origins = "http://localhost:55555/")
@Tag(name = "Customer Controller", description = "API pre správu zákazníkov a autentifikáciu")
public class CustomerController {

    private final CustomerService service;
    private final CustomerRepository repository;
    private final AuthenticationManager authenticationManager;

    // Načítanie tajného kľúča z konfigurácie
    private final Key secretKey;

    @Autowired
    public CustomerController(
            CustomerService service,
            AuthenticationManager authenticationManager,
            CustomerRepository repository,
            @Value("${jwt.secret}") String jwtSecret) {
        this.service = service;
        this.authenticationManager = authenticationManager;
        this.repository = repository;
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // Debugging: Výpis prijatého loginRequest
        System.out.println("DEBUG: Prijatý LoginRequest - Username: " + loginRequest.getUsername() + ", Password: " + loginRequest.getPassword());

        // Kontrola chýbajúcich údajov
        if (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            System.out.println("DEBUG: Chýbajúce prihlasovacie údaje!");
            return ResponseEntity.badRequest().body("Chýbajúce prihlasovacie údaje!");
        }

        // Pred autentifikáciou vrátime správu o prijatí requestu
        System.out.println("DEBUG: Request prijatý, spracovávam autentifikáciu...");

        try {
            // Autentifikácia používateľa
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // Vyhľadanie zákazníka podľa e-mailu
            Customer customer = repository.findByEmail(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Zákazník nenájdený!"));

            // Generovanie JWT tokenu
            String token = Jwts.builder()
                    .setSubject(customer.getEmail())
                    .claim("customerId", customer.getId())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hodín
                    .signWith(Keys.hmacShaKeyFor("verysecuresecretkeywith256bits1234567890".getBytes())) // Bezpečný kľúč
                    .compact();

            System.out.println("DEBUG: Vygenerovaný token pre používateľa: " + customer.getEmail());
            return ResponseEntity.ok(new LoginResponse(token, customer.getId()));

        } catch (Exception e) {
            System.out.println("DEBUG: Chyba autentifikácie: " + e.getMessage());
            return ResponseEntity.status(401).body("Neplatné prihlasovacie údaje!");
        }
    }


    @Operation(summary = "Získa zákazníka podľa ID", description = "Vráti detail zákazníka na základe jeho ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") Integer id) {
        System.out.println("DEBUG: Načítavanie zákazníka s ID: " + id);
        return ResponseEntity.ok(service.getCustomerById(id));
    }

    @Operation(summary = "Získa všetkých zákazníkov", description = "Vráti zoznam všetkých zákazníkov.")
    @GetMapping("/all")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        System.out.println("DEBUG: Načítavanie všetkých zákazníkov");
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Registruje nového zákazníka", description = "Uloží nového zákazníka do systému.")
    @PostMapping("/register")
    public ResponseEntity<Customer> saveCustomer(@RequestBody Customer customer) {
        System.out.println("DEBUG: Pokus o registráciu zákazníka s emailom: " + customer.getEmail());
        Customer savedCustomer = service.createCustomer(customer);
        System.out.println("DEBUG: Zákazník úspešne registrovaný s ID: " + savedCustomer.getId());
        return ResponseEntity.ok(savedCustomer);
    }


// DTO pre odpoveď s JWT tokenom a ID zákazníka
class LoginResponse {
    private String token;
    private Integer customerId;

    public LoginResponse(String token, Integer customerId) {
        this.token = token;
        this.customerId = customerId;
    }

    public String getToken() { return token; }
    public Integer getCustomerId() { return customerId; }
}
}
