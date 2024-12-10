package com.example.tbd.customer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller()
@RestController
@OpenAPIDefinition
@RequestMapping("/customer")
@CrossOrigin(origins = "http://localhost:55134")
public class CustomerController {

    CustomerService service;

    @Autowired
    public CustomerController(CustomerService service) {
        this.service = service;
    }

    //vypiše person podľa id
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(service.getCustomerById(id));
    }
    @ModelAttribute("Customer")
    public Object findCustomer(@PathVariable(name = "id", required = false) Integer id) {
        return id == null ? new Customer() : this.getCustomer(id);
    }
    //vypiše list všetkých persons
    @GetMapping("/all")
    public ResponseEntity<List<Customer>> getAlllist() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/allbyname/{name}")
    public ResponseEntity<List<Customer>> getAllByName(@PathVariable("name") String name) {
        return ResponseEntity.ok(service.findByAllName(name));
    }

    @GetMapping("/allbylastname/{lastname}")
    public ResponseEntity<List<Customer>> getAllBySurname(@PathVariable("surname") String surname) {
        return ResponseEntity.ok(service.findByAllSurname(surname));
    }
    @PostMapping("/register")
    public  ResponseEntity<Customer> saveCustomer(@RequestBody Customer customer) {
        System.out.println(customer.getName());
        System.out.println(customer.getBirthdate());
        return  ResponseEntity.ok(service.createCustomer(customer));
    }
}
