package com.example.tbd;

import com.example.tbd.customer.Customer;  // Import triedy Customer, ktorá reprezentuje zákazníka v databáze
import com.example.tbd.customer.CustomerRepository;  // Import repository pre prístup k údajom o zákazníkoch
import org.springframework.beans.factory.annotation.Autowired;  // Import pre automatickú injekciu závislostí
import org.springframework.security.core.userdetails.User;  // Import pre použitie triedy User v kontexte autentifikácie
import org.springframework.security.core.userdetails.UserDetails;  // Import pre UserDetails rozhranie
import org.springframework.security.core.userdetails.UserDetailsService;  // Import rozhrania pre načítavanie údajov o používateľovi
import org.springframework.security.core.userdetails.UsernameNotFoundException;  // Import výnimky pre neexistujúceho používateľa
import org.springframework.stereotype.Service;  // Import pre označenie triedy ako služba

import java.util.ArrayList;  // Import pre použitie zoznamu (v tomto prípade prázdneho)

@Service  // Označenie triedy ako Spring služba
public class CustomUserDetailsService implements UserDetailsService {  // Implementácia UserDetailsService pre zákazníka

    @Autowired
    private CustomerRepository repository;  // Automatická injekcia repository pre prístup k databáze zákazníkov

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {  // Implementácia metódy loadUserByUsername
        System.out.println("DEBUG: Načítavam zákazníka pre email: " + email);  // Debug výpis pre kontrolu, aký email sa načítava

        // Validácia: Kontrola prázdneho alebo null emailu
        if (email == null || email.trim().isEmpty()) {  // Kontrola, či je email prázdny alebo null
            System.out.println("DEBUG: Prázdny alebo null email zadaný!");  // Debug výpis v prípade neplatného emailu
            throw new UsernameNotFoundException("E-mail nemôže byť prázdny alebo null!");  // Vyhodenie výnimky pre neplatný email
        }

        // Hľadanie zákazníka v databáze na základe emailu
        Customer customer = repository.findByEmail(email)  // Vyhľadanie zákazníka podľa emailu
                .orElseThrow(() -> {  // Ak zákazník neexistuje, vyhodí výnimku
                    System.out.println("DEBUG: Zákazník nenájdený pre email: " + email);  // Debug výpis v prípade, že zákazník neexistuje
                    return new UsernameNotFoundException("Zákazník nenájdený: " + email);  // Vyhodenie výnimky
                });

        System.out.println("DEBUG: Načítaný zákazník - email: " + customer.getEmail());  // Debug výpis pre načítaného zákazníka
        System.out.println("DEBUG: Načítané heslo: " + customer.getPassword());  // Debug výpis pre načítané heslo zákazníka

        // Vytvorenie a vrátenie objektu User pre autentifikáciu
        return new User(customer.getEmail(), customer.getPassword(), new ArrayList<>());  // Vytvorenie objektu User, ktorý sa používa pri autentifikácii
    }
}
