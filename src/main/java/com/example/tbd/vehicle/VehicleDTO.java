package com.example.tbd.vehicle;

public class VehicleDTO {

    private Long id; // Identifikátor vozidla
    private String brand; // Značka vozidla
    private String model; // Model vozidla
    private String registeredAt; // Dátum registrácie
    private String vin; // VIN kód
    private String plateNo; // ŠPZ
    private String fuel; // Typ paliva
    private String color; // Farba vozidla
    private Integer mileage; // Počet kilometrov
    private String transmissionType; // Typ prevodovky
    private String tireSize; // Rozmer pneumatík
    private String lastServiced; // Posledný servis
    private String deleted; // Stav deleted
    private String createdAt; // Dátum vytvorenia

    // Gettery a settery pre všetky atribúty

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public String getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(String transmissionType) {
        this.transmissionType = transmissionType;
    }

    public String getTireSize() {
        return tireSize;
    }

    public void setTireSize(String tireSize) {
        this.tireSize = tireSize;
    }

    public String getLastServiced() {
        return lastServiced;
    }

    public void setLastServiced(String lastServiced) {
        this.lastServiced = lastServiced;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
