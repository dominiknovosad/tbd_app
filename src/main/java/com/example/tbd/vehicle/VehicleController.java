package com.example.tbd.vehicle;

import com.example.tbd.customer.CustomerRepository;  // Import pre CustomerRepository, ktoré sa používa na kontrolu existencie zákazníka
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/vehicle")  // Definuje URL cestu pre všetky endpointy tejto triedy
@Tag(name = "Vehicle Controller", description = "API pre správu vozidel")
public class VehicleController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class); // SLF4J logger
    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final VehicleService vehicleService;

    // Konštruktor s injekciou všetkých závislostí
    @Autowired
    public VehicleController(VehicleRepository vehicleRepository,
                             CustomerRepository customerRepository,
                             VehicleService vehicleService) {
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
        this.vehicleService = vehicleService;
    }

    // Endpoint pre pridanie nového vozidla
    @PostMapping("/add")
    public ResponseEntity<?> addVehicle(@RequestBody Vehicle vehicle) {
        logger.debug("Prijatý VehicleRequest - {}", vehicle); // Logovanie prijatého požiadavky na pridanie vozidla

        // Validácia povinných údajov
        if (vehicle.getCustomerId() == null || vehicle.getCustomerId() <= 0 ||
                vehicle.getBrand() == null || vehicle.getBrand().isEmpty() ||
                vehicle.getModel() == null || vehicle.getModel().isEmpty() ||
                vehicle.getRegisteredAt() == null || vehicle.getRegisteredAt().isEmpty() ||
                vehicle.getVin() == null || vehicle.getVin().isEmpty() ||
                vehicle.getPlateNo() == null || vehicle.getPlateNo().isEmpty()) {
            return ResponseEntity.badRequest().body("Neplatné údaje pre vozidlo!"); // Vráti chybu ak sú povinné údaje neúplné
        }

        // Kontrola, či zákazník s daným ID existuje
        if (!customerRepository.existsById(vehicle.getCustomerId())) {
            logger.warn("Zákazník s ID {} neexistuje.", vehicle.getCustomerId());
            return ResponseEntity.badRequest().body("Zákazník s poskytnutým ID neexistuje.");
        }

        // Parsing dátumu registrácie vozidla
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate registeredAt;
        try {
            registeredAt = LocalDate.parse(vehicle.getRegisteredAt(), formatter);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Neplatný formát dátumu registrácie vozidla. Očakávaný formát: DD.MM.YYYY.");
        }

        // Validácia voliteľných údajov
        if (vehicle.getFuel() != null && vehicle.getFuel().isEmpty()) {
            return ResponseEntity.badRequest().body("Typ paliva nemôže byť prázdny, ak je uvedený!");
        }
        if (vehicle.getColor() != null && vehicle.getColor().isEmpty()) {
            return ResponseEntity.badRequest().body("Farba nemôže byť prázdna, ak je uvedená!");
        }
        if (vehicle.getMileage() != null && vehicle.getMileage() < 0) {
            return ResponseEntity.badRequest().body("Najazdené kilometre nemôžu byť záporné!");
        }
        if (vehicle.getTireSize() != null && vehicle.getTireSize().isEmpty()) {
            return ResponseEntity.badRequest().body("Rozmer pneumatík nemôže byť prázdny, ak je uvedený!");
        }
        if (vehicle.getLastServiced() != null && vehicle.getLastServiced().isEmpty()) {
            return ResponseEntity.badRequest().body("Dátum posledného servisu nemôže byť prázdny, ak je uvedený!");
        }
        if (vehicle.getTransmissionType() != null && vehicle.getTransmissionType().isEmpty()) {
            return ResponseEntity.badRequest().body("Typ prevodovky nemôže byť prázdny, ak je uvedený!");
        }

        try {
            // Kontrola duplicity SPZ iba pre záznamy, kde deleted = 'N'
            if (vehicleRepository.existsByPlateNoAndDeleted(vehicle.getPlateNo(), "N")) {
                logger.warn("Vozidlo s SPZ {} už existuje.", vehicle.getPlateNo());
                return ResponseEntity.badRequest().body("Vozidlo so zadanou SPZ už existuje.");
            }
            if (vehicleRepository.existsByVin(vehicle.getVin())) {
                logger.warn("Vozidlo s VIN {} už existuje.", vehicle.getVin());
                return ResponseEntity.badRequest().body("Vozidlo so zadaným VIN už existuje.");
            }

            // Vytvorenie a uloženie nového vozidla
            Vehicle newVehicle = new Vehicle();
            newVehicle.setCustomerId(vehicle.getCustomerId());
            newVehicle.setBrand(vehicle.getBrand());
            newVehicle.setModel(vehicle.getModel());
            newVehicle.setRegisteredAt(vehicle.getRegisteredAt());
            newVehicle.setVin(vehicle.getVin());
            newVehicle.setPlateNo(vehicle.getPlateNo());
            newVehicle.setFuel(vehicle.getFuel());
            newVehicle.setColor(vehicle.getColor());
            newVehicle.setMileage(vehicle.getMileage());
            newVehicle.setTireSize(vehicle.getTireSize());
            newVehicle.setLastServiced(vehicle.getLastServiced());
            newVehicle.setTransmissionType(vehicle.getTransmissionType());
            newVehicle.setDeleted("N"); // Nastavenie defaultnej hodnoty pre deleted
            newVehicle.setCreatedAt(LocalDateTime.now()); // Nastavenie aktuálneho času

            // Uloženie vozidla do databázy
            vehicleRepository.save(newVehicle);

            logger.info("Vozidlo úspešne pridané pre uživateľa {}: Značka: {}, Model: {}, VIN: {}, ŠPZ: {}, Dátum registrácie: {}",
                    newVehicle.getCustomerId(), newVehicle.getBrand(), newVehicle.getModel(),
                    newVehicle.getVin(), newVehicle.getPlateNo(),
                    newVehicle.getRegisteredAt());

            return ResponseEntity.ok("Vozidlo úspešne pridané.");
        } catch (Exception e) {
            logger.error("Chyba pri pridávaní vozidla: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Chyba pri spracovaní požiadavky!");
        }
    }


    @GetMapping("/count")
    @Operation(summary = "Počet vozidiel", description = "Zobrazí počet vozidiel celkovo")
    public ResponseEntity<String> countVehicles() {
        long count = vehicleService.countVehicles();
        return ResponseEntity.ok("Celkový počet registrovaných vozidiel: " + count);
    }

    // Riešenie GET požiadavky na /vehicle/add (nie je podporované)
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ResponseEntity<?> handleGetRequest() {
        logger.debug("GET request is not supported for /vehicle/add.");  // Logovanie neplatnej GET požiadavky
        return ResponseEntity.status(405).body("GET method is not allowed. Use POST.");  // Vráti 405 Method Not Allowed
    }

    // Endpoint na získanie všetkých vozidiel
    @GetMapping("/showall")
    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        List<VehicleDTO> vehicles = vehicleService.getAllVehicles();
        if (vehicles.isEmpty()) {
            return ResponseEntity.noContent().build(); // Vráti 204 No Content, ak nie sú žiadne vozidlá
        }
        return ResponseEntity.ok(vehicles); // Vráti zoznam všetkých vozidiel (200 OK)
    }

    // Endpoint na získanie vozidla podľa ID
    @GetMapping("/id/{id}")  // Endpoint na získanie vozidla podľa ID
    public ResponseEntity<?> getVehicleById(@PathVariable Long id) {
        Optional<VehicleDTO> vehicleOutput = vehicleService.findById(id);
        if (vehicleOutput.isEmpty()) {
            // Vráti 404 Not Found s vlastnou textovou správou
            logger.debug("Vozidlo s ID {} nebolo nájdené.", id);
            return ResponseEntity.status(404).body("Vozidlo s ID " + id + " nebolo nájdené.");
        }
        return ResponseEntity.ok(vehicleOutput.get()); // Vráti 200 OK so záznamom vozidla
    }

    // Endpoint na získanie vozidla podľa VIN kódu
    @GetMapping("/vin/{vin}")  // Endpoint na získanie vozidla podľa VIN
    public ResponseEntity<?> getVehicleByVin(@PathVariable String vin) {
        Optional<VehicleDTO> vehicleOutput = vehicleService.findByVin(vin);
        if (vehicleOutput.isEmpty()) {
            logger.debug("Vozidlo s VIN {} nebolo nájdené.", vin);
            // Vráti 404 Not Found s vlastnou textovou správou
            return ResponseEntity.status(404).body("Vozidlo s VIN " + vin + " nebolo nájdené.");
        }
        return ResponseEntity.ok(vehicleOutput.get());
    }
    // Endpoint na získanie vozidiel podľa ID zákazníka
    @GetMapping("/customerid/{customerId}")
    public ResponseEntity<?> getVehiclesByCustomerId(@PathVariable Long customerId) {
        List<VehicleDTO> vehicles = vehicleService.findByCustomerId(customerId);
        logger.debug("Zobrazené aktívne vozidlá pre customerId {}: {}", customerId, vehicles);
        if (vehicles.isEmpty()) {
            // Vráti 204 No Content s vlastnou správou
            return ResponseEntity.status(204).body("Pre customerId " + customerId + " neexistujú žiadne aktívne vozidlá.");
        }
        return ResponseEntity.ok(vehicles);
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateVehicle(@RequestBody Vehicle updatedVehicle) {
        logger.debug("Prijatý požiadavka na aktualizáciu vozidla - {}", updatedVehicle);

        // Validácia vstupných údajov
        if (updatedVehicle.getId() == null || updatedVehicle.getId() <= 0) {
            return ResponseEntity.badRequest().body("Neplatné ID vozidla!");
        }

        // Kontrola, či vozidlo s daným ID existuje
        Optional<Vehicle> existingVehicleOptional = vehicleRepository.findById(updatedVehicle.getId());
        if (existingVehicleOptional.isEmpty()) {
            logger.warn("Vozidlo s ID {} neexistuje.", updatedVehicle.getId());
            return ResponseEntity.badRequest().body("Vozidlo s poskytnutým ID neexistuje.");
        }

        Vehicle existingVehicle = existingVehicleOptional.get();

        try {
            // Aktualizácia povinných hodnôt
            if (updatedVehicle.getCustomerId() != null) {
                existingVehicle.setCustomerId(updatedVehicle.getCustomerId());
            }
            if (updatedVehicle.getBrand() != null && !updatedVehicle.getBrand().isEmpty()) {
                existingVehicle.setBrand(updatedVehicle.getBrand());
            }
            if (updatedVehicle.getModel() != null && !updatedVehicle.getModel().isEmpty()) {
                existingVehicle.setModel(updatedVehicle.getModel());
            }
            if (updatedVehicle.getRegisteredAt() != null && !updatedVehicle.getRegisteredAt().isEmpty()) {
                // Validácia formátu dátumu
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                try {
                    LocalDate.parse(updatedVehicle.getRegisteredAt(), formatter);
                    existingVehicle.setRegisteredAt(updatedVehicle.getRegisteredAt());
                } catch (DateTimeParseException e) {
                    return ResponseEntity.badRequest().body("Neplatný formát dátumu registrácie vozidla. Očakávaný formát: yyyy-MM-dd.");
                }
            }
            if (updatedVehicle.getVin() != null && !updatedVehicle.getVin().isEmpty()) {
                // Kontrola duplicity VIN
                if (!existingVehicle.getVin().equals(updatedVehicle.getVin()) &&
                        vehicleRepository.existsByVin(updatedVehicle.getVin())) {
                    return ResponseEntity.badRequest().body("Vozidlo s poskytnutým VIN už existuje.");
                }
                existingVehicle.setVin(updatedVehicle.getVin());
            }
            if (updatedVehicle.getPlateNo() != null && !updatedVehicle.getPlateNo().isEmpty()) {
                // Kontrola duplicity SPZ
                if (!existingVehicle.getPlateNo().equals(updatedVehicle.getPlateNo()) &&
                        vehicleRepository.existsByPlateNo(updatedVehicle.getPlateNo())) {
                    return ResponseEntity.badRequest().body("Vozidlo so zadanou SPZ už existuje.");
                }
                existingVehicle.setPlateNo(updatedVehicle.getPlateNo());
            }

            // Aktualizácia voliteľných hodnôt
            if (updatedVehicle.getFuel() != null) {
                existingVehicle.setFuel(updatedVehicle.getFuel());
            }
            if (updatedVehicle.getColor() != null) {
                existingVehicle.setColor(updatedVehicle.getColor());
            }
            if (updatedVehicle.getMileage() != null) {
                existingVehicle.setMileage(updatedVehicle.getMileage());
            }
            if (updatedVehicle.getTireSize() != null) {
                existingVehicle.setTireSize(updatedVehicle.getTireSize());
            }
            if (updatedVehicle.getLastServiced() != null) {
                // Validácia formátu dátumu
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                try {
                    LocalDate.parse(updatedVehicle.getLastServiced(), formatter);
                    existingVehicle.setLastServiced(updatedVehicle.getLastServiced());
                } catch (DateTimeParseException e) {
                    return ResponseEntity.badRequest().body("Neplatný formát dátumu posledného servisu. Očakávaný formát: yyyy-MM-dd.");
                }
            }
            if (updatedVehicle.getTransmissionType() != null) {
                existingVehicle.setTransmissionType(updatedVehicle.getTransmissionType());
            }

            // Uloženie aktualizovaného vozidla
            vehicleRepository.save(existingVehicle);

            logger.info("Vozidlo s ID {} úspešne aktualizované.", existingVehicle.getId());
            return ResponseEntity.ok("Vozidlo úspešne aktualizované.");
        } catch (Exception e) {
            logger.error("Chyba pri aktualizácii vozidla: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Chyba pri aktualizácii vozidla!");
        }
    }


    @PutMapping("/delupdate/{id}")
    public ResponseEntity<?> deleteUpdateVehicle(@PathVariable Integer id) {
        logger.debug("Prijatý požiadavka na označenie vozidla s ID {} ako vymazaného.", id);

        // Validácia ID vozidla
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body("Neplatné ID vozidla!");
        }

        // Kontrola, či vozidlo s daným ID existuje
        Optional<Vehicle> existingVehicleOptional = vehicleRepository.findById(id);
        if (existingVehicleOptional.isEmpty()) {
            logger.warn("Vozidlo s ID {} neexistuje.", id);
            return ResponseEntity.badRequest().body("Vozidlo s poskytnutým ID neexistuje.");
        }

        try {
            Vehicle existingVehicle = existingVehicleOptional.get();

            // Aktualizácia hodnoty stĺpca "deleted" na "Y"
            existingVehicle.setDeleted("Y");

            // Uloženie zmeny do databázy
            vehicleRepository.save(existingVehicle);

            logger.info("Vozidlo s ID {} bolo označené ako vymazané.", id);
            return ResponseEntity.ok("Vozidlo bolo úspešne označené ako vymazané.");
        } catch (Exception e) {
            logger.error("Chyba pri označovaní vozidla s ID {} ako vymazaného: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Chyba pri spracovaní požiadavky!");
        }
    }
}
