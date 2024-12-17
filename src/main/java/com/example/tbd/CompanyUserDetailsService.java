package com.example.tbd;

import com.example.tbd.company.Company;
import com.example.tbd.company.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CompanyUserDetailsService implements UserDetailsService {

    @Autowired
    private CompanyRepository repository;

    @Override
    public UserDetails loadUserByUsername(String ico) throws UsernameNotFoundException {
        System.out.println("DEBUG: Načítavam firmu pre IČO: " + ico);

        // Validácia: Skontrolujeme, či je IČO číslo
        if (!ico.matches("\\d+")) {
            throw new UsernameNotFoundException("IČO musí obsahovať iba číslice: " + ico);
        }

        Company company = repository.findByIco(Integer.parseInt(ico))
                .orElseThrow(() -> {
                    System.out.println("DEBUG: Firma nenájdená pre IČO: " + ico);
                    return new UsernameNotFoundException("Firma nenájdená: " + ico);
                });

        System.out.println("DEBUG: Načítaná Firma - IČO: " + company.getIco());
        System.out.println("DEBUG: Načítané heslo: " + company.getPassword());

        // Vytvorenie UserDetails pre autentifikáciu
        return new User(String.valueOf(company.getIco()), company.getPassword(), new ArrayList<>());
    }
}

