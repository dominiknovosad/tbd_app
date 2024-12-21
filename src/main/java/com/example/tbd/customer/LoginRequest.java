package com.example.tbd.customer;

// Trieda slúžiaca na reprezentáciu prihlasovacích údajov
public class LoginRequest {

    private String username; // Premenná pre používateľské meno (e-mail alebo IČO)
    private String password; // Premenná pre heslo

    // Konštruktory
    // Predvolený konštruktor
    public LoginRequest() {
    }

    // Konštruktor pre inicializáciu prihlasovacích údajov s používateľským menom a heslom
    public LoginRequest(String username, String password) {
        this.username = username;  // Nastavenie používateľského mena
        this.password = password;  // Nastavenie hesla
    }

    // Gettery a Settery
    // Getter pre používateľské meno
    public String getUsername() {
        return username;
    }

    // Setter pre používateľské meno
    public void setUsername(String username) {
        this.username = username; // Nastavenie používateľského mena
    }

    // Getter pre heslo
    public String getPassword() {
        return password;
    }

    // Setter pre heslo
    public void setPassword(String password) {
        this.password = password; // Nastavenie hesla
    }
}
