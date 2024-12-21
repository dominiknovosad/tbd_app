package com.example.tbd;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.Key;
import java.util.Date;

// Komponent pre JWT autentifikáciu
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Služby pre získanie údajov o používateľovi (Zákazník alebo Firma)
    private final CustomUserDetailsService userDetailsService;
    private final CompanyUserDetailsService companyUserDetailsService;

    // Pevný bezpečný tajný kľúč pre podpisovanie tokenov
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor("verysecuresecretkeywith256bits1234567890".getBytes());

    // Konštruktor na injekciu závislostí (CustomUserDetailsService a CompanyUserDetailsService)
    public JwtAuthenticationFilter(CustomUserDetailsService userDetailsService,
                                   CompanyUserDetailsService companyUserDetailsService) {
        this.userDetailsService = userDetailsService;
        this.companyUserDetailsService = companyUserDetailsService;
    }

    // Metóda na filtrovanie požiadaviek
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Získanie hlavičky Authorization z požiadavky
        final String authHeader = request.getHeader("Authorization");

        // Ak hlavička neexistuje alebo nezačína "Bearer ", preskočíme spracovanie
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrahovanie tokenu z hlavičky Authorization
        final String jwtToken = authHeader.substring(7);
        String username = null;
        String companyICO = null;

        try {
            // Dekódovanie a validácia JWT tokenu
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)  // Použitie tajného kľúča na validáciu tokenu
                    .build()
                    .parseClaimsJws(jwtToken);  // Parsovanie tokenu

            Claims claims = claimsJws.getBody();  // Získanie údajov (claims) z tokenu
            username = claims.getSubject();  // Získanie používateľského mena (e-mail)
            companyICO = claims.get("companyICO", String.class); // Získanie IČO firmy z tokenu
            Date expiration = claims.getExpiration();  // Získanie dátumu exspirácie tokenu

            // Debug výpisy pre kontrolu platnosti tokenu
            System.out.println("DEBUG: Token je validný. Username: " + username + ", IČO: " + companyICO);
            System.out.println("DEBUG: Expirácia tokenu: " + expiration);

            // Kontrola, či token ešte neexpiruje
            if (expiration.before(new Date())) {
                System.out.println("DEBUG: Token expiroval.");
                filterChain.doFilter(request, response);
                return;
            }

            // Autentifikácia pre zákazníka, ak je prítomný len username a nie IČO
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null && companyICO == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);  // Načítanie údajov o zákazníkovi
                setAuthentication(userDetails, request);  // Nastavenie autentifikácie pre zákazníka
            }

            // Autentifikácia pre firmu cez IČO
            if (companyICO != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails companyDetails = companyUserDetailsService.loadUserByUsername(companyICO);  // Načítanie údajov o firme
                setAuthentication(companyDetails, request);  // Nastavenie autentifikácie pre firmu
            }

        } catch (Exception e) {
            System.out.println("DEBUG: Chyba pri spracovaní tokenu: " + e.getMessage());  // Chyba pri validácii tokenu
        }

        filterChain.doFilter(request, response);  // Pokračovanie v spracovaní požiadavky
    }

    // Metóda na nastavenie autentifikácie v kontexte
    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        // Vytvorenie autentifikačného tokenu
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // Nastavenie podrobností autentifikácie zo zdroja požiadavky
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);  // Nastavenie autentifikácie v kontexte
        System.out.println("DEBUG: Autentifikácia úspešne nastavená.");  // Debug výpis
    }
}
