package com.example.tbd.UI;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Route(value = "login", layout = MainLayout.class)
public class LoginView extends VerticalLayout {

    public LoginView() {
        setAlignItems(Alignment.CENTER);

        // Title
        H1 title = new H1("Login");

        // Dynamic text for login mode
        Span loginModeText = new Span("Log in as a Company");

        // Input restrictions for customer (email)
        TextField emailField = new TextField("Email");
        emailField.setPlaceholder("Enter your email");
        emailField.setMaxLength(99);
        emailField.setPattern("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        emailField.setRequired(true);

        PasswordField customerPasswordField = new PasswordField("Password");

        VerticalLayout customerLoginForm = new VerticalLayout();
        customerLoginForm.setAlignItems(Alignment.CENTER);
        customerLoginForm.setVisible(false);
        customerLoginForm.add(emailField, customerPasswordField);

        // Input restrictions for company (ICO)
        TextField icoField = new TextField("IČO");
        icoField.setPlaceholder("Enter your IČO");
        icoField.setMaxLength(10);
        icoField.setPattern("\\d{1,10}");
        icoField.setRequired(true);

        PasswordField companyPasswordField = new PasswordField("Password");

        VerticalLayout companyLoginForm = new VerticalLayout();
        companyLoginForm.setAlignItems(Alignment.CENTER);
        companyLoginForm.setVisible(true);
        companyLoginForm.add(icoField, companyPasswordField);

        // Button to switch between login modes
        Button toggleButton = new Button("Switch to Customer Login");

        // Toggle button behavior
        toggleButton.addClickListener(event -> {
            if (companyLoginForm.isVisible()) {
                companyLoginForm.setVisible(false);
                customerLoginForm.setVisible(true);
                loginModeText.setText("Log in as a Customer");
                toggleButton.setText("Switch to Company Login");
            } else {
                companyLoginForm.setVisible(true);
                customerLoginForm.setVisible(false);
                loginModeText.setText("Log in as a Company");
                toggleButton.setText("Switch to Customer Login");
            }
        });

        // Add components to layout
        add(title, loginModeText, toggleButton, companyLoginForm, customerLoginForm);

        // Login handlers
        Button companyLoginButton = new Button("Login", e -> handleLogin(icoField.getValue(), companyPasswordField.getValue(), true));
        companyLoginForm.add(companyLoginButton);

        Button customerLoginButton = new Button("Login", e -> handleLogin(emailField.getValue(), customerPasswordField.getValue(), false));
        customerLoginForm.add(customerLoginButton);
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
        private String token;
        private Long customerId;
        private Long companyId;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

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
