package at.technikum.energyproducer.services;

import at.technikum.energyproducer.config.RabbitMQConfig;
import at.technikum.energyproducer.dto.EnergyEvent;
import at.technikum.energyproducer.dto.EnergyEventType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;

import static at.technikum.energyproducer.dto.EnergyEvent.*;

@Service
@RequiredArgsConstructor
public class EnergyProducerService {
    private static final Logger log = LoggerFactory.getLogger(EnergyProducerService.class);

    private final RabbitTemplate rabbitTemplate;

    // sends message every 3 seconds
    @Scheduled(fixedRate = 3000)
    public void generateAndSendRandomUsageEvent() {
        var currentTimestamp = Instant.now();
        var randomUsage = isPeakHour(currentTimestamp) ? generateRandomUsage(0.3, 0.7) : generateRandomUsage(0, 0.2);
        var energyEvent = EnergyEvent.builder()
                .type(EnergyEventType.PRODUCER)
                .kwh(randomUsage)
                .datetime(currentTimestamp)
                .association("COMMUNITY")
                .build();
        sendEnergyUsageEvent(energyEvent);
        log.info("Sent energy usage event: {}", energyEvent);
    }

    /**
     * Check if the current hour is in peak.
     * Peak day is 11:00 - 15:00
     * @param instant to check
     * @return true if it is peak hour
     */
    private boolean isPeakHour(Instant instant) {
        var peakDayStartHour = 11;
        var peakDayEndHour = 15;


        var currentHour = instant.atZone(ZoneOffset.UTC).getHour();
        return currentHour >= peakDayStartHour && currentHour <= peakDayEndHour;
    }

    public void sendEnergyUsageEvent(EnergyEvent energyEvent) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.Q_USAGE, energyEvent);
    }

    private BigDecimal generateRandomUsage(double min, double max) {
        double value = min + (Math.random() * (max - min));
        return BigDecimal.valueOf(value);
    }

}
