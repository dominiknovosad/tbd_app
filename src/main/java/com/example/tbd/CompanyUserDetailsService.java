package com.example.tbd;

import com.example.tbd.company.Company;  // Import triedy Company, ktorá reprezentuje firmu v databáze
import com.example.tbd.company.CompanyRepository;  // Import repository pre prístup k údajom o firmách
import org.springframework.beans.factory.annotation.Autowired;  // Import pre automatickú injekciu závislostí
import org.springframework.security.core.userdetails.User;  // Import pre použitie triedy User v kontexte autentifikácie
import org.springframework.security.core.userdetails.UserDetails;  // Import pre UserDetails rozhranie
import org.springframework.security.core.userdetails.UserDetailsService;  // Import rozhrania pre načítavanie údajov o používateľovi
import org.springframework.security.core.userdetails.UsernameNotFoundException;  // Import výnimky pre neexistujúceho používateľa
import org.springframework.stereotype.Service;  // Import pre označenie triedy ako služba

import java.util.ArrayList;  // Import pre použitie zoznamu (v tomto prípade prázdneho)

@Service  // Označenie triedy ako Spring služba
public class CompanyUserDetailsService implements UserDetailsService {  // Implementácia UserDetailsService pre firmu

    @Autowired
    private CompanyRepository repository;  // Automatická injekcia repository pre prístup k databáze firiem

    @Override
    public UserDetails loadUserByUsername(String ico) throws UsernameNotFoundException {  // Implementácia metódy loadUserByUsername
      //  System.out.println("DEBUG: Načítavam firmu pre IČO: " + ico);  // Debug výpis pre kontrolu, aké IČO sa načítava

        // Validácia: Kontrola prázdneho alebo null IČO
        if (ico == null || ico.trim().isEmpty() || !ico.matches("\\d+")) {  // Kontrola, či je IČO neplatné
           // System.out.println("DEBUG: IČO je prázdne, null alebo obsahuje neplatné znaky!");  // Debug výpis v prípade neplatného IČO
            throw new UsernameNotFoundException("IČO nemôže byť prázdne, null alebo neplatné: " + ico);  // Vyhodenie výnimky pre neplatné IČO
        }

        // Hľadanie firmy v databáze na základe IČO
        Company company = repository.findByIco(Integer.parseInt(ico))  // Vyhľadanie firmy podľa IČO
                .orElseThrow(() -> {  // Ak firma neexistuje, vyhodí výnimku
                    //System.out.println("DEBUG: Firma nenájdená pre IČO: " + ico);  // Debug výpis v prípade, že firma neexistuje
                    return new UsernameNotFoundException("Firma nenájdená: " + ico);  // Vyhodenie výnimky
                });

        //System.out.println("DEBUG: Načítaná Firma - IČO: " + company.getIco());  // Debug výpis pre načítanú firmu
        //System.out.println("DEBUG: Načítané heslo: " + company.getPassword());  // Debug výpis pre načítané heslo firmy

        // Vytvorenie a vrátenie objektu User pre autentifikáciu
        return new User(String.valueOf(company.getIco()), company.getPassword(), new ArrayList<>());  // Vytvorenie objektu User, ktorý sa používa pri autentifikácii
    }
}
