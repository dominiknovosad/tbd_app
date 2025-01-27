package com.example.tbd.UI;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {
        // Create a header with a title
        H1 title = new H1("TBD APP");
        title.getStyle().set("margin", "0");

        // Add navigation links
        RouterLink homeLink = new RouterLink("Main", MainView.class);
        RouterLink aboutLink = new RouterLink("About", MainView.class); // Placeholder for another view
        RouterLink loginLink = new RouterLink("Login", LoginView.class);

        // Logout link (example)
        Anchor logoutLink = new Anchor("/logout", "Logout");

        // Header layout
        HorizontalLayout header = new HorizontalLayout(title, homeLink, aboutLink, loginLink, logoutLink);
        header.setWidthFull();
        header.setPadding(true);
        header.setSpacing(true);
        header.setAlignItems(Alignment.CENTER);

        // Add the header to the AppLayout
        addToNavbar(header);
    }
}
