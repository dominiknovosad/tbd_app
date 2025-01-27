package com.example.tbd.UI;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "customer-vehicle", layout = CustomerLayout.class)
public class VehicleView extends VerticalLayout {

    public VehicleView() {
        // Content of the vehicle page
        add(new H1("Vehicle Management"));
        add(new Paragraph("This is the vehicle page content."));
    }
}