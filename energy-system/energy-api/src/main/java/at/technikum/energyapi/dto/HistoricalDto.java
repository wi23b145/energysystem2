package at.technikum.energyapi.dto;

import java.time.Instant;

public record HistoricalDto(Instant start, Instant end,
                            double community_produced,
                            double community_used,
                            double grid_used) {}
