package com.example.tbd;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
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
                .csrf(AbstractHttpConfigurer::disable) // Zakáže CSRF ochranu
                .cors(AbstractHttpConfigurer::disable) // Povolenie alebo zakázanie CORS (záleží na vašom prípade)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/customer/login",
                                "/customer/register",
                                "/customer/editprofile/**",
                                "/customer/all",
                                "/customer/{id}",
                                "/customer/count",
                                "/company/login",
                                "/company/**",
                                "/company/register",
                                "/vehicle/add",
                                "/vehicle/showall",
                                "/vehicle/id/{id}",
                                "/vehicle/vin/{vin}",
                                "/vehicle/customerid/{customerId}",
                                "/vehicle/update",
                                "/vehicle/count",
                                "/product/add",
                                "/product/count",
                                "/product/companyid/{companyId}",
                                "/product/delupdate/{id}",
                                "/product/showall",
                                "/loginview/**",
                                "/VAADIN/**",
                                "/frontend/**",
                                "/frontend-es5/**",
                                "/frontend-es6/**",
                                "/resources/**",
                                "/webjars/**",
                                "/offline-stub.html",
                                "/offline.html",
                                "/favicon.ico",
                                "/ui/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                );

        return http.build();
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
                        .allowedOrigins("http://localhost:55555/","http://localhost:8080/")  // Povolenie všetkých portov na localhost
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Povolené HTTP metódy
                        .allowedHeaders("Authorization", "Content-Type", "Accept")  // Povolené hlavičky ako Authorization
                        .allowCredentials(true);  // Povolenie prenosu cookies
                System.out.println("DEBUG: CORS configuration applied.");  // Debugovanie CORS konfigurácie
            }
        };
    }
}
