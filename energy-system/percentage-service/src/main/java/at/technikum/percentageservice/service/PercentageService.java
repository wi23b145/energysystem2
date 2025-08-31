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

    private final PercentageRepository repo;

    @Transactional
    public void processPercentageEvent(PercentageEvent event) {
        log.info("Processing percentage event {}", event);

        var hour = event.getHour();
        var percentage = repo.findByHour(hour).orElse(Percentage.builder()
                .hour(hour)
                .communityDepleted(BigDecimal.ZERO)
                .gridPortion(BigDecimal.ZERO)
                .build());

        var produced = event.getCommunityProduced();
        var used     = event.getCommunityUsed();
        var grid     = event.getGridUsed();

        // how many percent of community produced has been used already
        if (produced.compareTo(BigDecimal.ZERO) > 0) {
            // how many percent of community produced has been used already
            var communityDepletedPercent = used.divide(produced, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
            percentage.setCommunityDepleted(communityDepletedPercent);
        } else {
            percentage.setCommunityDepleted(new BigDecimal(100));
        }
        // how many percent of total energy used is from the grid
        var totalUsed = used.add(grid);
        var gridUsed = new BigDecimal(100).divide(totalUsed, RoundingMode.HALF_UP).multiply(grid);
        percentage.setGridPortion(gridUsed);

        repo.save(percentage);

    }


}
