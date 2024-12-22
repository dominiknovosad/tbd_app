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

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CompanyUserDetailsService companyUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter, CompanyUserDetailsService companyUserDetailsService) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.companyUserDetailsService = companyUserDetailsService;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // Zakáže CSRF ochranu
                .cors().and()  // Povolenie CORS
                .authorizeRequests()
                .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/customer/login",
                        "/customer/register",
                        "/customer/editprofile/**",
                        "/customer/editprofile/",
                        "/company/login",
                        "/company/register",
                        "/company/{id}",
                        "/vehicle/add",
                        "/vehicle/showall",
                        "/vehicle/id/{id}",
                        "/vehicle/vin/{vin}",
                        "/vehicle/customerid/{customerId}"
                ).permitAll()  // Povolený prístup bez autentifikácie
                .anyRequest().authenticated()  // Ostatné požiadavky vyžadujú autentifikáciu
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // Stateless pre autentifikáciu
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // Pridanie JWT filtra

        return http.build();  // Vytvorenie a vrátenie filtra
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        authenticationManagerBuilder
                .userDetailsService(companyUserDetailsService)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // Povolenie všetkých endpointov
                        .allowedOrigins("http://localhost:37017/","http://localhost:8080/")  // Povolenie všetkých portov na localhost
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Povolené HTTP metódy
                        .allowedHeaders("Authorization", "Content-Type", "Accept")  // Povolené hlavičky ako Authorization
                        .allowCredentials(true);  // Povolenie prenosu cookies
                System.out.println("DEBUG: CORS configuration applied.");  // Debugovanie CORS konfigurácie
            }
        };
    }
}
