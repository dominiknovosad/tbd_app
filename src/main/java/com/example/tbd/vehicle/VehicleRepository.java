package com.example.tbd.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    List<Vehicle> findAll(); // Find all vehicles
    Optional<Vehicle> findById(Long id); // Find vehicle by ID
    Optional<Vehicle> findByVin(String vin);   // Find vehicle by VIN
    Optional<Vehicle> findByPlateNo(String plateNo);
    List<Vehicle> findByCustomerId(Long customerId); // Find vehicles by customer ID
    boolean existsByPlateNo(String plateNo);
    boolean existsByVin(String vin);
    List<Vehicle> findByCustomerIdAndDeleted(Long customerId, String deleted);
    boolean existsByPlateNoAndDeleted(String plateNo, String deleted);
    @Query(value = "SELECT COUNT(*) FROM vehicle WHERE deleted = 'N'", nativeQuery = true)
    long countVehicles();

}
