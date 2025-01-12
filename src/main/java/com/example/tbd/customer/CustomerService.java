package com.example.tbd.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository; // Repository na komunikáciu s databázou
    private final CustomerMapper customerMapper; // Mapper na konverziu medzi entitou a DTO
    private final PasswordEncoder passwordEncoder; // PasswordEncoder na šifrovanie hesiel
    // Logger na logovanie informácií
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    public CustomerService(CustomerRepository customerRepository, CustomerMapper customerMapper, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
        this.passwordEncoder = passwordEncoder;
    }

    // Metóda na získanie zákazníka podľa ID
    public CustomerDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toCustomerDTO)
                .orElseThrow(() -> {
                    logger.warn("Zákazník s ID {} neexistuje.", id);
                    return new RuntimeException("Zákazník s ID " + id + " neexistuje.");
                });
    }

    // Metóda na získanie všetkých zákazníkov (vracia zoznam DTO)
    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            logger.info("Žiadni zákazníci neboli nájdení.");
        }
        return customers.stream()
                .map(customerMapper::toCustomerDTO)
                .collect(Collectors.toList());
    }

    // Metóda na vytvorenie nového zákazníka
    public Customer createCustomer(Customer customer) {
        // Šifrovanie hesla pred uložením do databázy
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        // Uloženie zákazníka do databázy
        Customer savedCustomer = customerRepository.save(customer);
        // Logovanie úspešného uloženia
        logger.info("Zákazník bol úspešne vytvorený: {}", savedCustomer);
        // Návrat uloženého zákazníka
        return savedCustomer;
    }

    // Metóda na aktualizáciu profilu zákazníka
    public boolean updateCustomerProfile(Long id, UpdateProfileRequest editProfileRequest) {
        Optional<Customer> customerOptional = customerRepository.findById(id);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();

            // Aktualizácia údajov zákazníka
            customer.setName(editProfileRequest.getName());
            customer.setSurname(editProfileRequest.getSurname());
            customer.setCity(editProfileRequest.getCity());
            customer.setTelephone(editProfileRequest.getTelephone());

            if (editProfileRequest.getBirthdate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                customer.setBirthdate(LocalDate.parse(editProfileRequest.getBirthdate(), formatter));
            }

            customer.setEmail(editProfileRequest.getEmail());

            if (editProfileRequest.getPassword() != null && !editProfileRequest.getPassword().isEmpty()) {
                customer.setPassword(passwordEncoder.encode(editProfileRequest.getPassword()));
            }

            customerRepository.save(customer);
            logger.info("Zákazník bol úspešne aktualizovaný: {}", customer);
            return true;
        }

        logger.warn("Zákazník s ID {} neexistuje. Aktualizácia zlyhala.", id);
        return false;
    }

    // Metódy na počítanie zákazníkov podľa časových období
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
}