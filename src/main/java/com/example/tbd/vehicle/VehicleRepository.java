package com.example.tbd.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    List<Vehicle> findAll(); // Find all vehicles
    Optional<Vehicle> findById(Integer id); // Find vehicle by ID
    Optional<Vehicle> findByVin(String vin);   // Find vehicle by VIN
    Optional<Vehicle> findByPlateNo(String plateNo);
    List<Vehicle> findByCustomerId(Integer customerId); // Find vehicles by customer ID
    boolean existsByPlateNo(String plateNo);
    boolean existsByVin(String vin);
    List<Vehicle> findByCustomerIdAndDeleted(Integer customerId, String deleted);
    boolean existsByPlateNoAndDeleted(String plateNo, String deleted);
    @Query("SELECT COUNT(v) FROM Vehicle v WHERE v.id is not null")
    long countVehicle();

}
