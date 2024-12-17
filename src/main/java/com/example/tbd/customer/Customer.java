package com.example.tbd.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.core.style.ToStringCreator;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "customer") // Názov tabuľky v databáze
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    @NotNull
    private String name;

    @Column(name = "surname", nullable = false)
    @NotNull
    private String surname;

    @Column(name = "city", nullable = false)
    @NotNull
    private String city;

    @Column(name = "telephone", nullable = false)
    @NotNull
    @Digits(fraction = 0, integer = 10)
    private String telephone;

    @Column(name = "birthdate", nullable = false)
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date birthdate;

    @Column(name = "email", nullable = false)
    @NotNull
    private String email;

    @Column(name = "password", nullable = false)
    @NotNull
    private String password;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

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
                .append("created_at", this.getCreatedAt())
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
