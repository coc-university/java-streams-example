package com.codecamp.streams;

import java.util.List;
import java.util.function.Predicate;

public class StreamsExample {

    public static void main(String[] args) {

        List<String> tiere = List.of("Hund", "Katze", "Maus", "Elefant", "Affe", "Vogel");

        // @FunctionalInterface mit Single Abstract Method
        Predicate<String> tierFilter = tier -> tier.length() == 4;

        List<String> tiereNachStreamVerarbeitung = tiere.stream() // Stream erzeugen
                .filter(tierFilter) // Lambda-Ausdruck implementiert Predicate<String>
                .peek(System.out::println) // Seiteneffekt der nicht den Stream beeinflusst (zb für Debugging)
                .map(String::toLowerCase) // Methodenreferenz als Implementierung von Function<String, String>
                .sorted() // Zwischenoperation ohne Lambda oder Methodenreferenz
                .toList(); // Endverarbeitung triggert Ausführung

        tiereNachStreamVerarbeitung.forEach(tier -> System.out.println("Tier: " + tier)); // Lambda-Ausdruck implementiert Consumer<String>
    }

}
