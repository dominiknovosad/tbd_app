package com.example.tbd.customer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer")
@CrossOrigin(origins = "*")
@Tag(name = "Customer Controller", description = "API pre správu zákazníkov")
public class CustomerController {

    private final CustomerService service;

    @Autowired
    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @Operation(summary = "Získa zákazníka podľa ID", description = "Vráti detail zákazníka na základe jeho ID.")
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(service.getCustomerById(id));
    }

    @Operation(summary = "Získa všetkých zákazníkov", description = "Vráti zoznam všetkých zákazníkov.")
    @GetMapping("/all")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Nájde zákazníkov podľa mena", description = "Vráti zákazníkov so zadaným menom.")
    @GetMapping("/allbyname/{name}")
    public ResponseEntity<List<Customer>> getAllByName(@PathVariable("name") String name) {
        return ResponseEntity.ok(service.findByAllName(name));
    }

    @Operation(summary = "Nájde zákazníkov podľa priezviska", description = "Vráti zákazníkov so zadaným priezviskom.")
    @GetMapping("/allbylastname/{surname}")
    public ResponseEntity<List<Customer>> getAllBySurname(@PathVariable("surname") String surname) {
        return ResponseEntity.ok(service.findByAllSurname(surname));
    }

    @Operation(summary = "Registruje nového zákazníka", description = "Uloží nového zákazníka do systému.")
    @PostMapping("/register")
    public ResponseEntity<Customer> saveCustomer(@RequestBody Customer customer) {
        System.out.println(customer.getName());
        System.out.println(customer.getBirthdate());
        return ResponseEntity.ok(service.createCustomer(customer));
    }
}
