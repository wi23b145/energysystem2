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
        // Überprüft, ob es eine Spitzenzeit ist und generiert den Verbrauch entsprechend
        BigDecimal randomUsage = isPeakHour(currentTimestamp)
                ? generateRandomUsage(0.3, 0.7)  // Spitzenzeiten: höherer Verbrauch (0.3 bis 0.7 kWh)
                : generateRandomUsage(0.0, 0.2); // Normale Zeiten: niedrigerer Verbrauch (0.0 bis 0.2 kWh)

        // EnergyEvent erzeugen
        EnergyEvent energyEvent = EnergyEvent.builder()
                .type(EnergyEventType.USER)        // Event-Typ ist "USER"
                .kwh(randomUsage)                 // Generierter kWh-Verbrauch
                .datetime(currentTimestamp)        // Aktueller Zeitstempel
                .association("COMMUNITY")         // Zugehörigkeit zur "COMMUNITY"
                .build();

        // Das Event an die RabbitMQ-Queue senden
        sendEnergyUsageEvent(energyEvent);
        log.info("Sent energy usage event: {}", energyEvent); // Loggt das gesendete Event
    }

    // Überprüft, ob die gegebene Zeit ein Zeitraum mit Spitzenverbrauch ist (6-10 Uhr oder 18-22 Uhr)
    private boolean isPeakHour(Instant instant) {
        int h = instant.atZone(ZoneOffset.UTC).getHour();  // Holt die Stunde der Zeit
        return (h >= 6 && h <= 10) || (h >= 18 && h <= 22); // Spitzenzeiten: 6-10 Uhr und 18-22 Uhr
    }

    // Senden des EnergyEvent an die RabbitMQ-Queue
    public void sendEnergyUsageEvent(EnergyEvent energyEvent) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.Q_USAGE, energyEvent); // Senden an die Queue
    }

    // Generiert einen zufälligen Verbrauchswert im angegebenen Bereich
    private BigDecimal generateRandomUsage(double min, double max) {
        double value = min + (Math.random() * (max - min));  // Zufallswert zwischen min und max
        return BigDecimal.valueOf(value);  // Rückgabe als BigDecimal
    }
}
