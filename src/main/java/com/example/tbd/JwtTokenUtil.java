package com.example.tbd;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

// Komponent pre správu JWT tokenov
@Component
public class JwtTokenUtil {
    // Pevný bezpečný tajný kľúč pre podpisovanie tokenov
    private static final String SECRET_KEY = "verysecuresecretkeywith256bits1234567890";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());  // Vytvorenie tajného kľúča na základe SECRET_KEY

    // Metóda na generovanie JWT tokenu pre zákazníka alebo firmu
    public String generateToken(String subject, int companyId, String companyICO) {
        return Jwts.builder()
                .setSubject(subject)  // Nastavenie subjektu tokenu (napr. e-mail alebo IČO firmy)
                .claim("companyId", companyId)  // Pridanie ID firmy ako claim
                .claim("companyICO", companyICO) // Pridanie IČO firmy ako claim
                .setIssuedAt(new Date())  // Nastavenie času vydania tokenu
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Nastavenie platnosti tokenu na 24 hodín (86400000 ms)
                .signWith(key)  // Podpisovanie tokenu pomocou tajného kľúča
                .compact();  // Vytvorenie kompaktného tokenu
    }

    // Metóda na získanie informácií (claims) z tokenu
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)  // Nastavenie kľúča na validáciu podpisu
                .build()
                .parseClaimsJws(token)  // Parsovanie a dešifrovanie tokenu
                .getBody();  // Získanie claims z dešifrovaného tokenu
    }

    // Metóda na validáciu tokenu
    public boolean isTokenValid(String token, String subject) {
        Claims claims = extractClaims(token);  // Získanie claims z tokenu
        // Validácia: Token je platný, ak sa subjekt (napr. e-mail) zhoduje a token ešte neexpiruje
        return claims.getSubject().equals(subject) && claims.getExpiration().after(new Date());
    }
}
