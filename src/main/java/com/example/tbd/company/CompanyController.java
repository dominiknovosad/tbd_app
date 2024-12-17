package com.example.tbd.company;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company")
@CrossOrigin(origins = "*")
@Tag(name = "Company Controller", description = "API pre správu firiem") // Tag pre Swagger
public class CompanyController {

    private final CompanyService service;

    @Autowired
    public CompanyController(CompanyService service) {
        this.service = service;
    }

    @Operation(summary = "Získa firmu podľa ID", description = "Vráti detail firmy na základe jej ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Company> getCompany(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(service.getCompanyById(id));
    }

    @Operation(summary = "Získa všetky firmy", description = "Vráti zoznam všetkých firiem.")
    @GetMapping("/all")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(service.getAllCompany());
    }

    @Operation(summary = "Získa firmy podľa mena", description = "Nájde všetky firmy so zadaným názvom.")
    @GetMapping("/allbyname/{name}")
    public ResponseEntity<List<Company>> getAllByCompanyName(@PathVariable("name") String companyName) {
        return ResponseEntity.ok(service.findAllByCompanyName(companyName));
    }

    @Operation(summary = "Získa firmy podľa IČO", description = "Nájde všetky firmy podľa IČO.")
    @GetMapping("/byico/{ico}")
    public ResponseEntity<List<Company>> getAllByIco(@PathVariable("ico") Integer ico) {
        return ResponseEntity.ok(service.findAllByIco(ico));
    }

    @Operation(summary = "Registruje novú firmu", description = "Uloží novú firmu do systému.")
    @PostMapping("/register")
    public ResponseEntity<Company> saveCompany(@RequestBody Company company) {
        System.out.println("Meno spoločnosti: " + company.getCompanyName());
        System.out.println("IČO: " + company.getIco());
        return ResponseEntity.ok(service.createCompany(company));
    }
}
