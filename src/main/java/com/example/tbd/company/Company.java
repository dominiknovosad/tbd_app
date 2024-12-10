package com.example.tbd.company;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.style.ToStringCreator;

import java.util.Date;

@Entity
@Table(name = "company") // Název tabulky v databázi, do které bude tato entita mapována.
public class Company  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    @NotNull // Validace - telefonní číslo nesmí být prázdné
    private String name;

    @Column(name = "surname")
    @NotNull // Validace - telefonní číslo nesmí být prázdné
    private String surname;

    @Column(name = "city")
    @NotNull // Validace - telefonní číslo nesmí být prázdné
    private String city;

    @Column(name = "telephone")
    @NotNull // Validace - telefonní číslo nesmí být prázdné
    @Digits(fraction = 0, integer = 10) // Validace - telefonní číslo musí být číselné a maximálně 10 číslic dlouhé
    private String telephone;

    @Column(name = "birthdate")
    @NotNull
    @JsonFormat(pattern = "dd-mm-yyyy")  // Formát dátumu, ak je to potrebné
    private Date birthdate;

    @Column(name = "email")
    @NotNull // Validace - nesmí být prázdné
    private String email;

    @Column(name = "password")
    @NotNull // Validace - nesmí být prázdné
    private String password;


    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("id", this.getId())
                .append("name", this.getName())
                .append("surname", this.getSurname())
                .append("birth_date", this.getBirthdate())
                .append("email", this.getEmail())
                .append("telephone", this.getTelephone())
                .append("city", this.getCity())
                .append("password", this.getPassword())
                .toString();
    }


    // Metoda getCity() slouží k získání hodnoty atributu city.
    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

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

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {this.email = email;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {this.password = password;}

}
