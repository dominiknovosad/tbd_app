package com.example.tbd.UI;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import org.springframework.web.client.RestTemplate;

@Route(value = "customer-profile/:id", layout = MainLayout.class)
public class CustomerProfileView extends VerticalLayout implements BeforeEnterObserver {

    private final H1 title = new H1("Customer Profile");

    private final Paragraph nameParagraph = new Paragraph();
    private final Paragraph surnameParagraph = new Paragraph();
    private final Paragraph cityParagraph = new Paragraph();
    private final Paragraph telephoneParagraph = new Paragraph();
    private final Paragraph emailParagraph = new Paragraph();
    private final Paragraph birthdateParagraph = new Paragraph();
    private final Paragraph roleParagraph = new Paragraph();

    public CustomerProfileView() {
        setAlignItems(Alignment.CENTER);

        title.getStyle().set("text-align", "center");

        add(title, nameParagraph, surnameParagraph, cityParagraph, telephoneParagraph, emailParagraph, birthdateParagraph, roleParagraph);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String customerId = event.getRouteParameters().get("id").orElse(null);
        if (customerId != null) {
            loadCustomerData(customerId);
        } else {
            title.setText("Customer not found");
        }
    }

    private void loadCustomerData(String customerId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/customer/" + customerId;

        try {
            CustomerDTO customer = restTemplate.getForObject(url, CustomerDTO.class);

            if (customer != null) {
                nameParagraph.setText("Name: " + customer.getName());
                surnameParagraph.setText("Surname: " + customer.getSurname());
                cityParagraph.setText("City: " + customer.getCity());
                telephoneParagraph.setText("Telephone: " + customer.getTelephone());
                emailParagraph.setText("Email: " + customer.getEmail());
                birthdateParagraph.setText("Birthdate: " + (customer.getBirthdate() != null ? customer.getBirthdate() : "N/A"));

                String role;
                switch (customer.getRoleId()) {
                    case 1:
                        role = "User";
                        break;
                    case 5:
                        role = "Pro User";
                        break;
                    case 10:
                        role = "Moderator";
                        break;
                    case 15:
                        role = "Manager";
                        break;
                    case 99:
                        role = "Administrator";
                        break;
                    default:
                        role = "Unknown Role";
                }
                roleParagraph.setText("Role: " + role);
            } else {
                title.setText("Customer not found");
            }
        } catch (Exception e) {
            title.setText("Error fetching customer data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static class CustomerDTO {
        private Long id;
        private String name;
        private String surname;
        private String city;
        private String telephone;
        private String email;
        private String birthdate;
        private Integer roleId;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(String birthdate) {
            this.birthdate = birthdate;
        }

        public Integer getRoleId() {
            return roleId;
        }

        public void setRoleId(Integer roleId) {
            this.roleId = roleId;
        }
    }
}
