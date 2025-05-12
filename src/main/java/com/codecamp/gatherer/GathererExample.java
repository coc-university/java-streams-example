package com.codecamp.gatherer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

public class GathererExample {

    public static void main(String[] args) {

        Stream<String> tiere = Stream.of("Hund", "Katze", "Maus", "Elefant", "Affe", "Vogel", "Fuchs");

        int batchSize = 3;

        Gatherer<String, List<String>, List<String>> gruppiereTiere = Gatherer.ofSequential(

                // Initialisierung vom Zustand
                ArrayList::new, // temporäre Liste

                // Verarbeitung jedes Elements und Update vom Zustand
                (zustand, tier, downstream) -> {
                    zustand.add(tier);
                    if (zustand.size() == batchSize) {
                        downstream.push(new ArrayList<>(zustand)); // gib Kopie der Gruppe weiter
                        zustand.clear(); // Zustand leeren für nächste Gruppe
                    }
                    return true;
                },

                // Abschluss (optional): Am Ende übrig gebliebene Elemente ausgeben
                (zustand, downstream) -> {
                    if (!zustand.isEmpty()) {
                        downstream.push(new ArrayList<>(zustand));
                    }
                }
        );

        tiere.gather(gruppiereTiere)
                .forEach(gruppe -> System.out.println("Gruppe: " + gruppe));
    }

    /*

    Das Beispiel zur Gruppierung von Elementen kann nicht mit der normalen Streams API gelöst werden.
    Mit einer klassischen For-Schleife ist es zwar möglich, hat aber folgende Nachteile:
        - Kein Streaming-Code, daher weniger konsistente Code-Base und kein Lazy Processing
        - schlechter Wiederverwendbar, da nicht so modular bzw herauslösbar
        - parallele Verarbeitung ist aufwendig

     */

}
