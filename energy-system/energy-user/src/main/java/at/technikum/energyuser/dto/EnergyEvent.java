package at.technikum.energyuser.dto; // Paketdeklaration: Die Klasse gehört zum DTO-Paket des Energy-User-Moduls.

import lombok.AllArgsConstructor; // Importiert Lombok-Annotation für den Konstruktor mit allen Parametern
import lombok.Builder; // Importiert Lombok-Annotation für das Erstellen eines Builders
import lombok.Getter; // Importiert Lombok-Annotation für Getter-Methoden
import lombok.NoArgsConstructor; // Importiert Lombok-Annotation für den Standardkonstruktor
import lombok.Setter; // Importiert Lombok-Annotation für Setter-Methoden
import lombok.ToString; // Importiert Lombok-Annotation für die toString()-Methode

import java.math.BigDecimal; // Importiert BigDecimal für die präzise Speicherung von Dezimalzahlen wie kWh
import java.time.Instant; // Importiert Instant für die Zeiterfassung mit Zeitstempeln (Java 8)

@Getter // Erzeugt automatisch Getter-Methoden für alle Felder
@Setter // Erzeugt automatisch Setter-Methoden für alle Felder
@NoArgsConstructor // Erzeugt automatisch einen Standardkonstruktor ohne Parameter
@AllArgsConstructor // Erzeugt automatisch einen Konstruktor mit allen Parametern
@Builder // Ermöglicht das Erstellen von Objekten dieser Klasse mit dem Builder-Pattern
@ToString // Erzeugt automatisch eine toString()-Methode, die eine stringisierte Darstellung des Objekts erzeugt
public class EnergyEvent {

    // --- Felder der Klasse EnergyEvent ---

    private EnergyEventType type; // Typ des Events (z.B. PRODUCER oder USER)
    private String association; // Assoziation des Events, z.B. "COMMUNITY" oder andere Identifikatoren
    private BigDecimal kwh; // Energieverbrauch oder -produktion in Kilowattstunden
    private Instant datetime; // Zeitpunkt des Ereignisses, z.B. der Zeitstempel des Ereignisses



}



