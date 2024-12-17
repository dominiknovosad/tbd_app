package com.example.tbd;

import com.example.tbd.customer.Customer;
import com.example.tbd.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("DEBUG: Načítavam zákazníka pre email: " + email);

        Customer customer = repository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("DEBUG: Zákazník nenájdený pre email: " + email);
                    return new UsernameNotFoundException("Zákazník nenájdený: " + email);
                });

        System.out.println("DEBUG: Načítaný zákazník - email: " + customer.getEmail());
        System.out.println("DEBUG: Načítané heslo: " + customer.getPassword());

        return new User(customer.getEmail(), customer.getPassword(), new ArrayList<>());
    }


}

