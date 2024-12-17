package com.example.tbd;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenUtil {
    private static final String SECRET_KEY = "verysecuresecretkeywith256bits1234567890";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // Metóda na generovanie JWT tokenu pre zákazníka alebo firmu
    public String generateToken(String subject, int companyId, String companyICO) {
        return Jwts.builder()
                .setSubject(subject)  // IČO firmy
                .claim("companyId", companyId)  // ID firmy
                .claim("companyICO", companyICO) // IČO firmy ako claim
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Token platný 24 hodín
                .signWith(key)
                .compact();
    }

    // Metóda na získanie informácií z tokenu
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Metóda na validáciu tokenu
    public boolean isTokenValid(String token, String subject) {
        Claims claims = extractClaims(token);
        return claims.getSubject().equals(subject) && claims.getExpiration().after(new Date());
    }
}
