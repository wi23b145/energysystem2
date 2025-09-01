package at.technikum.energyapi.dto;

import java.time.Instant;

// Diese Record-Klasse repräsentiert historische Energiedaten
// Record-Typen bieten eine kompakte Möglichkeit, Daten zu kapseln
public record HistoricalDto(Instant start, Instant end,
                            double community_produced,
                            double community_used,
                            double grid_used) {}
