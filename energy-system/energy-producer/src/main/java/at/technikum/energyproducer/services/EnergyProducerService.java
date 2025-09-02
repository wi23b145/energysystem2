package at.technikum.energyproducer.services;

import at.technikum.energyproducer.config.RabbitMQConfig;  // Importiert die RabbitMQ-Konfigurationsklasse
import at.technikum.energyproducer.dto.EnergyEvent;  // Importiert das DTO für EnergyEvent
import at.technikum.energyproducer.dto.EnergyEventType;  // Importiert den EnergyEventType
import lombok.RequiredArgsConstructor;  // Annotation, die den Konstruktor automatisch erstellt
import org.slf4j.Logger;  // Importiert Logger für das Logging
import org.slf4j.LoggerFactory;  // Importiert die LoggerFactory zur Erstellung des Loggers
import org.springframework.amqp.rabbit.core.RabbitTemplate;  // Importiert RabbitTemplate für die Kommunikation mit RabbitMQ
import org.springframework.scheduling.annotation.Scheduled;  // Importiert die Scheduled-Annotation für periodische Aufgaben
import org.springframework.stereotype.Service;  // Markiert die Klasse als Spring-Service-Komponente

import java.math.BigDecimal;  // Importiert BigDecimal für präzise Berechnungen
import java.time.Instant;  // Importiert Instant für Zeitstempel
import java.time.ZoneOffset;  // Importiert ZoneOffset für Zeitzonenangaben

import static at.technikum.energyproducer.dto.EnergyEvent.*;  // Importiert alle statischen Mitglieder der EnergyEvent-Klasse

@Service  // Markiert die Klasse als Spring-Service
@RequiredArgsConstructor  // Generiert einen Konstruktor mit den finalen Feldern
public class EnergyProducerService {
    private static final Logger log = LoggerFactory.getLogger(EnergyProducerService.class);  // Erstellt einen Logger für die Klasse

    private final RabbitTemplate rabbitTemplate;  // RabbitTemplate, um Nachrichten an RabbitMQ zu senden

    // Methode, die alle 3 Sekunden eine zufällige Energiemenge sendet
    @Scheduled(fixedRate = 3000)  // Definiert, dass die Methode alle 3000 Millisekunden (3 Sekunden) ausgeführt wird
    public void generateAndSendRandomUsageEvent() {
        var currentTimestamp = Instant.now();  // Holt sich den aktuellen Zeitstempel
        var randomUsage = isPeakHour(currentTimestamp) ? generateRandomUsage(0.3, 0.7) : generateRandomUsage(0, 0.2);  // Generiert eine zufällige Energiemenge, abhängig von der Tageszeit
        var energyEvent = EnergyEvent.builder()  // Erzeugt ein neues EnergyEvent mit den erzeugten Werten
                .type(EnergyEventType.PRODUCER)  // Setzt den Event-Typ auf PRODUCER
                .kwh(randomUsage)  // Setzt die erzeugte Energiemenge
                .datetime(currentTimestamp)  // Setzt den Zeitstempel
                .association("COMMUNITY")  // Setzt die Assoziation auf "COMMUNITY"
                .build();  // Baut das EnergyEvent
        sendEnergyUsageEvent(energyEvent);  // Sendet das generierte Energieereignis
        log.info("Sent energy usage event: {}", energyEvent);  // Loggt das gesendete Ereignis
    }

    /**
     * Prüft, ob die aktuelle Stunde im Peak-Zeitraum liegt.
     * Der Peak-Zeitraum ist von 11:00 bis 15:00 Uhr.
     * @param instant Der zu prüfende Zeitstempel
     * @return true, wenn es sich um die Peak-Stunden handelt
     */
    private boolean isPeakHour(Instant instant) {
        var peakDayStartHour = 11;  // Beginn des Peak-Zeitraums (11:00)
        var peakDayEndHour = 15;  // Ende des Peak-Zeitraums (15:00)

        var currentHour = instant.atZone(ZoneOffset.UTC).getHour();  // Holt sich die aktuelle Stunde im UTC-Zeitzonenformat
        return currentHour >= peakDayStartHour && currentHour <= peakDayEndHour;  // Gibt true zurück, wenn die aktuelle Stunde im Peak-Zeitraum liegt
    }

    // Methode, um das Energieereignis an RabbitMQ zu senden
    public void sendEnergyUsageEvent(EnergyEvent energyEvent) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.Q_USAGE, energyEvent);  // Sendet das Energieereignis an die konfigurierte RabbitMQ-Queue
    }

    // Hilfsmethode zur Erzeugung eines zufälligen Energieverbrauchs innerhalb eines angegebenen Bereichs
    private BigDecimal generateRandomUsage(double min, double max) {
        double value = min + (Math.random() * (max - min));  // Generiert eine zufällige Zahl im Bereich [min, max]
        return BigDecimal.valueOf(value);  // Gibt den Wert als BigDecimal zurück
    }
}
