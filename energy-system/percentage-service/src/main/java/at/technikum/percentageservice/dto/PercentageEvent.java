package at.technikum.percentageservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Die Klasse PercentageEvent repräsentiert ein Ereignis, das Informationen
 * zu den erzeugten und verbrauchten Energiemengen einer Gemeinschaft und dem
 * aus dem Stromnetz verbrauchten Anteil enthält.
 * Diese Informationen werden für die Berechnung und Anzeige des Anteils
 * der Gemeinschaftsenergie im Vergleich zum Netzverbrauch verwendet.
 */
@Getter  // Getter werden für alle Felder generiert
@Setter  // Setter werden für alle Felder generiert
@NoArgsConstructor  // Konstruktor ohne Argumente wird generiert
@AllArgsConstructor  // Konstruktor mit allen Feldern wird generiert
@Builder  // Ermöglicht den Builder-Pattern für eine leserliche und einfache Objekt-Erstellung
public class PercentageEvent {

    // Der Zeitpunkt des Ereignisses, z.B. der jeweilige Zeitpunkt, für den der Energieverbrauch gemessen wird.
    private Instant hour;

    // Die Menge an Energie, die von der Gemeinschaft produziert wurde, als BigDecimal für hohe Präzision.
    private BigDecimal communityProduced;

    // Die Menge an Energie, die von der Gemeinschaft verbraucht wurde, ebenfalls als BigDecimal.
    private BigDecimal communityUsed;

    // Der Anteil der Energie, der aus dem Stromnetz bezogen wurde, ebenfalls als BigDecimal.
    private BigDecimal gridUsed;
}
