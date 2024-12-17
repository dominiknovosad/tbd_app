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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;
    private final CompanyUserDetailsService companyUserDetailsService;

    // Pevný bezpečný tajný kľúč pre podpisovanie tokenov
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor("verysecuresecretkeywith256bits1234567890".getBytes());

    public JwtAuthenticationFilter(CustomUserDetailsService userDetailsService,
                                   CompanyUserDetailsService companyUserDetailsService) {
        this.userDetailsService = userDetailsService;
        this.companyUserDetailsService = companyUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = authHeader.substring(7);
        String username = null;
        String companyICO = null;

        try {
            // Dekódovanie a validácia JWT tokenu
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(jwtToken);

            Claims claims = claimsJws.getBody();
            username = claims.getSubject();
            companyICO = claims.get("companyICO", String.class); // Získanie IČO z tokenu
            Date expiration = claims.getExpiration();

            System.out.println("DEBUG: Token je validný. Username: " + username + ", IČO: " + companyICO);
            System.out.println("DEBUG: Expirácia tokenu: " + expiration);

            if (expiration.before(new Date())) {
                System.out.println("DEBUG: Token expiroval.");
                filterChain.doFilter(request, response);
                return;
            }

            // Autentifikácia pre používateľa (Zákazníka)
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null && companyICO == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                setAuthentication(userDetails, request);
            }

            // Autentifikácia pre firmu cez IČO
            if (companyICO != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails companyDetails = companyUserDetailsService.loadUserByUsername(companyICO);
                setAuthentication(companyDetails, request);
            }

        } catch (Exception e) {
            System.out.println("DEBUG: Chyba pri spracovaní tokenu: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        System.out.println("DEBUG: Autentifikácia úspešne nastavená.");
    }
}



