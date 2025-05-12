
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
- Definition via Functional Interfaces mit Single Abstract Method
- Implementierung durch Lambda-Ausdruck oder Methodenreferenz

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


## 3. Zukunft mit Stream Gatherers

### 3.1 Allgemein
- Übersetzung: Gatherer = Sammler
- final seit Java 24 (März 2025)
- also in LTS Java 25 (September 2025) enthalten

### 3.2 Ziele (welche Probleme lösen?)
- eigene, wiederverwendbare Zwischenoperationen (Streaming API nicht aufblähen)
- Zustandsbehaftete Verarbeitung
  - zb Aggregation und Fensterung
  - also mehr Flexibilität
- Feinere Kontrolle über die Elementweitergabe in der Pipeline
- ermöglicht 1:1, 1:N, N:1 und M:N Transformationen
- Heise-Artikel:
  - Diese Ergänzung schließt eine lang bestehende Lücke in der Stream-Verarbeitung: 
  - die Fähigkeit, benutzerdefinierte Aggregationen über mehrere Elemente hinweg 
  - in kontrollierter, zustandsbehafteter Weise durchzuführen,
  - jedoch nicht erst am Ende der Pipeline, wie es bei Collector-Instanzen der Fall ist, 
  - sondern während des Stream-Prozesses selbst, als integraler Bestandteil der Transformation.

### 3.3 Aufbau

```
Gatherer<T,A,R> interface
```

- T = Input
- A = State
- R = Output

```
Gatherer<T, A, R> gatherer = Gatherer.of(
	() -> A,             				// Initialisierung
	(state, element, downstream) -> boolean, 	// Verarbeitung
	(state, downstream) -> void        		// Abschluss 
);
```

- Initialisierung
	- Erzeugt den initialen Zustand (Typ A)
- Verarbeitung
	- Verarbeitet jedes Element (Typ T)
	- aktualisiert den Zustand (Typ A) 
	- und pusht ggf. Ergebnisse (Typ R) über downstream
	- Gibt true zurück, wenn die Verarbeitung fortgesetzt werden soll
- Abschluss (optional)
	- Verarbeitet den finalen Zustand (Typ A) 
	- und pusht ggf. abschließende Ergebnisse (Typ R) über downstream

```
Stream<T> inputStream = ...;
Stream<R> outputStream = inputStream.gather(gatherer);
```

- es gibt vordefinierte Gatherer wie zb windowFixed, windowSliding, fold, etc 

### 3.4 Anwendungsfälle
- generell bei komplexeren Verarbeitungsschritten
- und/oder wenn der Zustand eine Rolle spielt
- wo die Verarbeitung eines Elements von seinem Kontext innerhalb des Streams abhängt
- Beispiele (generiert von KI)
  - Berechnung eines gleitenden Durchschnitts der letzten 3 Messwerte in einem Sensor-Datenstrom (Windowing)
  - Gruppieren von Logeinträgen, die innerhalb einer bestimmten Zeitspanne aufeinander folgen
  - Erkennen von aufeinanderfolgenden "Start"- und "End"-Ereignissen in einem Workflow
  - Anreichern von Verkaufsdaten mit Informationen über vorherige Käufe desselben Kunden innerhalb einer bestimmten Sitzung

### 3.5 Links
- Viktor Klang (Architekt Stream Gatherers)
    - Offizielles Dokument: https://openjdk.org/jeps/485
    - YouTube-Video (JavaOne 2025): https://www.youtube.com/watch?v=v_5SKpfkI2U&t=2207s
- Dan Vega (Spring Developer Advocate)
  - Artikel: https://www.danvega.dev/blog/stream-gatherers
  - GitHub-Repo: https://github.com/danvega/gatherer
  - YouTube-Video: https://www.youtube.com/watch?v=hIbCu1slooE&t=1147s
- Deutscher Artikel: https://www.heise.de/hintergrund/Core-Java-Die-Stream-API-im-Wandel-funktionale-Datenfluesse-in-Java-10353156.html?seite=2
