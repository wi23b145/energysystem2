package at.technikum.energyapi.dto;

import java.time.Instant;

public record CurrentDto(Instant hour, double community_depleted, double grid_portion) {}
