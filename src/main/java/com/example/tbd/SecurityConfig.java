package com.example.tbd;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration  // Označuje triedu ako konfiguračnú triedu pre Spring, ktorá poskytuje Bean definície
public class SecurityConfig {

    // Autowired služby pre správu používateľov a filtrovanie autentifikácie
    private final CustomUserDetailsService userDetailsService;
    private final CompanyUserDetailsService companyUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Konštruktor na injekciu závislostí (userDetailsService, jwtAuthenticationFilter a companyUserDetailsService)
    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter, CompanyUserDetailsService companyUserDetailsService) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.companyUserDetailsService = companyUserDetailsService;
    }

    // Bean definícia pre konfigurovanie zabezpečenia aplikácie
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // Zakazuje CSRF ochranu
                .cors().and()  // Povolenie CORS pre povolené domény (pomocou WebMvcConfigurer)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(  // Definovanie endpointov, ktoré sú prístupné bez autentifikácie
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/customer/login",
                                "/customer/register",
                                "/company/login",
                                "/company/register",
                                "/company/{id}",
                                "/company/byico/{ico}",
                                "/company/allbyname/{name}",
                                "/company/all",
                                "/vehicle/add",  // Povolený prístup na pridanie vozidla
                                "/vehicle/showall",  // Povolený prístup na zobrazenie všetkých vozidiel
                                "/vehicle/id/{id}",  // Povolený prístup na zobrazenie vozidla podľa ID
                                "/vehicle/vin/{vin}",  // Povolený prístup na zobrazenie vozidla podľa VIN
                                "/vehicle/customerid/{customerId}"  // Povolený prístup na zobrazenie vozidiel podľa ID zákazníka
                        ).permitAll()  // Všetky tieto požiadavky sú povolené bez autentifikácie
                        .anyRequest().authenticated()  // Všetky ostatné požiadavky si vyžadujú autentifikáciu
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Nastavenie na stateless autentifikáciu (bez udržiavania session)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // Pridanie JWT filtra pred autentifikáciu
        return http.build();  // Vytvorenie a vrátenie konfigurovaného objektu
    }

    // Bean definícia pre PasswordEncoder, ktorý je použitý na šifrovanie hesiel
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Používa BCrypt pre šifrovanie hesiel
    }

    // Bean definícia pre AuthenticationManager, ktorý je zodpovedný za autentifikáciu používateľov
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)  // Pre zákazníkov sa používa CustomUserDetailsService
                .passwordEncoder(passwordEncoder());  // Heslá sú šifrované pomocou BCryptPasswordEncoder

        authenticationManagerBuilder
                .userDetailsService(companyUserDetailsService)  // Pre firmy sa používa CompanyUserDetailsService
                .passwordEncoder(passwordEncoder());  // Heslá sú šifrované pomocou BCryptPasswordEncoder

        return authenticationManagerBuilder.build();  // Vytvorenie AuthenticationManager
    }

    // Bean definícia pre povolenie CORS pre frontend aplikáciu
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // Povolenie všetkých endpointov
                        .allowedOrigins("http://localhost:55555/")  // Povolený frontend URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Povolené HTTP metódy
                        .allowedHeaders("*")  // Povolené hlavičky
                        .allowCredentials(true);  // Povolenie prenosu cookies
                System.out.println("DEBUG: CORS configuration applied.");  // Debugovanie CORS konfigurácie
            }
        };
    }
}
