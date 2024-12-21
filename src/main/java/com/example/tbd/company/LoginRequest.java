package com.example.tbd.company;

public class LoginRequest {

    // Atribút pre IČO firmy, ktoré sa použije pri prihlásení
    private String ico;      // IČO firmy

    // Atribút pre heslo firmy, ktoré sa použije pri prihlásení
    private String password; // Heslo firmy

    // Predvolený konštruktor (používaný napríklad pre deserializáciu JSON)
    public LoginRequest() {
    }

    // Konštruktor, ktorý prijíma IČO a heslo ako argumenty
    public LoginRequest(String ico, String password) {
        this.ico = ico;       // Nastavenie IČO
        this.password = password; // Nastavenie hesla
    }

    // Getter (metóda na získanie hodnoty IČO)
    public String getIco() {
        return ico; // Vráti IČO firmy
    }

    // Setter (metóda na nastavenie hodnoty IČO)
    public void setIco(String ico) {
        this.ico = ico; // Nastaví IČO firmy
    }

    // Getter (metóda na získanie hodnoty hesla)
    public String getPassword() {
        return password; // Vráti heslo firmy
    }

    // Setter (metóda na nastavenie hodnoty hesla)
    public void setPassword(String password) {
        this.password = password; // Nastaví heslo firmy
    }
}
