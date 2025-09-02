package at.technikum.usageservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PercentageEvent {
    private Instant hour;
    private BigDecimal communityProduced;
    private BigDecimal communityUsed;
    private BigDecimal gridUsed;
}
