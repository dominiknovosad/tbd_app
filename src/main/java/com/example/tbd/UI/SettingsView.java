package com.example.tbd.UI;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "customer-settings", layout = CustomerLayout.class)
public class SettingsView extends VerticalLayout {

    public SettingsView() {
        // Content of the settings page
        add(new H1("Settings"));
        add(new Paragraph("This is the settings page content."));
    }
}
