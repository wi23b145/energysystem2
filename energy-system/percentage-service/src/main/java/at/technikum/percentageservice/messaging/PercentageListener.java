package at.technikum.percentageservice.messaging;

import at.technikum.percentageservice.config.RabbitMQConfig;
import at.technikum.percentageservice.dto.PercentageEvent;
import at.technikum.percentageservice.service.PercentageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Die Klasse PercentageListener ist verantwortlich für das Empfangen und Verarbeiten von
 * Nachrichten, die über RabbitMQ gesendet werden. Wenn ein neues PercentageEvent empfangen wird,
 * wird es an den PercentageService weitergegeben, um den Prozentsatz zu berechnen und zu verarbeiten.
 */
@Component  // Markiert die Klasse als Spring-Komponente, die als Bean verwaltet wird und für die Dependency Injection verfügbar ist.
@RequiredArgsConstructor  // Lombok-Annotation, die einen Konstruktor mit allen finalen Feldern (dependencies) erstellt.
public class PercentageListener {

    // Die PercentageService-Instanz wird über Dependency Injection in den Listener eingefügt.
    private final PercentageService percentageService;

    /**
     * Dieser Listener wird durch die Ankunft einer Nachricht in der RabbitMQ-Warteschlange
     * "energy.percentage" aktiviert. Wenn eine Nachricht empfangen wird, ruft dieser Listener
     * die Methode processPercentageEvent im PercentageService auf.
     *
     * @param event Das PercentageEvent-Objekt, das die empfangene Nachricht darstellt.
     */
    @RabbitListener(queues = RabbitMQConfig.Q_PERCENTAGE)  // Die Methode hört auf Nachrichten aus der RabbitMQ-Warteschlange, die in RabbitMQConfig.Q_PERCENTAGE definiert ist.
    public void onPercentageEvent(PercentageEvent event) {
        // Übergibt das empfangene PercentageEvent an den PercentageService zur weiteren Verarbeitung.
        percentageService.processPercentageEvent(event);
    }
}
