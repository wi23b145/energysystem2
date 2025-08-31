package at.technikum.usageservice.service;

import at.technikum.usageservice.config.RabbitMQConfig;
import at.technikum.usageservice.dto.EnergyEvent;
import at.technikum.usageservice.dto.PercentageEvent;
import at.technikum.usageservice.entity.Usage;
import at.technikum.usageservice.repository.UsageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class UsageService {
    private static final Logger log = LoggerFactory.getLogger(UsageService.class);
    private final UsageRepository repo;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void processUserEvent(EnergyEvent event) {
        log.info("Processing user event {}", event);
        var hour = event.getDatetime().truncatedTo(ChronoUnit.HOURS);
        var usage = repo.findByHour(hour).orElse(Usage.builder()
                .hour(hour)
                .communityProduced(BigDecimal.ZERO)
                .communityUsed(BigDecimal.ZERO)
                .gridUsed(BigDecimal.ZERO)
                .build());

        var availableInCommunity = usage.getCommunityProduced().subtract(usage.getCommunityUsed());
        // if community has enough to cover the needs, we don't change the grid used
        if (availableInCommunity.compareTo(event.getKwh()) >= 0) {
            usage.setCommunityUsed(usage.getCommunityUsed().add(event.getKwh()).setScale(3, RoundingMode.HALF_UP));
        } else {
            // the community did not produce enough to satisfy needs, so we need to take the rest from the grid
            var requiredFromGrid = event.getKwh().subtract(availableInCommunity);
            usage.setCommunityUsed(usage.getCommunityProduced().setScale(3, RoundingMode.HALF_UP));
            usage.setGridUsed(usage.getGridUsed().add(requiredFromGrid).setScale(3, RoundingMode.HALF_UP));
        }

        repo.save(usage);
        sendUpdate(usage);
    }

    @Transactional
    public void processProducerEvent(EnergyEvent event) {
        log.info("Processing producer event {}", event);
        var hour = event.getDatetime().truncatedTo(ChronoUnit.HOURS);
        var usage = repo.findByHour(hour).orElse(Usage.builder()
                .hour(hour)
                .communityProduced(BigDecimal.ZERO)
                .communityUsed(BigDecimal.ZERO)
                .gridUsed(BigDecimal.ZERO)
                .build());

        usage.setCommunityProduced(usage.getCommunityProduced().add(event.getKwh()).setScale(3, RoundingMode.HALF_UP));

        repo.save(usage);
        sendUpdate(usage);
    }

    private void sendUpdate(Usage usage) {
        var percentageEvent = PercentageEvent.builder()
                .hour(usage.getHour())
                .communityProduced(usage.getCommunityProduced())
                .communityUsed(usage.getCommunityUsed())
                .gridUsed(usage.getGridUsed())
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConfig.Q_PERCENTAGE, percentageEvent);
    }
}
