package at.technikum.energyuser.service;

import at.technikum.energyuser.config.RabbitMQConfig;
import at.technikum.energyuser.dto.EnergyEvent;
import at.technikum.energyuser.dto.EnergyEventType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class EnergyUserService {
    private static final Logger log = LoggerFactory.getLogger(EnergyUserService.class);
    private final RabbitTemplate rabbitTemplate;

    // sendet alle 3 Sekunden
    @Scheduled(fixedRate = 3000)
    public void generateAndSendRandomUsageEvent() {
        Instant currentTimestamp = Instant.now();
        BigDecimal randomUsage = isPeakHour(currentTimestamp)
                ? generateRandomUsage(0.3, 0.7)
                : generateRandomUsage(0.0, 0.2);

        EnergyEvent energyEvent = EnergyEvent.builder()
                .type(EnergyEventType.USER)
                .kwh(randomUsage)
                .datetime(currentTimestamp)
                .association("COMMUNITY")
                .build();

        sendEnergyUsageEvent(energyEvent);
        log.info("Sent energy usage event: {}", energyEvent);
    }

    private boolean isPeakHour(Instant instant) {
        int h = instant.atZone(ZoneOffset.UTC).getHour();
        return (h >= 6 && h <= 10) || (h >= 18 && h <= 22);
    }

    public void sendEnergyUsageEvent(EnergyEvent energyEvent) {
        // sendet direkt an die Queue (Default-Exchange), wie in RabbitMQConfig definiert
        rabbitTemplate.convertAndSend(RabbitMQConfig.Q_USAGE, energyEvent);
    }

    private BigDecimal generateRandomUsage(double min, double max) {
        double value = min + (Math.random() * (max - min));
        return BigDecimal.valueOf(value);
    }
}
