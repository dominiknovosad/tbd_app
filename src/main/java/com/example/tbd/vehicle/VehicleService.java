package com.example.tbd.vehicle;

import com.example.tbd.Products.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class VehicleService {
    private VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }
    public long countVehicles() {
        return vehicleRepository.countVehicles();
    }
}
