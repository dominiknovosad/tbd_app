package com.example.tbd.product;

import com.example.tbd.company.CompanyRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product") // Definuje URL cestu pre všetky endpointy tejto triedy
@Tag(name = "Služby controller", description = "API pre správu služieb")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private ProductRepository productRepository;
    private CompanyRepository companyRepository;
    private ProductService productService;
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Endpoint na získanie všetkých služieb
    @GetMapping("/showall")
    public ResponseEntity<List<Product>> getAllServices() {
        List<Product> products = productRepository.findAll();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build(); // Vráti 204 No Content, ak nie sú žiadne služby
        }
        return ResponseEntity.ok(products); // Vráti zoznam všetkých služieb (200 OK)
    }

    // Endpoint na získanie služieb podľa ID firmy
    @GetMapping("/companyid/{companyId}")
    public ResponseEntity<List<Product>> getServicesByCompanyId(@PathVariable Integer companyId) {
        List<Product> products = productRepository.findByCompanyId(companyId);

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build(); // Vráti 204 No Content, ak žiadne služby neexistujú pre danú firmu
        }
        return ResponseEntity.ok(products); // Vráti zoznam služieb (200 OK)
    }
    @GetMapping("/count")
    @Operation(summary = "Počet produktov", description = "Zobrazí počet produktov celkovo")
    public ResponseEntity<String> countProducts() {
        long count = productService.countProducts();
        return ResponseEntity.ok("Celkový počet produktov: " + count);
    }

    // Endpoint na označenie služby ako vymazanej
    @PutMapping("/delupdate/{id}")
    public ResponseEntity<?> markServiceAsDeleted(@PathVariable Integer id) {
        logger.debug("Prijatá požiadavka na označenie služby s ID {} ako vymazanej.", id);

        // Validácia ID služby
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("Neplatné ID služby!");
        }

        // Kontrola, či služba s daným ID existuje
        Optional<Product> existingServiceOptional = productRepository.findById(id);
        if (existingServiceOptional.isEmpty()) {
            logger.warn("Služba s ID {} neexistuje.", id);
            return ResponseEntity.badRequest().body("Služba s poskytnutým ID neexistuje.");
        }

        try {
            Product existingService = existingServiceOptional.get();

            // Aktualizácia hodnoty stĺpca "deleted" na "Y"
            existingService.setDeleted("Y");

            // Uloženie zmeny do databázy
            productRepository.save(existingService);

            logger.info("Služba s ID {} bola označená ako vymazaná.", id);
            return ResponseEntity.ok("Služba bola úspešne označená ako vymazaná.");
        } catch (Exception e) {
            logger.error("Chyba pri označovaní služby s ID {} ako vymazanej: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Chyba pri spracovaní požiadavky!");
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        logger.debug("Prijatý VehicleRequest - {}", product);  // Logovanie prijatého požiadavky na pridanie služby

        // Validácia vstupných údajov
        if (product.getCompanyId() == null || product.getCompanyId() <= 0 ||
                product.getName() == null || product.getName().isEmpty() ||
                product.getDescription() == null || product.getDescription().isEmpty() ||
                product.getPrice() == null || product.getPrice().isEmpty()) {
            return ResponseEntity.badRequest().body("Neplatné údaje pre pridanie služby!");  // Vráti chybu ak sú údaje neúplné
        }

        // Kontrola, či firma s daným ID existuje
        boolean companyExists = companyRepository.existsById(product.getCompanyId());
        if (!companyExists) {
            logger.warn("Firma s ID {} neexistuje.", product.getCompanyId());  // Logovanie, ak zákazník neexistuje
            return ResponseEntity.badRequest().body("Firma s poskytnutým ID neexistuje.");
        }

            // Vytvorenie a uloženie nového vozidla
            Product newproduct = new Product();
            newproduct.setCompanyId(product.getCompanyId());
            newproduct.setName(product.getName());
            newproduct.setDescription(product.getDescription());
            newproduct.setPrice(product.getPrice());
            newproduct.setCreatedAt(LocalDateTime.now());  // Uloženie aktuálneho dátumu a času ako LocalDateTime
        try {
            // Uloženie vozidla do databázy
            productRepository.save(newproduct);

            // Logovanie úspešného pridania vozidla
            logger.info("Služba úspešne pridané pre firmuId {}: Nazov: {}, Cena: {}",
                    newproduct.getCompanyId(),
                    newproduct.getName(),
                    newproduct.getPrice());

            return ResponseEntity.ok("Služba úspešne pridaná.");  // Vráti úspešnú odpoveď
        } catch (Exception e) {
            // Logovanie chyby pri ukladaní vozidla
            logger.error("Chyba pri pridávaní vozidla: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Chyba pri spracovaní požiadavky!");  // Vráti internú chybu servera
        }
    }

}
