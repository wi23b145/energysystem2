package at.technikum.percentageservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Die Entität Percentage repräsentiert ein Datensatz aus der Tabelle "percentage" in der
 * Datenbank. Sie speichert die Prozentsätze für die Gemeinschafts- und Netzenergie
 * für eine bestimmte Stunde.
 *
 * Die Tabelle "percentage" enthält Informationen darüber, wie viel Prozent der Energie
 * aus der Gemeinschaftsproduktion und wie viel Prozent aus dem Netz bezogen wurde.
 */
@Entity  // Markiert die Klasse als eine JPA-Entität, die mit einer Datenbanktabelle verbunden wird.
@Table(name = "percentage", schema="energysystem")  // Definiert den Tabellennamen und das Schema in der Datenbank.
@Getter  // Lombok-Annotation, um automatisch Getter für alle Felder zu generieren.
@Setter  // Lombok-Annotation, um automatisch Setter für alle Felder zu generieren.
@Builder  // Ermöglicht die Verwendung des Builder-Patterns für eine einfache Erstellung von Objekten.
@AllArgsConstructor  // Generiert einen Konstruktor mit allen Feldern als Parametern.
@NoArgsConstructor  // Generiert einen Konstruktor ohne Parameter (für JPA und Deserialisierung).
public class Percentage {

    // Primärschlüssel für die Entität. Wird automatisch generiert.
    @Id  // Markiert das Feld als Primärschlüssel.
    @GeneratedValue(strategy = GenerationType.AUTO)  // Legt fest, dass der Wert automatisch generiert wird.
    @Column(name = "id")  // Definiert den Namen der Spalte in der Datenbank.
    private UUID id;  // Der Primärschlüssel ist vom Typ UUID, um eindeutige Identifikatoren zu erzeugen.

    // Zeitpunkt des Ereignisses (die Stunde, für die der Prozentsatz berechnet wird).
    @Column(name = "hour", columnDefinition = "TIMESTAMP")  // Die Spalte wird als TIMESTAMP in der Datenbank definiert.
    private Instant hour;  // Speichert den Zeitpunkt (z.B. die Uhrzeit) des Ereignisses als Instant.

    // Der Anteil der Energie, der von der Gemeinschaft verbraucht wurde (in Prozent).
    @Column(name = "community_depleted", columnDefinition = "NUMERIC")  // Die Spalte wird als NUMERIC (genaue Zahl) definiert.
    private BigDecimal communityDepleted;  // Der Prozentsatz der verbrauchten Gemeinschaftsenergie.

    // Der Anteil der Energie, der aus dem Stromnetz bezogen wurde (in Prozent).
    @Column(name = "grid_portion", columnDefinition = "NUMERIC")  // Die Spalte wird ebenfalls als NUMERIC definiert.
    private BigDecimal gridPortion;  // Der Prozentsatz der Energie, die aus dem Stromnetz bezogen wurde.
}

