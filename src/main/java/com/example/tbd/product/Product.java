package com.example.tbd.product;

import com.fasterxml.jackson.annotation.JsonFormat;  // Import na formátovanie dátumu pre JSON
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;  // Import pre JPA anotácie
import org.hibernate.annotations.CreationTimestamp;  // Import pre automatické nastavenie dátumu a času vytvorenia

import java.time.LocalDateTime;  // Import pre typ LocalDateTime, ktorý obsahuje dátum aj čas

@Entity  // Anotácia pre označenie triedy ako entitu, ktorá bude mapovaná na databázovú tabuľku
@Table(name = "product")  // Názov tabuľky v databáze, na ktorú bude entita mapovaná
public class Product {
    @Id  // Označuje primárny kľúč
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Nastavenie automatického generovania hodnôt pre primárny kľúč
    private Integer id;  // ID služby

    @Column(name = "company_id", nullable = false)  // Názov stĺpca v databáze, s podmienkou, že hodnota nemôže byť null
    private Integer companyId;  // ID firmy, ktorý vlastní vozidlo

    @Column(nullable = false)  // Označuje, že hodnota nesmie byť null
    private String name;  // Názov služby

    @Column(nullable = false)  // Označuje, že hodnota nesmie byť null
    private String description;  // Popis služby

    @Column(name = "price", nullable = false, length = 10)  // Názov stĺpca pre cenu s dĺžkou 10 znakov
    private String price;  // cena služby


    @Column(name = "deleted", nullable = false)
    private String deleted = "N";

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

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @JsonIgnore
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getDeleted() {
        return deleted;
    }
    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }
}
