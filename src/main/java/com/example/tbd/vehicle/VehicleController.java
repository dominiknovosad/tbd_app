package com.example.tbd.vehicle;

import com.example.tbd.customer.CustomerRepository;  // Import pre CustomerRepository, ktoré sa používa na kontrolu existencie zákazníka
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/vehicle")  // Definuje URL cestu pre všetky endpointy tejto triedy
@Tag(name = "Vehicle Controller", description = "API pre správu vozidel")
public class VehicleController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);  // SLF4J logger na logovanie informácií

    @Autowired
    private VehicleRepository vehicleRepository;  // Injektuje VehicleRepository pre prístup k údajom o vozidlách

    @Autowired
    private CustomerRepository customerRepository;  // Injektuje CustomerRepository pre kontrolu existencie zákazníka

    // Endpoint pre pridanie nového vozidla
    @PostMapping("/add")
    public ResponseEntity<?> addVehicle(@RequestBody Vehicle vehicle) {
        logger.debug("Prijatý VehicleRequest - {}", vehicle);  // Logovanie prijatého požiadavky na pridanie vozidla

        // Validácia vstupných údajov
        if (vehicle.getCustomerId() == null || vehicle.getCustomerId() <= 0 ||
                vehicle.getBrand() == null || vehicle.getBrand().isEmpty() ||
                vehicle.getModel() == null || vehicle.getModel().isEmpty() ||
                vehicle.getRegisteredAt() == null || vehicle.getRegisteredAt().isEmpty() ||
                vehicle.getVin() == null || vehicle.getVin().isEmpty() ||
                vehicle.getPlateNo() == null || vehicle.getPlateNo().isEmpty()) {
            return ResponseEntity.badRequest().body("Neplatné údaje pre vozidlo!");  // Vráti chybu ak sú údaje neúplné
        }

        // Kontrola, či zákazník s daným ID existuje
        boolean customerExists = customerRepository.existsById(vehicle.getCustomerId());
        if (!customerExists) {
            logger.warn("Zákazník s ID {} neexistuje.", vehicle.getCustomerId());  // Logovanie, ak zákazník neexistuje
            return ResponseEntity.badRequest().body("Zákazník s poskytnutým ID neexistuje.");
        }

        // Parsing dátumu registrácie vozidla
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate registeredAt;
        try {
            registeredAt = LocalDate.parse(vehicle.getRegisteredAt(), formatter);  // Pokus o parsing dátumu
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Neplatný formát dátumu registrácie vozidla. Očakávaný formát: DD.MM.YYYY.");  // Chyba pri neplatnom formáte dátumu
        }

        try {
            // Vytvorenie a uloženie nového vozidla
            Vehicle newvehicle = new Vehicle();
            newvehicle.setCustomerId(vehicle.getCustomerId());
            newvehicle.setBrand(vehicle.getBrand());
            newvehicle.setModel(vehicle.getModel());
            newvehicle.setRegisteredAt(vehicle.getRegisteredAt());  // Nastavenie správneho dátumu registrácie
            newvehicle.setVin(vehicle.getVin());
            newvehicle.setPlateNo(vehicle.getPlateNo());
            newvehicle.setCreatedAt(LocalDateTime.now());  // Uloženie aktuálneho dátumu a času ako LocalDateTime

            // Uloženie vozidla do databázy
            vehicleRepository.save(newvehicle);

            // Logovanie úspešného pridania vozidla
            logger.info("Vozidlo úspešne pridané pre uživateľa {}: Značka: {}, Model: {}, VIN: {}, ŠPZ: {}, Dátum registrácie: {}",
                    newvehicle.getCustomerId(),newvehicle.getBrand(), newvehicle.getModel(),
                    newvehicle.getVin(), newvehicle.getPlateNo(),
                    newvehicle.getRegisteredAt());

            return ResponseEntity.ok("Vozidlo úspešne pridané.");  // Vráti úspešnú odpoveď
        } catch (Exception e) {
            // Logovanie chyby pri ukladaní vozidla
            logger.error("Chyba pri pridávaní vozidla: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Chyba pri spracovaní požiadavky!");  // Vráti internú chybu servera
        }
    }

    // Riešenie GET požiadavky na /vehicle/add (nie je podporované)
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ResponseEntity<?> handleGetRequest() {
        logger.debug("GET request is not supported for /vehicle/add.");  // Logovanie neplatnej GET požiadavky
        return ResponseEntity.status(405).body("GET method is not allowed. Use POST.");  // Vráti 405 Method Not Allowed
    }

    // Endpoint na získanie všetkých vozidiel
    @GetMapping("/showall")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        if (vehicles.isEmpty()) {
            return ResponseEntity.noContent().build();  // Vráti 204 No Content, ak nie sú žiadne vozidlá
        }
        return ResponseEntity.ok(vehicles);  // Vráti zoznam všetkých vozidiel (200 OK)
    }

    // Endpoint na získanie vozidla podľa ID
    @GetMapping("/id/{id}")  // Použitie "/{id}" pre získanie vozidla podľa ID
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Integer id) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (vehicle.isEmpty()) {
            return ResponseEntity.notFound().build();  // Vráti 404 Not Found, ak vozidlo neexistuje
        }
        return ResponseEntity.ok(vehicle.get());  // Vráti 200 OK so záznamom vozidla
    }

    // Endpoint na získanie vozidla podľa VIN kódu
    @GetMapping("/vin/{vin}")  // Použitie "/vin/{vin}" pre získanie vozidla podľa VIN
    public ResponseEntity<Vehicle> getVehicleByVin(@PathVariable String vin) {
        Optional<Vehicle> vehicle = vehicleRepository.findByVin(vin);
        if (vehicle.isEmpty()) {
            return ResponseEntity.notFound().build();  // Vráti 404 Not Found, ak vozidlo neexistuje
        }
        return ResponseEntity.ok(vehicle.get());  // Vráti 200 OK so záznamom vozidla
    }

    // Endpoint na získanie vozidiel podľa ID zákazníka
    @GetMapping("/customerid/{customerId}")  // Použitie "/{customerId}" pre získanie vozidiel podľa ID zákazníka
    public ResponseEntity<List<Vehicle>> getVehiclesByCustomerId(@PathVariable Integer customerId) {
        List<Vehicle> vehicles = vehicleRepository.findByCustomerId(customerId);
        if (vehicles.isEmpty()) {
            return ResponseEntity.noContent().build();  // Vráti 204 No Content, ak žiadne vozidlo pre zákazníka neexistuje
        }
        return ResponseEntity.ok(vehicles);  // Vráti zoznam vozidiel (200 OK)
    }
}
