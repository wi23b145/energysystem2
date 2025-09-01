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

@Service // Kennzeichnet diese Klasse als einen Spring-Service, der Geschäftslogik enthält
@RequiredArgsConstructor // Lombok generiert den Konstruktor, der alle finalen Felder initialisiert (insbesondere 'repo' und 'rabbitTemplate')
public class UsageService {
    private static final Logger log = LoggerFactory.getLogger(UsageService.class); // Logger zur Protokollierung von Ereignissen
    private final UsageRepository repo; // Repository zum Zugriff auf die 'Usage'-Entität
    private final RabbitTemplate rabbitTemplate; // RabbitTemplate, um Nachrichten an RabbitMQ zu senden

    @Transactional // Markiert diese Methode als transaktional, d.h., alle Datenbankoperationen werden innerhalb einer Transaktion durchgeführt
    public void processUserEvent(EnergyEvent event) {
        log.info("Processing user event {}", event); // Loggt das Empfangene Ereignis
        var hour = event.getDatetime().truncatedTo(ChronoUnit.HOURS); // Trunciert den Zeitstempel auf die volle Stunde
        var usage = repo.findByHour(hour).orElse(Usage.builder() // Holt den 'Usage'-Datensatz für die Stunde oder erstellt einen neuen Datensatz
                .hour(hour)
                .communityProduced(BigDecimal.ZERO)
                .communityUsed(BigDecimal.ZERO)
                .gridUsed(BigDecimal.ZERO)
                .build());

        BigDecimal x = event.getKwh(); // Die verbrauchte Energiemenge (kWh) wird vom Ereignis übernommen

        // Berechnet, wie viel Energie noch in der Community verfügbar ist (sollte nicht negativ sein)
        BigDecimal available = usage.getCommunityProduced().subtract(usage.getCommunityUsed());
        if (available.signum() < 0) available = BigDecimal.ZERO; // Wenn der verfügbare Wert negativ ist, setze ihn auf 0

        // Berechnet den Anteil, der aus der Community und dem Netz bezogen wird
        BigDecimal communityDraw = x.min(available); // Der Betrag, der aus der Community entnommen wird
        BigDecimal gridDraw = x.subtract(communityDraw); // Der verbleibende Betrag, der aus dem Netz bezogen wird (>= 0)

        usage.setCommunityUsed( usage.getCommunityUsed().add(communityDraw) ); // Aktualisiert die Menge der aus der Community verwendeten Energie
        usage.setGridUsed( usage.getGridUsed().add(gridDraw) ); // Aktualisiert die Menge der aus dem Netz bezogenen Energie

        // Runden der Werte auf 3 Dezimalstellen, um Rundungsfehler zu vermeiden
        usage.setCommunityUsed( usage.getCommunityUsed().setScale(3, RoundingMode.HALF_UP) );
        usage.setGridUsed( usage.getGridUsed().setScale(3, RoundingMode.HALF_UP) );

        repo.save(usage); // Speichert den aktualisierten 'Usage'-Datensatz in der Datenbank
        sendUpdate(usage); // Sendet ein Update an RabbitMQ
    }

    @Transactional // Diese Methode ist ebenfalls transaktional
    public void processProducerEvent(EnergyEvent event) {
        log.info("Processing producer event {}", event); // Loggt das Ereignis
        var hour = event.getDatetime().truncatedTo(ChronoUnit.HOURS); // Trunciert den Zeitstempel auf die Stunde
        var usage = repo.findByHour(hour).orElse(Usage.builder() // Holt den 'Usage'-Datensatz oder erstellt einen neuen
                .hour(hour)
                .communityProduced(BigDecimal.ZERO)
                .communityUsed(BigDecimal.ZERO)
                .gridUsed(BigDecimal.ZERO)
                .build());

        // Aktualisiert die produzierte Energie in der Community
        usage.setCommunityProduced(
                usage.getCommunityProduced().add(event.getKwh()).setScale(3, RoundingMode.HALF_UP)
        );

        repo.save(usage); // Speichert den aktualisierten 'Usage'-Datensatz
        sendUpdate(usage); // Sendet ein Update an RabbitMQ
    }

    private void sendUpdate(Usage usage) {
        var percentageEvent = PercentageEvent.builder() // Baut ein Prozentsatz-Ereignis
                .hour(usage.getHour()) // Stellt sicher, dass die Stunde im Event enthalten ist
                .communityProduced(usage.getCommunityProduced()) // Gibt die produzierte Energie weiter
                .communityUsed(usage.getCommunityUsed()) // Gibt die genutzte Energie weiter
                .gridUsed(usage.getGridUsed()) // Gibt die bezogene Energie aus dem Netz weiter
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConfig.Q_PERCENTAGE, percentageEvent); // Sendet das Event an RabbitMQ
    }
}
