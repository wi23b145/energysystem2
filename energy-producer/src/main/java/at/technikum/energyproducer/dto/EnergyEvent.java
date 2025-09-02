package at.technikum.energyproducer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO (Data Transfer Object) für das Energieereignis.
 * Diese Klasse stellt ein Ereignis dar, das Informationen über den Energieverbrauch und -typ enthält.
 */
@Getter  // Generiert automatisch die Getter-Methoden für alle Felder.
@Setter  // Generiert automatisch die Setter-Methoden für alle Felder.
@NoArgsConstructor  // Generiert einen Konstruktor ohne Parameter.
@AllArgsConstructor  // Generiert einen Konstruktor mit allen Feldern als Parametern.
@Builder  // Ermöglicht den Builder-Pattern für eine einfache Erstellung von Objekten.
@ToString  // Generiert automatisch die toString()-Methode für eine lesbare Darstellung des Objekts.
public class EnergyEvent {

    private EnergyEventType type;  // Der Typ des Energieereignisses (z.B. Produktion oder Verbrauch).
    private String association;  // Die zugehörige Entität, z.B. "COMMUNITY".
    private BigDecimal kwh;  // Der Energieverbrauch in kWh.
    private Instant datetime;  // Der Zeitpunkt des Ereignisses.
}
