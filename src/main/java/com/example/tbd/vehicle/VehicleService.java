package com.example.tbd.vehicle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    // Počet vozidiel
    public long countVehicles() {
        return vehicleRepository.countVehicles();
    }

    // Získanie všetkých vozidiel ako DTO
    public List<VehicleDTO> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll(); // Načítanie všetkých vozidiel z databázy
        return vehicleMapper.toVehicleOutputList(vehicles); // Použitie MapStruct na mapovanie
    }

    // Získanie vozidla podľa ID ako DTO
    public Optional<VehicleDTO> findById(Long id) {
        return vehicleRepository.findById(id)
                .map(vehicleMapper::toVehicleOutput); // Transformácia entity na DTO pomocou MapStruct
    }

    public Optional<VehicleDTO> findByVin(String vin) {
        return vehicleRepository.findByVin(vin)
                .map(vehicleMapper::toVehicleOutput); // Transformácia entity na DTO pomocou MapStruct
    }

    public List<VehicleDTO> findByCustomerId(Long customerId) {
        List<Vehicle> vehicles = vehicleRepository.findByCustomerIdAndDeleted(customerId, "N"); // Načítanie aktívnych vozidiel
        return vehicleMapper.toVehicleOutputList(vehicles); // Transformácia entít na DTO pomocou MapStruct
    }

}
