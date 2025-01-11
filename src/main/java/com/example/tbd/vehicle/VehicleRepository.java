package com.example.tbd.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

    // Find all vehicles
    List<Vehicle> findAll();

    // Find vehicle by ID
    Optional<Vehicle> findById(Integer id);

    // Find vehicle by VIN
    Optional<Vehicle> findByVin(String vin);

    Optional<Vehicle> findByPlateNo(String plateNo);

    // Find vehicles by customer ID
    List<Vehicle> findByCustomerId(Integer customerId);

    boolean existsByPlateNo(String plateNo);

    boolean existsByVin(String vin);
    List<Vehicle> findByCustomerIdAndDeleted(Integer customerId, String deleted);
    boolean existsByPlateNoAndDeleted(String plateNo, String deleted);

}
