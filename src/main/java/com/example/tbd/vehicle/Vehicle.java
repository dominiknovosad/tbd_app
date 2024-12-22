package com.example.tbd.vehicle;

import com.fasterxml.jackson.annotation.JsonFormat;  // Import na formátovanie dátumu pre JSON
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;  // Import pre JPA anotácie
import org.hibernate.annotations.CreationTimestamp;  // Import pre automatické nastavenie dátumu a času vytvorenia

import java.time.LocalDate;  // Import pre typ LocalDate, ktorý obsahuje iba dátum
import java.time.LocalDateTime;  // Import pre typ LocalDateTime, ktorý obsahuje dátum aj čas

@Entity  // Anotácia pre označenie triedy ako entitu, ktorá bude mapovaná na databázovú tabuľku
@Table(name = "vehicle")  // Názov tabuľky v databáze, na ktorú bude entita mapovaná
public class Vehicle {

    @Id  // Označuje primárny kľúč
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Nastavenie automatického generovania hodnôt pre primárny kľúč
    private Integer id;  // ID vozidla

    @Column(name = "customer_id", nullable = false)  // Názov stĺpca v databáze, s podmienkou, že hodnota nemôže byť null
    private Integer customerId;  // ID zákazníka, ktorý vlastní vozidlo

    @Column(nullable = false)  // Označuje, že hodnota nesmie byť null
    private String brand;  // Značka vozidla

    @Column(nullable = false)  // Označuje, že hodnota nesmie byť null
    private String model;  // Model vozidla

    // Formátovanie dátumu registrácie vozidla na formát "yyyy-MM-dd"
    @Column(name = "registered_at", nullable = false)  // Názov stĺpca pre dátum registrácie vozidla
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")  // Formát pre JSON výstup
    private String registeredAt;  // Dátum registrácie vozidla (LocalDate obsahuje iba dátum)

    @Column(name = "VIN", unique = true, nullable = false, length = 17)  // Názov stĺpca pre VIN kód s dĺžkou 17 znakov a unikátnosťou
    private String vin;  // VIN kód vozidla

    @Column(name = "plate_no", unique = true, nullable = false)
    @JsonProperty("plate_no")
    private String plateNo;

    // Vytvorenie dátumu a času pre časovú značku "created_at", nastavuje sa automaticky
    @Column(name = "created_at", nullable = false, updatable = false)  // Stĺpec pre dátum a čas vytvorenia, ktorý sa neaktualizuje
    @CreationTimestamp  // Automatické nastavenie času vytvorenia
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", shape = JsonFormat.Shape.STRING)  // Formát pre JSON výstup, zahŕňa aj čas
    private LocalDateTime createdAt;  // Dátum a čas vytvorenia vozidla (LocalDateTime obsahuje dátum aj čas)

    // Gettery a settery pre všetky atribúty
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
