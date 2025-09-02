package at.technikum.percentageservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
