package at.technikum.energyapi.entities;

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

// Diese Entität repräsentiert den Energieverbrauch und die -produktion innerhalb einer Community und vom Stromnetz
@Entity
@Table(name = "energy_usage", schema="energysystem") // Tabellenname "energy_usage" im Schema "energysystem"
@Getter // Generiert Getter-Methoden für alle Felder
@Setter // Generiert Setter-Methoden für alle Felder
@Builder // Erlaubt das Erstellen von Objekten mit einem Builder-Muster
@AllArgsConstructor // Erzeugt einen Konstruktor mit allen Feldern
@NoArgsConstructor // Erzeugt einen parameterlosen Konstruktor
public class Usage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Die ID wird automatisch generiert
    @Column(name = "id") // Spaltenname in der Datenbank
    private UUID id; // Eindeutige ID für den Datensatz

    @Column(name = "hour", columnDefinition = "TIMESTAMP") // Die Stunde, zu der die Daten erfasst wurden
    private Instant hour; // Zeitpunkt der Erfassung

    @Column(name = "community_produced", columnDefinition = "NUMERIC") // Energiemenge, die innerhalb der Community produziert wurde
    private BigDecimal communityProduced;

    @Column(name = "community_used", columnDefinition = "NUMERIC") // Energiemenge, die von der Community verbraucht wurde
    private BigDecimal communityUsed;

    @Column(name = "grid_used", columnDefinition = "NUMERIC") // Energiemenge, die aus dem Stromnetz bezogen wurde
    private BigDecimal gridUsed;
}
