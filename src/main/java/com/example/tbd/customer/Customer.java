package com.example.tbd.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.core.style.ToStringCreator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Trieda predstavujúca entitu Customer v databáze
@Entity
@Table(name = "customer") // Názov tabuľky v databáze
public class Customer {

    @Id // Označuje primárny kľúč
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatické generovanie hodnoty ID
    private Integer id;

    @Column(name = "name", nullable = false) // Názov stĺpca v tabuľke pre meno zákazníka
    @NotNull // Zabezpečuje, že meno nemôže byť prázdne
    private String name;

    @Column(name = "surname", nullable = false) // Názov stĺpca v tabuľke pre priezvisko zákazníka
    @NotNull // Zabezpečuje, že priezvisko nemôže byť prázdne
    private String surname;

    @Column(name = "city", nullable = false) // Názov stĺpca v tabuľke pre mesto
    @NotNull // Zabezpečuje, že mesto nemôže byť prázdne
    private String city;

    @Column(name = "telephone", nullable = false) // Názov stĺpca v tabuľke pre telefónne číslo
    @NotNull // Zabezpečuje, že telefónne číslo nemôže byť prázdne
    @Digits(fraction = 0, integer = 10) // Zabezpečuje, že telefónne číslo má presne 10 číslic
    private String telephone;

    // Dátum narodenia zákazníka, s formátovaním na 'dd.MM.yyyy'
    @Column(name = "birthdate", nullable = false) // Názov stĺpca pre dátum narodenia
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy") // Formát pre serializáciu a deserializáciu
    private LocalDate birthdate;

    @Column(name = "email", nullable = false) // Názov stĺpca pre email zákazníka
    @NotNull // Zabezpečuje, že email nemôže byť prázdny
    private String email;

    @Column(name = "password", nullable = false) // Názov stĺpca pre heslo zákazníka
    @NotNull // Zabezpečuje, že heslo nemôže byť prázdne
    private String password;

    // Dátum a čas vytvorenia zákazníka, s použitím LocalDateTime a @CreationTimestamp pre automatické nastavenie
    @CreationTimestamp // Automatické nastavenie hodnoty pri vytvorení záznamu
    @Column(name = "created_at", updatable = false) // Názov stĺpca pre dátum a čas vytvorenia
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") // Formát pre zobrazenie dátumu a času
    private LocalDateTime createdAt;


    @Column(name = "role_id", nullable = false) // Názov stĺpca pre heslo zákazníka
    @NotNull
    private Integer roleId = 1;

    // Override metódy toString pre ľahšie zobrazenie objektu Customer
    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("id", this.getId()) // Pridanie id zákazníka
                .append("name", this.getName()) // Pridanie mena zákazníka
                .append("surname", this.getSurname()) // Pridanie priezviska zákazníka
                .append("birth_date", this.getBirthdate()) // Pridanie dátumu narodenia zákazníka
                .append("email", this.getEmail()) // Pridanie emailu zákazníka
                .append("telephone", this.getTelephone()) // Pridanie telefónneho čísla zákazníka
                .append("city", this.getCity()) // Pridanie mesta zákazníka
                .append("password", this.getPassword()) // Pridanie hesla zákazníka
                .append("created_at", this.getCreatedAt()) // Pridanie dátumu a času vytvorenia zákazníka
                .toString();
    }

    // Rozhranie pre CustomerRepository, ktoré umožňuje prístup k údajom zákazníkov v databáze
    public interface CustomerRepository extends JpaRepository<Customer, Integer> {
        boolean existsById(Integer id);  // Metóda na overenie existencie zákazníka podľa ID
    }

    // Gettery a settery pre všetky atribúty triedy Customer

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
   public Integer getRoleId() {
        return roleId;
    }
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}
