package com.example.tbd.vehicle;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    VehicleMapper INSTANCE = Mappers.getMapper(VehicleMapper.class);

    // Mapovanie jednej entity na DTO
    VehicleDTO toVehicleOutput(Vehicle vehicle);

    // Mapovanie zoznamu entít na zoznam DTO
    List<VehicleDTO> toVehicleOutputList(List<Vehicle> vehicles);
}