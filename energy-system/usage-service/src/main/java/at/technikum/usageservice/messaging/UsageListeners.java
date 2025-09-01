package at.technikum.usageservice.messaging;

import at.technikum.usageservice.config.RabbitMQConfig;
import at.technikum.usageservice.dto.EnergyEvent;
import at.technikum.usageservice.dto.EnergyEventType;
import at.technikum.usageservice.service.UsageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component // Markiert die Klasse als Spring-Komponente, die vom Spring-Kontext verwaltet wird
public class UsageListeners {

    private static final Logger log = LoggerFactory.getLogger(UsageListeners.class); // Logger für diese Klasse, um Protokolle zu schreiben

    private final UsageService usageService; // Einbindung des 'UsageService', um Business-Logik auszuführen

    // Konstruktor zur Initialisierung des 'UsageService' über Dependency Injection
    public UsageListeners(UsageService usageService) {
        this.usageService = usageService; // Speichert die übergebene Instanz von 'UsageService'
    }

    // Der RabbitMQ Listener, der auf Nachrichten aus der Queue 'energy.usage' hört
    @RabbitListener(queues = RabbitMQConfig.Q_USAGE) // Gibt an, dass diese Methode Nachrichten aus der 'energy.usage'-Queue verarbeitet
    public void onUsageEvent(EnergyEvent event) {
        // Prüft den Typ des Events und leitet es an die entsprechende Methode des 'UsageService' weiter
        if (event.getType() == EnergyEventType.USER) {
            // Wenn das Event vom Typ 'USER' ist, wird die Methode 'processUserEvent' des 'UsageService' aufgerufen
            usageService.processUserEvent(event);
        } else if (event.getType() == EnergyEventType.PRODUCER) {
            // Wenn das Event vom Typ 'PRODUCER' ist, wird die Methode 'processProducerEvent' des 'UsageService' aufgerufen
            usageService.processProducerEvent(event);
        } else {
            // Wenn der Event-Typ nicht erkannt wird, wird eine Fehlermeldung im Log ausgegeben
            log.error("Unhandled event type: {}", event);
        }
    }
}