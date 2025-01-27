package com.example.tbd.UI;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Route(value = "company-profile/:id", layout = MainLayout.class)
public class CompanyProfileView extends VerticalLayout implements BeforeEnterObserver {

    private final H1 title = new H1("Company Profile");

    private final Paragraph companyNameParagraph = new Paragraph();
    private final Paragraph icoParagraph = new Paragraph();
    private final Paragraph emailParagraph = new Paragraph();
    private final Paragraph telephoneParagraph = new Paragraph();
    private final Paragraph addressParagraph = new Paragraph();

    public CompanyProfileView() {
        setAlignItems(Alignment.CENTER);

        title.getStyle().set("text-align", "center");

        add(title, companyNameParagraph, icoParagraph, emailParagraph, telephoneParagraph, addressParagraph);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        String companyId = event.getRouteParameters().get("id").orElse(null);

        if (companyId == null || companyId.equals("null") || companyId.isEmpty()) {
            title.setText("Company not found");
            Notification.show("Invalid or missing company ID", 3000, Notification.Position.MIDDLE);
        } else {
            loadCompanyData(companyId);
        }
    }

    private void loadCompanyData(String companyId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/company/" + companyId;

        try {
            CompanyDTO company = restTemplate.getForObject(url, CompanyDTO.class);

            if (company != null) {
                companyNameParagraph.setText("Company Name: " + company.getCompanyName());
                icoParagraph.setText("ICO: " + company.getIco());
                emailParagraph.setText("Email: " + company.getEmail());
                telephoneParagraph.setText("Telephone: " + company.getTelephone());
                addressParagraph.setText("Address: " + company.getAddress());
            } else {
                title.setText("Company not found");
                Notification.show("Company data not found", 3000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            title.setText("Error fetching company data");
            Notification.show("Error fetching company data: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            e.printStackTrace();
        }
    }

    private void handleLogin(String username, String password, boolean isCompany) {
        RestTemplate restTemplate = new RestTemplate();
        String endpoint = isCompany ? "/company/login" : "/customer/login";
        String url = "http://localhost:8080" + endpoint;

        try {
            ResponseEntity<LoginResponse> response = restTemplate.postForEntity(url, new LoginRequest(username, password), LoginResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                LoginResponse loginResponse = response.getBody();
                Long id = isCompany ? loginResponse.getCompanyId() : loginResponse.getCustomerId();

                if (id != null) {
                    if (isCompany) {
                        getUI().ifPresent(ui -> ui.access(() -> ui.navigate(CompanyProfileView.class, new RouteParameters("id", String.valueOf(id)))));
                    } else {
                        getUI().ifPresent(ui -> ui.access(() -> ui.navigate(CustomerProfileView.class, new RouteParameters("id", String.valueOf(id)))));
                    }
                } else {
                    Notification.show("Login successful but no valid ID was returned.", 3000, Notification.Position.MIDDLE);
                }
            } else {
                Notification.show("Invalid credentials. Please try again.", 3000, Notification.Position.MIDDLE);
            }
        } catch (Exception e) {
            Notification.show("Error during login: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            e.printStackTrace();
        }
    }


    public static class CompanyDTO {
        private Integer id;
        private String companyName;
        private Integer ico;
        private String email;
        private String telephone;
        private String address;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public Integer getIco() {
            return ico;
        }

        public void setIco(Integer ico) {
            this.ico = ico;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class LoginResponse {
        private Long customerId;
        private Long companyId;

        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }

        public Long getCompanyId() {
            return companyId;
        }

        public void setCompanyId(Long companyId) {
            this.companyId = companyId;
        }
    }
}
