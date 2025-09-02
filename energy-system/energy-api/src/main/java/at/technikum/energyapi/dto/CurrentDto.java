package at.technikum.energyapi.dto;

import java.time.Instant;

// Diese Record-Klasse repräsentiert die aktuellen Energiedaten
public record CurrentDto(
        Instant hour,                // Die Stunde, zu der die Daten erfasst wurden
        double community_depleted,   // Energiemenge, die in der Community verbraucht wurde
        double grid_portion          // Anteil der Energie, der aus dem Netz stammt
) {}