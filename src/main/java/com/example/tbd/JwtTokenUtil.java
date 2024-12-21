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

    public String generateToken(String subject, int customerId, String customerEmail) {
        return Jwts.builder()
                .setSubject(subject)
                .claim("customerId", customerId)
                .claim("customerEmail", customerEmail)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Token valid for 24 hours
                .signWith(key)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, String subject) {
        Claims claims = extractClaims(token);
        return claims.getSubject().equals(subject) && claims.getExpiration().after(new Date());
    }
}
