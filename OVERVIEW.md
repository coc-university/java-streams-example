
# Überblick Java Streams

## 1. Rückblick (wie war es früher)
- Traditionelle Ansätze der Datenverarbeitung vor Java 8
  - Fokus auf das "wie"
  - imperativ, Schritt für Schritt Anweisungen
- zb for oder while Schleifen


## 2. Aktuelle Streams API

### 2.1 Was ist ein Stream?
- Abstraktionsschicht
- Sequenz von Elementen die Operationen unterstützt
- ein Stream ist keine Datenstruktur (wie Collections)
- Datenquelle bleibt unverändert

### 2.2 Vorteile (gegenüber früher)
- Verbesserte Lesbarkeit
  - Deklarativer Stil der Datenverarbeitung ("was" statt "wie")
  - Schritt Richtung funktionale Programmierung
- Performance
  - Lazy Evaluation, Registrierung → Ausführung erst bei Endverarbeitung
  - Parallele Ausführung möglich

### 2.3 Technische Grundlage

Hintergrund:
- Java ist keine nativ funktionale Sprache
- daher muss unter der Haube mit Schnittstellen und Klassen gearbeitet werden

Definition:
- Functional Interface mit Single Abstract Method (SAM)
- Wichtig: es darf nur eine abstrakte Methode geben, damit die Zuordnung der Impl. klar ist
- es dürfen beliebig viele konkret ausformulierte Methoden enthalten sein

```
@FunctionalInterface
public interface Predicate<T> {

    boolean method(T t);
    default otherMethod() {...}
    default anotherMethod() {...}
}
```

Implementierung:
- durch Lambda-Ausdruck (eigene Logik übergeben)
- oder via Methoden-Referenz (Verweis auf bestehende Logik)
- nicht empfohlen: mit einer anonymen Klasse (Boilerplate-Code)

```
// Beispiel Lambda-Ausdruck:
Predicate<String> tierFilter = tier -> tier.length() == 4;
```

### 2.4 Streams nutzen
- Erzeugung:
  - aus Collections bzw Arrays via .stream()
  - aus statischer Stream-Methode via Stream.of(...)
- Zwischenoperationen (Intermediate Operations), zb:
  - boolean filter(Predicate)
  - T map(Function)
- Endverarbeitung (Terminal Operation), zb:
  - void forEach(Consumer) -> Ausführen einer Aktion
  - T collect(Collector) -> Struktur erzeugen
  - count(), max() → Einzelnen Wert erzeugen

```
// Beispiel für Erzeugung und Verarbeitung eines Streams:
List<String> tiere = List.of(...);
List<String> tiereNachStreamVerarbeitung = tiere.stream() 
        .filter(tierFilter) 
        .map(String::toLowerCase) 
        .toList(); 
```

## 3. Zukunft mit Stream Gatherers

### 3.1 Allgemein
- Übersetzung: Gatherer = Sammler
- final seit Java 24 (März 2025)
- also in LTS Java 25 (September 2025) enthalten

### 3.2 Ziele (welche Probleme lösen?)
- eigene, wiederverwendbare Zwischenoperationen (Streaming API nicht aufblähen)
- Zustandsbehaftete Verarbeitung
- Feinere Kontrolle über die Elementweitergabe in der Pipeline
- ermöglicht 1:1, 1:N, N:1 und M:N Transformationen
- Heise-Artikel:
  - Diese Ergänzung schließt eine lang bestehende Lücke in der Stream-Verarbeitung: 
  - die Fähigkeit, benutzerdefinierte Aggregationen über mehrere Elemente hinweg 
  - in kontrollierter, zustandsbehafteter Weise durchzuführen,
  - jedoch nicht erst am Ende der Pipeline, wie es bei Collector-Instanzen der Fall ist, 
  - sondern während des Stream-Prozesses selbst, als integraler Bestandteil der Transformation.

### 3.4 Anwendungsfälle
- generell bei komplexeren Verarbeitungsschritten
- und/oder wenn der Zustand eine Rolle spielt
- wo die Verarbeitung eines Elements von seinem Kontext innerhalb des Streams abhängt
- Beispiele (generiert von KI)
  - Berechnung eines gleitenden Durchschnitts der letzten 3 Messwerte in einem Sensor-Datenstrom (Windowing)
  - Gruppieren von Logeinträgen, die innerhalb einer bestimmten Zeitspanne aufeinander folgen
  - Erkennen von aufeinanderfolgenden "Start"- und "End"-Ereignissen in einem Workflow
  - Anreichern von Verkaufsdaten mit Informationen über vorherige Käufe desselben Kunden innerhalb einer bestimmten Sitzung

### 3.4 Aufbau

Definition:
```
public interface Gatherer<T, A, R> {

    default Supplier<A> initializer() {               // Initialisierung
        return defaultInitializer();
    };

    Integrator<A, T, R> integrator();                 // Verarbeitung

    default BiConsumer<A, Downstream<> finisher() {   // Abschluss 
        return defaultFinisher();
    }
```

Implementierung:
```
Gatherer<T, A, R> gatherer = Gatherer.of(
    () -> A,                                          // Initialisierung
    (state, element, downstream) -> boolean,          // Verarbeitung
    (state, downstream) -> void                       // Abschluss 
);
```

Nutzung:
```
Stream<R> outputStream = inputStream.gather(gatherer)

// es gibt vordefinierte Gatherer wie zb windowFixed, windowSliding, fold, etc
```

Verwendete Generics:
- T = Input
- A = State
- R = Output

Downstream:
- ein Objekt für die kontrollierte Weitergabe von Elementen
- jedes .push() schiebt ein Element in den Ergebnis-Stream
- anschließend kann damit in der Pipeline weitergearbeitet werden

Abschnitte im Gatherer:
- Initialisierung
  - erzeugt den initialen Zustand
- Verarbeitung
  - verarbeitet jedes Element
  - aktualisiert den Zustand
  - und pusht ggf. Ergebnisse über Downstream
  - gibt true zurück, wenn die Verarbeitung fortgesetzt werden soll
- Abschluss (optional)
  - wird aufgerufen, nachdem alle Elemente verarbeitet wurden
  - verarbeitet noch den finalen/verbliebenen Zustand
  - und pusht ggf. abschließende Ergebnisse über Downstream

### 3.5 Links
- Viktor Klang (Architekt Stream Gatherers)
  - Offizielles Dokument: https://openjdk.org/jeps/485
  - YouTube-Video (JavaOne 2025): https://www.youtube.com/watch?v=v_5SKpfkI2U&t=2207s
- Dan Vega (Spring Developer Advocate)
  - Artikel: https://www.danvega.dev/blog/stream-gatherers
  - GitHub-Repo: https://github.com/danvega/gatherer
  - YouTube-Video: https://www.youtube.com/watch?v=hIbCu1slooE&t=1147s
- Deutscher Artikel: https://www.heise.de/hintergrund/Core-Java-Die-Stream-API-im-Wandel-funktionale-Datenfluesse-in-Java-10353156.html?seite=2
