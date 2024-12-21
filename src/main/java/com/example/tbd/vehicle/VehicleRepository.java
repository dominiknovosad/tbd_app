package com.example.tbd.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    // Find all vehicles
    List<Vehicle> findAll();

    // Find vehicle by ID
    Optional<Vehicle> findById(Integer id);

    // Find vehicle by VIN
    Optional<Vehicle> findByVin(String vin);

    // Find vehicles by customer ID
    List<Vehicle> findByCustomerId(Integer customerId);
}
