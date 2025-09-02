package at.technikum.percentageservice.service;

import at.technikum.percentageservice.dto.PercentageEvent;  // Importiert das DTO für PercentageEvent
import at.technikum.percentageservice.entity.Percentage;  // Importiert die Entität für Percentage
import at.technikum.percentageservice.repo.PercentageRepository;  // Importiert das Repository für Percentage
import lombok.RequiredArgsConstructor;  // Importiert die Annotation, um Konstruktoren automatisch zu erzeugen
import org.slf4j.Logger;  // Importiert Logger für Logging
import org.slf4j.LoggerFactory;  // Importiert die LoggerFactory zur Erstellung des Loggers
import org.springframework.stereotype.Service;  // Markiert die Klasse als Service-Komponente
import org.springframework.transaction.annotation.Transactional;  // Markiert die Methode als transaktional

import java.math.BigDecimal;  // Importiert BigDecimal für präzise Berechnungen
import java.math.RoundingMode;  // Importiert RoundingMode für das Runden von BigDecimal-Werten

@Service  // Kennzeichnet die Klasse als Spring Service
@RequiredArgsConstructor  // Erzeugt einen Konstruktor mit den finalen Feldern
public class PercentageService {
    private static final Logger log = LoggerFactory.getLogger(PercentageService.class);  // Logger für die Klasse

    private static final BigDecimal ZERO = BigDecimal.ZERO;  // Stellt den Wert 0 als BigDecimal bereit
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");  // Stellt den Wert 100 als BigDecimal bereit
    private static final int SCALE = 4;  // Gibt die Anzahl der Dezimalstellen für Berechnungen an (hier 4)

    private final PercentageRepository repo;  // Repository, das die Datenbankoperationen für Percentage verwaltet

    @Transactional  // Markiert die Methode als transaktional, was bedeutet, dass sie in einer Transaktion ausgeführt wird
    public void processPercentageEvent(PercentageEvent event) {
        log.info("Processing percentage event {}", event);  // Loggt das Ereignis, das verarbeitet wird

        var hour = event.getHour();  // Holt sich die Stunde aus dem Event
        var percentage = repo.findByHour(hour).orElse(Percentage.builder()  // Versucht, das Percentage-Objekt aus der DB zu holen oder erstellt eines
                .hour(hour)
                .communityDepleted(ZERO)  // Setzt den initialen Wert für communityDepleted
                .gridPortion(ZERO)  // Setzt den initialen Wert für gridPortion
                .build());  // Baut das Percentage-Objekt

        var produced = n(event.getCommunityProduced());  // Holt sich den Wert für produzierte Energie und prüft, ob er null ist
        var used = n(event.getCommunityUsed());  // Holt sich den Wert für genutzte Energie und prüft, ob er null ist
        var grid = n(event.getGridUsed());  // Holt sich den Wert für aus dem Netz genutzte Energie und prüft, ob er null ist

        // 1) Anteil der produzierten Energie, der bereits verbraucht ist
        BigDecimal depletedPct = ZERO;  // Initialisiert den Prozentsatz für verbrauchte Energie
        if (produced.signum() > 0) {  // Wenn die produzierte Energie größer als 0 ist
            depletedPct = used
                    .divide(produced, SCALE, RoundingMode.HALF_UP)  // Berechnet den Prozentsatz der verbrauchten Energie und rundet auf 4 Dezimalstellen
                    .multiply(ONE_HUNDRED);  // Multipliziert mit 100, um den Prozentsatz zu berechnen
        }
        depletedPct = clampPct(depletedPct);  // Stellt sicher, dass der Wert im Bereich 0..100 bleibt
        percentage.setCommunityDepleted(depletedPct);  // Setzt den berechneten Prozentsatz für communityDepleted

        // 2) Anteil der Energie aus dem Netz
        BigDecimal totalUsed = used.add(grid);  // Gesamtverbrauch (Nutzung + Netz)
        BigDecimal gridPct = ZERO;  // Initialisiert den Prozentsatz für Netzenergie
        if (totalUsed.signum() > 0) {  // Wenn der Gesamtverbrauch größer als 0 ist
            gridPct = grid
                    .divide(totalUsed, SCALE, RoundingMode.HALF_UP)  // Berechnet den Prozentsatz der Energie aus dem Netz und rundet auf 4 Dezimalstellen
                    .multiply(ONE_HUNDRED);  // Multipliziert mit 100, um den Prozentsatz zu berechnen
        }
        gridPct = clampPct(gridPct);  // Stellt sicher, dass der Wert im Bereich 0..100 bleibt
        percentage.setGridPortion(gridPct);  // Setzt den berechneten Prozentsatz für gridPortion

        // Optional: Schön runden für die Anzeige/Speicherung
        percentage.setCommunityDepleted(percentage.getCommunityDepleted().setScale(2, RoundingMode.HALF_UP));  // Rundet den Prozentsatz auf 2 Dezimalstellen
        percentage.setGridPortion(percentage.getGridPortion().setScale(2, RoundingMode.HALF_UP));  // Rundet den Prozentsatz auf 2 Dezimalstellen

        repo.save(percentage);  // Speichert das Percentage-Objekt in der Datenbank
    }

    // Hilfsmethode, die einen null-Wert durch ZERO ersetzt
    private static BigDecimal n(BigDecimal v) {
        return v == null ? ZERO : v;  // Gibt den Wert zurück, wenn er nicht null ist, andernfalls 0
    }

    // Stellt sicher, dass der Prozentsatz im Bereich 0..100 liegt
    private static BigDecimal clampPct(BigDecimal v) {
        if (v.compareTo(ZERO) < 0) return ZERO;  // Wenn der Wert kleiner als 0 ist, gibt es 0 zurück
        if (v.compareTo(ONE_HUNDRED) > 0) return ONE_HUNDRED;  // Wenn der Wert größer als 100 ist, gibt es 100 zurück
        return v;  // Andernfalls gibt es den Wert zurück
    }
}
