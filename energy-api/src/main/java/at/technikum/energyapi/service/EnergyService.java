package at.technikum.energyapi.service;

import at.technikum.energyapi.dto.CurrentDto;
import at.technikum.energyapi.dto.HistoricalDto;
import at.technikum.energyapi.entities.Usage;
import at.technikum.energyapi.repo.PercentageRepository;
import at.technikum.energyapi.repo.UsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class EnergyService {

    private final UsageRepository usageRepo;

    public CurrentDto getCurrent() {
        var opt = usageRepo.findTopByOrderByHourDesc();
        if (opt.isEmpty()) {
            return new CurrentDto(Instant.now(), 0.0, 0.0);
        }
        var u = opt.get();

        double produced = n(u.getCommunityProduced());
        double used     = n(u.getCommunityUsed());
        double grid     = n(u.getGridUsed());

        // Community Pool = Anteil der produzierten Energie, der verbraucht wurde (%)
        double pool = 0.0;
        if (produced > 0) {
            pool = (used / produced) * 100.0;
        }
        pool = clampPct(pool);

        // Grid Portion = Anteil Netz an (CommunityUsed + GridUsed) (%)
        double totalUsed = used + grid;
        double portion = 0.0;
        if (totalUsed > 0) {
            portion = (grid / totalUsed) * 100.0;
        }
        portion = clampPct(portion);

        return new CurrentDto(Instant.now(), round2(pool), round2(portion));
    }

    public HistoricalDto getHistorical(Instant start, Instant end) {
        // End exklusiv machen (eine Minute/Slot weiter)
        Instant endExclusive = end.plusSeconds(1); // oder plusMinutes(15) je nach Slot
        var rows = usageRepo.findAllByHourGreaterThanEqualAndHourLessThan(start, endExclusive);

        double produced = 0, used = 0, grid = 0;
        for (var u : rows) {
            produced += n(u.getCommunityProduced());
            used     += n(u.getCommunityUsed());
            grid     += n(u.getGridUsed());
        }
        return new HistoricalDto(start, end, round3(produced), round3(used), round3(grid));
    }

    private static double n(BigDecimal v) {
        return v == null ? 0.0 : v.doubleValue();
    }

    private static double clampPct(double v) {
        if (v < 0.0) return 0.0;
        if (v > 100.0) return 100.0;
        return v;
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private static double round3(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }
}
