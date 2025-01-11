package com.example.tbd.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Import pre šifrovanie hesiel
import org.springframework.stereotype.Service; // Import pre označenie triedy ako Spring Products
import org.slf4j.Logger; // Import pre logovanie
import org.slf4j.LoggerFactory; // Import pre vytvorenie loggeru

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List; // Import pre zoznam zákazníkov
import java.time.LocalDateTime;

@Service // Označuje túto triedu ako Spring Products, ktorá spravuje logiku pre zákazníkov
public class CustomerService {

    private final CustomerRepository customerRepository; // Repository na komunikáciu s databázou
    @Autowired
    private PasswordEncoder passwordEncoder; // PasswordEncoder na šifrovanie hesiel

    // Pridanie Logger pre logovanie informácií, varovaní alebo chýb
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    public long countUsersLast24Hours() {
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        return customerRepository.countUsersFrom(last24Hours);
    }
    public long countUsersLast7Days() {
        LocalDateTime last7Days = LocalDateTime.now().minusDays(7);
        return customerRepository.countUsersFrom(last7Days);
    }
    public long countUsersLast30Days() {
        LocalDateTime last30Days = LocalDateTime.now().minusDays(30);
        return customerRepository.countUsersFrom(last30Days);
    }
    public long countUsersLast365Days() {
        LocalDateTime last365Days = LocalDateTime.now().minusDays(365);
        return customerRepository.countUsersFrom(last365Days);
    }
    public long countCustomersWithRoleUser() {
        return customerRepository.countUsersWithRoleUser();
    }
    // Konštruktor na injekciu závislostí
    // Tento konštruktor je automaticky volaný Springom na injekciu potrebných závislostí
    public CustomerService(CustomerRepository repository, PasswordEncoder passwordEncoder) {
        this.customerRepository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    // Metóda na získanie zákazníka podľa ID
    public Customer getCustomerById(Integer id) {
        // Snaží sa nájsť zákazníka podľa ID v databáze, ak ho nenájde, vráti null
        return customerRepository.findById(id).orElse(null);
    }

    // Metóda na získanie všetkých zákazníkov z databázy
    public List<Customer> getAll() {
        // Vráti všetkých zákazníkov zo zoznamu
        return customerRepository.findAll();
    }

    // Metóda na získanie všetkých zákazníkov podľa mena
    public List<Customer> findByAllName(String name) {
        // Snaží sa nájsť zákazníkov, ktorí majú zadané meno
        return customerRepository.findAllByName(name);
    }

    // Metóda na získanie všetkých zákazníkov podľa priezviska
    public List<Customer> findByAllSurname(String surname) {
        // Snaží sa nájsť zákazníkov, ktorí majú zadané priezvisko
        return customerRepository.findAllBySurname(surname);
    }

    // Metóda na vytvorenie nového zákazníka
    // Vytvorí nového zákazníka, zašifruje jeho heslo a uloží ho do databázy
    public Customer createCustomer(Customer customer) {
        // Šifrovanie hesla pred uložením do databázy
        String encodedPassword = passwordEncoder.encode(customer.getPassword());
        customer.setPassword(encodedPassword); // Nastaví zašifrované heslo do objektu zákazníka
        // Uloží zákazníka do databázy a vráti uloženého zákazníka
        Customer savedCustomer = customerRepository.save(customer);
        // Logovanie úspešnej registrácie zákazníka
        logger.info("Zákazník vytvorený: ID: {}, Name: {}, Surname: {}, Email: {}, role_id: {}", savedCustomer.getId(), savedCustomer.getName(), savedCustomer.getSurname(), savedCustomer.getEmail(), savedCustomer.getRoleId());
        return savedCustomer; // Vráti zákazníka s novými dátami
    }

    public boolean updateCustomerProfile(Integer id, UpdateProfileRequest editProfileRequest) {
        Customer customer = customerRepository.findById(id).orElse(null);

        if (customer != null) {
            // Update customer fields
            customer.setName(editProfileRequest.getName());
            customer.setSurname(editProfileRequest.getSurname());
            customer.setCity(editProfileRequest.getCity());
            customer.setTelephone(editProfileRequest.getTelephone());

            // Convert birthdate from String to LocalDate if not null
            if (editProfileRequest.getBirthdate() != null) {
                // Define the formatter to match 'dd.MM.yyyy' format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                LocalDate birthdate = LocalDate.parse(editProfileRequest.getBirthdate(), formatter);
                customer.setBirthdate(birthdate); // Set the converted birthdate
            }
            customer.setEmail(editProfileRequest.getEmail());
            // If password is provided, encode it and update it
            if (editProfileRequest.getPassword() != null && !editProfileRequest.getPassword().isEmpty()) {
                customer.setPassword(passwordEncoder.encode(editProfileRequest.getPassword()));
            }
            // Uloženie aktualizovaných údajov
            customerRepository.save(customer);
            // Logovanie po zmene
            logger.info("Nové údaje zákazníka po aktualizácii: {}", customer);
            return true;
        }
        return false;
    }


}
