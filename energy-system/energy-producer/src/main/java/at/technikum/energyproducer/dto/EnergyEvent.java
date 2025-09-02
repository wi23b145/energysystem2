package at.technikum.energyproducer.dto;

import lombok.AllArgsConstructor;  // Annotation für den Konstruktor mit allen Feldern
import lombok.Builder;  // Annotation für den Builder-Pattern
import lombok.Getter;  // Annotation für automatische Getter-Methoden
import lombok.NoArgsConstructor;  // Annotation für den Konstruktor ohne Felder
import lombok.Setter;  // Annotation für automatische Setter-Methoden
import lombok.ToString;  // Annotation für die toString-Methode

import java.math.BigDecimal;  // Importiert BigDecimal für die Arbeit mit präzisen Zahlen
import java.time.Instant;  // Importiert Instant für Zeitstempel (Zeitpunkte)

@Getter  // Generiert automatisch Getter-Methoden für alle Felder
@Setter  // Generiert automatisch Setter-Methoden für alle Felder
@NoArgsConstructor  // Generiert einen Konstruktor ohne Felder
@AllArgsConstructor  // Generiert einen Konstruktor mit allen Feldern
@Builder  // Ermöglicht den Einsatz des Builder-Designmusters
@ToString  // Generiert automatisch eine toString-Methode
public class EnergyEvent {
    private EnergyEventType type;  // Der Typ des Ereignisses (z.B. PRODUCER)
    private String association;  // Die Assoziation des Ereignisses (z.B. "COMMUNITY")
    private BigDecimal kwh;  // Der Energieverbrauch in kWh
    private Instant datetime;  // Der Zeitstempel des Ereignisses
}
