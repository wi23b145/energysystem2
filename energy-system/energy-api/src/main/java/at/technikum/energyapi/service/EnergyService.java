package at.technikum.energyapi.service;

import at.technikum.energyapi.dto.CurrentDto;
import at.technikum.energyapi.dto.HistoricalDto;
import at.technikum.energyapi.repo.UsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

// Kennzeichnet diese Klasse als einen Service, der Geschäftslogik enthält
@Service
@RequiredArgsConstructor // Lombok generiert den Konstruktor mit allen finalen Feldern
public class EnergyService {

    private final UsageRepository usageRepo; // Das Repository, das für den Zugriff auf die 'Usage'-Entität zuständig ist

    // Diese Methode liefert die aktuellen Energiedaten zurück
    public CurrentDto getCurrent() {
        // Ruft das neueste 'Usage'-Objekt basierend auf dem neuesten 'hour'-Wert ab
        var opt = usageRepo.findTopByOrderByHourDesc();

        // Falls kein Wert gefunden wird, wird ein Default-Wert mit 0 zurückgegeben
        if (opt.isEmpty()) {
            return new CurrentDto(Instant.now(), 0.0, 0.0);
        }

        // Holt das 'Usage'-Objekt aus dem Optional, falls es existiert
        var u = opt.get();

        // Umwandlung der BigDecimal-Werte in double
        double produced = n(u.getCommunityProduced());
        double used     = n(u.getCommunityUsed());
        double grid     = n(u.getGridUsed());

        // Berechnet den Anteil der produzierten Energie, die in der Community verbraucht wurde (%)
        double pool = 0.0;
        if (produced > 0) {
            pool = (used / produced) * 100.0;
        }
        pool = clampPct(pool); // Stellt sicher, dass der Wert zwischen 0 und 100 liegt

        // Berechnet den Anteil des Stromnetzes an der gesamten genutzten Energie (%)
        double totalUsed = used + grid;
        double portion = 0.0;
        if (totalUsed > 0) {
            portion = (grid / totalUsed) * 100.0;
        }
        portion = clampPct(portion); // Stellt sicher, dass der Wert zwischen 0 und 100 liegt

        // Gibt die aktuellen Werte als 'CurrentDto' zurück (Werte werden auf 2 Dezimalstellen gerundet)
        return new CurrentDto(Instant.now(), round2(pool), round2(portion));
    }

    // Diese Methode liefert historische Energiedaten für einen bestimmten Zeitraum
    public HistoricalDto getHistorical(Instant start, Instant end) {
        // Stellt sicher, dass das Enddatum exklusiv ist (d.h., es wird eine Minute zum Endzeitpunkt hinzugefügt)
        Instant endExclusive = end.plusSeconds(1); // Oder plusMinutes(15) je nach dem Zeitrahmen (z.B. Minuten, Stunden)

        // Holt alle 'Usage'-Datensätze, die im angegebenen Zeitraum liegen
        var rows = usageRepo.findAllByHourGreaterThanEqualAndHourLessThan(start, endExclusive);

        // Summiert die produzierten, verbrauchten und aus dem Netz bezogenen Energiemengen
        double produced = 0, used = 0, grid = 0;
        for (var u : rows) {
            produced += n(u.getCommunityProduced());
            used     += n(u.getCommunityUsed());
            grid     += n(u.getGridUsed());
        }

        // Gibt die aggregierten historischen Daten als 'HistoricalDto' zurück
        return new HistoricalDto(start, end, round3(produced), round3(used), round3(grid));
    }

    // Diese Methode wandelt einen BigDecimal-Wert in einen double-Wert um, wobei null-Werte als 0 behandelt werden
    private static double n(BigDecimal v) {
        return v == null ? 0.0 : v.doubleValue(); // Rückgabe von 0.0, wenn v null ist
    }

    // Diese Methode stellt sicher, dass der Prozentsatzwert im Bereich von 0 bis 100 liegt
    private static double clampPct(double v) {
        if (v < 0.0) return 0.0; // Wenn der Wert unter 0 ist, wird 0 zurückgegeben
        if (v > 100.0) return 100.0; // Wenn der Wert über 100 ist, wird 100 zurückgegeben
        return v; // Wenn der Wert im gültigen Bereich liegt, wird er unverändert zurückgegeben
    }

    // Diese Methode rundet einen double-Wert auf 2 Dezimalstellen
    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0; // Multipliziert mit 100, rundet und teilt durch 100
    }

    // Diese Methode rundet einen double-Wert auf 3 Dezimalstellen
    private static double round3(double v) {
        return Math.round(v * 1000.0) / 1000.0; // Multipliziert mit 1000, rundet und teilt durch 1000
    }
}