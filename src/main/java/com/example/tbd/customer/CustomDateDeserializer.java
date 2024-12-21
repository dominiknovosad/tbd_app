package com.example.tbd.customer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Trieda na deserializáciu dátumu z JSON formátu na Java Date objekt
public class CustomDateDeserializer extends JsonDeserializer<Date> {

    // Definícia primárneho formátu dátumu - očakávaný formát je 'dd.MM.yyyy'
    private static final SimpleDateFormat primaryFormatter = new SimpleDateFormat("dd.MM.yyyy");

    // Definícia náhradného formátu dátumu - očakávaný formát je 'EEE MMM dd HH:mm:ss zzz yyyy' (napr. "Mon Oct 10 00:00:00 CET 1994")
    private static final SimpleDateFormat fallbackFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

    // Override metódy deserializácie na konverziu JSON dátumu do Java Date objektu
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        // Získanie reťazca s dátumom z JSON parsera
        String dateStr = jsonParser.getText();

        try {
            // Pokus o analýzu dátumu pomocou primárneho formátu
            return primaryFormatter.parse(dateStr);
        } catch (ParseException e) {
            try {
                // Ak primárny formát zlyhá, pokúsi sa o analýzu s náhradným formátom
                return fallbackFormatter.parse(dateStr);
            } catch (ParseException ex) {
                // Ak ani náhradný formát nefunguje, vyvolá výnimku s popisom chyby
                throw new RuntimeException("Invalid date format. Expected 'dd.MM.yyyy' or 'EEE MMM dd HH:mm:ss zzz yyyy'.");
            }
        }
    }
}
