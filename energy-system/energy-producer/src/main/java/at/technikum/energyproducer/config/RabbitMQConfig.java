package at.technikum.energyproducer.config;

import com.fasterxml.jackson.databind.ObjectMapper;  // Importiert ObjectMapper für die Konvertierung von JSON
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;  // Importiert das JavaTimeModule zur Unterstützung von Java 8-Zeittypen (z.B. Instant)
import org.springframework.amqp.core.Queue;  // Importiert die Queue-Klasse für die Definition von RabbitMQ-Queues
import org.springframework.amqp.rabbit.annotation.EnableRabbit;  // Importiert die Annotation, um RabbitMQ zu aktivieren
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;  // Importiert den Jackson-Message-Converter für RabbitMQ
import org.springframework.context.annotation.Bean;  // Ermöglicht das Erstellen von Beans in Spring
import org.springframework.context.annotation.Configuration;  // Markiert die Klasse als Spring-Konfigurationsklasse

// Aktiviert RabbitMQ-Listener für die Spring-Anwendung
@EnableRabbit
// Markiert die Klasse als eine Konfigurationsklasse, die Beans für Spring definiert
@Configuration
public class RabbitMQConfig {

    // Definiert eine Konstante für den Namen der RabbitMQ-Queue
    public static final String Q_USAGE = "energy.usage";

    // Bean, um eine RabbitMQ-Queue zu erstellen
    @Bean
    public Queue usageQueue() {
        return new Queue(Q_USAGE, true);  // Erzeugt eine persistente Queue mit dem Namen "energy.usage"
    }

    // Bean für den JSON-Message-Converter, der es ermöglicht, RabbitMQ-Nachrichten als JSON zu empfangen und zu senden
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();  // Erstellt einen neuen ObjectMapper
        mapper.registerModule(new JavaTimeModule());  // Registriert das JavaTimeModule für die Unterstützung von Java 8-Zeittypen
        return new Jackson2JsonMessageConverter(mapper);  // Gibt den Jackson2JsonMessageConverter zurück, der den ObjectMapper nutzt
    }
}

