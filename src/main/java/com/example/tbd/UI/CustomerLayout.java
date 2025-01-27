package com.example.tbd.UI;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;

public class CustomerLayout extends VerticalLayout implements RouterLayout {

    public CustomerLayout() {
        // Create a navbar
        HorizontalLayout navbar = new HorizontalLayout();
        navbar.setSpacing(true);
        navbar.setPadding(true);
        navbar.setWidthFull();

        // Add links to the navbar
        Anchor profileLink = new Anchor("customer-profile", "Profile");
        Anchor vehicleLink = new Anchor("customer-vehicle", "Vehicle");
        Anchor settingsLink = new Anchor("customer-settings", "Settings");

        // Add the links to the navbar
        navbar.add(profileLink, vehicleLink, settingsLink);

        // Add the navbar to the layout
        add(navbar);
    }
}
