package at.technikum.usageservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyEvent {
    private EnergyEventType type;
    private String association; // e.g. "COMMUNITY"
    private BigDecimal kwh;
    private Instant datetime;
}
