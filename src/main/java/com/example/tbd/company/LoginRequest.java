package com.example.tbd.company;

public class LoginRequest {
    private String ico;      // Nový atribút pre IČO
    private String password; // Heslo firmy

    // Konštruktory
    public LoginRequest() {
    }

    public LoginRequest(String ico, String password) {
        this.ico = ico;
        this.password = password;
    }

    // Getter a Setter pre IČO
    public String getIco() {
        return ico;
    }

    public void setIco(String ico) {
        this.ico = ico;
    }

    // Getter a Setter pre heslo
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
