package com.example.tbd.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class LoginRequest {

    @Schema(description = "Používateľské meno firmy (IČO) na prihlásenie", example = "12345678")
    @JsonProperty("username") // Príjme JSON kľúč "username" a mapuje ho na atribút "ico"
    private String ico;

    @Schema(description = "Heslo firmy na prihlásenie", example = "heslo123")
    private String password;

    // Predvolený konštruktor
    public LoginRequest() {
    }

    // Konštruktor s parametrami
    public LoginRequest(String ico, String password) {
        this.ico = ico;
        this.password = password;
    }

    // Getter pre IČO
    public String getIco() {
        return ico;
    }

    // Setter pre IČO
    public void setIco(String ico) {
        this.ico = ico;
    }

    // Getter pre heslo
    public String getPassword() {
        return password;
    }

    // Setter pre heslo
    public void setPassword(String password) {
        this.password = password;
    }
}
