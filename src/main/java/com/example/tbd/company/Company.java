package com.example.tbd.company;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(name = "company") // Názov tabuľky v databáze
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "company_name")
    @NotNull(message = "Názov spoločnosti nesmie byť prázdny")
    private String companyName;

    @Column(name = "ico")
    @NotNull(message = "IČO nesmie byť prázdne")
    private Integer ico;

    @Column(name = "email")
    @NotNull(message = "Email nesmie byť prázdny")
    private String email;

    @Column(name = "telephone")
    @NotNull(message = "Telefónne číslo nesmie byť prázdne")
    @Digits(fraction = 0, integer = 10, message = "Telefón musí mať maximálne 10 číslic")
    private String telephone;

    @Column(name = "address")
    @NotNull(message = "Adresa nesmie byť prázdna")
    private String address;

    @Column(name = "password")
    @NotNull(message = "Heslo nesmie byť prázdne")
    private String password;

    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date createdAt;

    // Gettery a settery
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Integer getIco() {
        return ico;
    }

    public void setIco(Integer ico) {
        this.ico = ico;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

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
                '}';
    }
}
