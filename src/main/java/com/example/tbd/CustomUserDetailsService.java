package com.example.tbd;

import com.example.tbd.customer.Customer;  // Import triedy Customer, ktorá reprezentuje zákazníka v databáze
import com.example.tbd.customer.CustomerRepository;  // Import repository pre prístup k údajom o zákazníkoch
import org.springframework.beans.factory.annotation.Autowired;  // Import pre automatickú injekciu závislostí
import org.springframework.security.core.userdetails.User;  // Import pre použitie triedy User v kontexte autentifikácie
import org.springframework.security.core.userdetails.UserDetails;  // Import pre UserDetails rozhranie
import org.springframework.security.core.userdetails.UserDetailsService;  // Import rozhrania pre načítavanie údajov o používateľovi
import org.springframework.security.core.userdetails.UsernameNotFoundException;  // Import výnimky pre neexistujúceho používateľa
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;  // Import pre označenie triedy ako služba

import java.util.ArrayList;  // Import pre použitie zoznamu (v tomto prípade prázdneho)

@Service  // Označenie triedy ako Spring služba
public class CustomUserDetailsService implements UserDetailsService {  // Implementácia UserDetailsService pre zákazníka

    @Autowired
    private CustomerRepository repository;  // Automatická injekcia repository pre prístup k databáze zákazníkov

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  // Implementácia metódy loadUserByUsername
        System.out.println("DEBUG: Načítavam zákazníka pre email/ičo: " + username);  // Debug výpis pre kontrolu, aký username sa načítava

        // Validácia: Kontrola prázdneho alebo null username
        if (username == null || username.trim().isEmpty()) {  // Kontrola, či je username prázdny alebo null
            System.out.println("DEBUG: Prázdny alebo null email zadaný!");  // Debug výpis v prípade neplatného username
            throw new UsernameNotFoundException("E-mail/IČO nemôže byť prázdny alebo null!");  // Vyhodenie výnimky pre neplatný email/IČO
        }

        // Hľadanie zákazníka v databáze na základe emailu (alebo IČO)
        Customer customer = repository.findByEmail(username)  // Vyhľadanie zákazníka podľa emailu
                .orElseThrow(() -> {  // Ak zákazník neexistuje, vyhodí výnimku
                    System.out.println("DEBUG: Zákazník nenájdený pre e-mail: " + username);  // Debug výpis v prípade, že zákazník neexistuje
                    return new UsernameNotFoundException("Zákazník nenájdený: " + username);  // Vyhodenie výnimky
                });

        System.out.println("DEBUG: Načítaný zákazník - email: " + customer.getEmail());  // Debug výpis pre načítaného zákazníka
        System.out.println("DEBUG: Načítané heslo: " + customer.getPassword());  // Debug výpis pre načítané heslo zákazníka

        // Vytvorenie a vrátenie objektu User pre autentifikáciu
        return new User(customer.getEmail(), customer.getPassword(), new ArrayList<>());  // Vytvorenie objektu User, ktorý sa používa pri autentifikácii
    }
}
