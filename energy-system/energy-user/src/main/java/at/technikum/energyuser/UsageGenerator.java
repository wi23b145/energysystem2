package at.technikum.energyuser;
import java.time.OffsetDateTime;
import java.util.Random;

public class UsageGenerator {

    public double generateUserKwh(Random rnd) {
        int hour = OffsetDateTime.now().getHour();
        double base; // kWh pro Minute

        if (hour >= 7 && hour < 9) {
            base = 0.010; // Morgen-Peak
        } else if (hour >= 17 && hour < 21) {
            base = 0.012; // Abend-Peak
        } else if (hour >= 0 && hour < 5) {
            base = 0.003; // Nacht
        } else {
            base = 0.006; // Grundlast
        }

        // ±20% Rauschen
        double noise = (rnd.nextDouble() * 0.4) - 0.2;
        double v = Math.max(0.0, base * (1.0 + noise));
        return Math.round(v * 1000.0) / 1000.0; // auf 3 Nachkommastellen
    }
}
