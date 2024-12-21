package com.example.tbd.vehicle;

// Statická vnútorná trieda pre VehicleRequest
public class VehicleRequest {

    // Atribúty pre údaje o vozidle
    private Integer customerId;  // ID zákazníka, ktorý vlastní vozidlo
    private String brand;        // Značka vozidla (napr. Škoda, BMW)
    private String model;        // Model vozidla (napr. Superb, X5)
    private String registeredAt; // Dátum registrácie vozidla vo formáte "DD.MM.YYYY"
    private String vin;          // VIN (Vehicle Identification Number) kód vozidla

    // Getter a Setter pre customerId
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    // Getter a Setter pre značku vozidla
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    // Getter a Setter pre model vozidla
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    // Getter a Setter pre dátum registrácie vozidla
    public String getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(String registeredAt) {
        this.registeredAt = registeredAt;
    }

    // Getter a Setter pre VIN číslo vozidla
    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

}
