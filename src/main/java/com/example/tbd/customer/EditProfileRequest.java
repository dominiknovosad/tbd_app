package com.example.tbd.customer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class EditProfileRequest {

    @NotBlank(message = "Meno je povinné")
    private String name;

    @NotBlank(message = "Priezvisko je povinné")
    private String surname;

    @NotBlank(message = "Mesto je povinné")
    private String city;

    @NotBlank(message = "Telefón je povinný")
    private String telephone;

    @NotBlank(message = "Dátum narodenia je povinný")
    private String birthdate;

    @NotBlank(message = "E-mail je povinný")
    @Size(min = 5, max = 100, message = "E-mail musí byť medzi 5 a 100 znakmi")
    private String email;

    private String password; // Optional, only if user wants to change password

    // Getters and Setters
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

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
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
