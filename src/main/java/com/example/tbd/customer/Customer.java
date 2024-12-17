package com.example.tbd.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.style.ToStringCreator;

import java.util.Date;

@Entity
@Table(name = "customer") // Názov tabuľky v databáze
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "surname")
    @NotNull
    private String surname;

    @Column(name = "city")
    @NotNull
    private String city;

    @Column(name = "telephone")
    @NotNull
    @Digits(fraction = 0, integer = 10)
    private String telephone;

    @Column(name = "birthdate")
    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy") // Správny formát dátumu
    private Date birthdate;

    @Column(name = "email")
    @NotNull
    private String email;

    @Column(name = "password")
    @NotNull
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

    // Gettery a Settery
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
