package com.example.tbd;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatter {

    public static void main(String[] args) {
        String inputDate = "15.06.2024"; // Vstup v tvare DD.MM.RRRR

        // Definovanie formátu vstupného a výstupného dátumu
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // Prevod vstupného dátumu na LocalDate a následné formátovanie
        LocalDate date = LocalDate.parse(inputDate, inputFormatter);
        String formattedDate = date.format(outputFormatter);

        // Výpis správne naformátovaného dátumu
        System.out.println("Naformátovaný dátum: " + formattedDate);
    }
}