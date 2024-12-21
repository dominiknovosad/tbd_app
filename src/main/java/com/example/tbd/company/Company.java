package com.example.tbd.company;

import com.fasterxml.jackson.annotation.JsonFormat; // Import pre formátovanie dátumu a času pri serializácii do JSON
import jakarta.persistence.*; // Import pre JPA anotácie (Entity, Table, Column, Id a pod.)
import jakarta.validation.constraints.Digits; // Import pre validáciu číslic (kontrola počtu číslic)
import jakarta.validation.constraints.NotNull; // Import pre validáciu, aby hodnota nebola null (prázdna)
import org.hibernate.annotations.CreationTimestamp; // Import pre automatické nastavenie dátumu a času pri vytvorení záznamu

import java.time.LocalDateTime; // Import pre LocalDateTime na uchovanie dátumu a času

@Entity // Označuje, že ide o JPA entitu
@Table(name = "company") // Určuje názov tabuľky v databáze, s ktorou bude táto entita spojená
public class Company {

    @Id // Označuje primárny kľúč tejto entity
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Anotácia pre automatické generovanie hodnôt pre primárny kľúč v databáze (inkrementované číslo)
    private Integer id; // ID spoločnosti, bude primárnym kľúčom v databáze

    @Column(name = "company_name") // Určuje názov stĺpca v databáze, ktorý bude zodpovedať tomuto atribútu
    @NotNull(message = "Názov spoločnosti nesmie byť prázdny") // Validácia, aby názov spoločnosti nebol prázdny
    private String companyName; // Názov spoločnosti

    @Column(name = "ico") // Určuje názov stĺpca pre IČO
    @NotNull(message = "IČO nesmie byť prázdne") // Validácia, aby IČO nebolo prázdne
    private Integer ico; // IČO spoločnosti

    @Column(name = "email") // Určuje názov stĺpca pre email
    @NotNull(message = "Email nesmie byť prázdny") // Validácia, aby email nebol prázdny
    private String email; // Email spoločnosti

    @Column(name = "telephone") // Určuje názov stĺpca pre telefón
    @NotNull(message = "Telefónne číslo nesmie byť prázdne") // Validácia, aby telefónne číslo nebolo prázdne
    @Digits(fraction = 0, integer = 10, message = "Telefón musí mať maximálne 10 číslic") // Validácia, že telefónne číslo môže mať maximálne 10 číslic
    private String telephone; // Telefónne číslo spoločnosti

    @Column(name = "address") // Určuje názov stĺpca pre adresu
    @NotNull(message = "Adresa nesmie byť prázdna") // Validácia, aby adresa nebola prázdna
    private String address; // Adresa spoločnosti

    @Column(name = "password") // Určuje názov stĺpca pre heslo
    @NotNull(message = "Heslo nesmie byť prázdne") // Validácia, aby heslo nebolo prázdne
    private String password; // Heslo spoločnosti

    @Column(name = "created_at", updatable = false) // Určuje názov stĺpca pre dátum a čas vytvorenia záznamu
    @CreationTimestamp // Automaticky nastaví čas vytvorenia záznamu pri jeho uložení do databázy
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") // Formátovanie dátumu a času do formátu "yyyy-MM-dd HH:mm:ss" pri serializácii do JSON
    private LocalDateTime createdAt; // Dátum a čas vytvorenia záznamu (timestamp)

    // Gettery a settery pre jednotlivé atribúty

    public Integer getId() {
        return id; // Getter pre ID spoločnosti
    }

    public void setId(Integer id) {
        this.id = id; // Setter pre ID spoločnosti
    }

    public String getCompanyName() {
        return companyName; // Getter pre názov spoločnosti
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName; // Setter pre názov spoločnosti
    }

    public Integer getIco() {
        return ico; // Getter pre IČO spoločnosti
    }

    public void setIco(Integer ico) {
        this.ico = ico; // Setter pre IČO spoločnosti
    }

    public String getEmail() {
        return email; // Getter pre email spoločnosti
    }

    public void setEmail(String email) {
        this.email = email; // Setter pre email spoločnosti
    }

    public String getTelephone() {
        return telephone; // Getter pre telefónne číslo spoločnosti
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone; // Setter pre telefónne číslo spoločnosti
    }

    public String getAddress() {
        return address; // Getter pre adresu spoločnosti
    }

    public void setAddress(String address) {
        this.address = address; // Setter pre adresu spoločnosti
    }

    public String getPassword() {
        return password; // Getter pre heslo spoločnosti
    }

    public void setPassword(String password) {
        this.password = password; // Setter pre heslo spoločnosti
    }

    public LocalDateTime getCreatedAt() {
        return createdAt; // Getter pre dátum a čas vytvorenia spoločnosti
    }

    // Override metódy toString() pre lepšie vypísanie objektu
    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", ico=" + ico +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", address='" + address + '\'' +
                ", createdAt=" + createdAt +
                '}'; // Vypíše všetky informácie o spoločnosti
    }
}
