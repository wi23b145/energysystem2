package at.technikum.energyuser;
import java.time.OffsetDateTime;
import java.util.Random;

public class UsageGenerator {

    // Methode, um kWh für den Nutzer zu generieren
    public double generateUserKwh(Random rnd) {
        // Die aktuelle Stunde abrufen
        int hour = OffsetDateTime.now().getHour();
        double base; // Basiswert für kWh pro Minute

        // Bestimmung des Basiswerts je nach Tageszeit
        if (hour >= 7 && hour < 9) {
            base = 0.010; // Morgen-Peak (7-9 Uhr)
        } else if (hour >= 17 && hour < 21) {
            base = 0.012; // Abend-Peak (17-21 Uhr)
        } else if (hour >= 0 && hour < 5) {
            base = 0.003; // Nacht (0-5 Uhr)
        } else {
            base = 0.006; // Grundlast (sonst)
        }

        // Hinzufügen von ±20% Rauschen für zufällige Schwankungen
        double noise = (rnd.nextDouble() * 0.4) - 0.2; // Zufallswert zwischen -0.2 und +0.2
        double v = Math.max(0.0, base * (1.0 + noise)); // Berechnung des endgültigen kWh-Werts und sicherstellen, dass der Wert nicht negativ wird
        return Math.round(v * 1000.0) / 1000.0; // Rückgabe des Werts auf 3 Nachkommastellen gerundet
    }
}
