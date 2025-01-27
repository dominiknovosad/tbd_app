package com.example.tbd.UI;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = com.example.tbd.UI.MainLayout.class)
public class MainView extends VerticalLayout {

    public MainView() {
        Div welcomeMessage = new Div();
        welcomeMessage.setText("Welcome to the Vaadin application!");
        add(welcomeMessage);
    }
}
