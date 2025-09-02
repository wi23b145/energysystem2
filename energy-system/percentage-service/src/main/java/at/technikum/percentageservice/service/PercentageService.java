package at.technikum.percentageservice.service;

import at.technikum.percentageservice.dto.PercentageEvent;
import at.technikum.percentageservice.entity.Percentage;
import at.technikum.percentageservice.repo.PercentageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PercentageService {
    private static final Logger log = LoggerFactory.getLogger(PercentageService.class);

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final int SCALE = 4; // oder 6, wenn du’s noch glatter willst

    private final PercentageRepository repo;

    @Transactional
    public void processPercentageEvent(PercentageEvent event) {
        log.info("Processing percentage event {}", event);

        var hour = event.getHour();
        var percentage = repo.findByHour(hour).orElse(Percentage.builder()
                .hour(hour)
                .communityDepleted(ZERO)
                .gridPortion(ZERO)
                .build());

        var produced = n(event.getCommunityProduced());
        var used     = n(event.getCommunityUsed());
        var grid     = n(event.getGridUsed());

        // 1) Anteil der produzierten Energie, der bereits verbraucht ist
        BigDecimal depletedPct = ZERO;
        if (produced.signum() > 0) {
            depletedPct = used
                    .divide(produced, SCALE, RoundingMode.HALF_UP)  // <— mit Scale
                    .multiply(ONE_HUNDRED);
        }
        depletedPct = clampPct(depletedPct); // 0..100
        percentage.setCommunityDepleted(depletedPct);

        // 2) Anteil aus dem Netz
        BigDecimal totalUsed = used.add(grid); // = tatsächlicher Verbrauch
        BigDecimal gridPct = ZERO;
        if (totalUsed.signum() > 0) {
            gridPct = grid
                    .divide(totalUsed, SCALE, RoundingMode.HALF_UP) // <— mit Scale
                    .multiply(ONE_HUNDRED);
        }
        gridPct = clampPct(gridPct);
        percentage.setGridPortion(gridPct);

        // Optional: schön runden für die Anzeige/Speicherung
        percentage.setCommunityDepleted( percentage.getCommunityDepleted().setScale(2, RoundingMode.HALF_UP) );
        percentage.setGridPortion(       percentage.getGridPortion().setScale(2, RoundingMode.HALF_UP) );

        repo.save(percentage);
    }

    private static BigDecimal n(BigDecimal v) { return v == null ? ZERO : v; }

    private static BigDecimal clampPct(BigDecimal v) {
        if (v.compareTo(ZERO) < 0) return ZERO;
        if (v.compareTo(ONE_HUNDRED) > 0) return ONE_HUNDRED;
        return v;
    }
}
