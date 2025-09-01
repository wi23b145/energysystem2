package at.technikum.usageservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit // Aktiviert RabbitMQ für die Spring-Anwendung, ermöglicht die Verwendung von RabbitMQ-bezogenen Funktionen
@Configuration // Kennzeichnet diese Klasse als eine Konfigurationsklasse, die Beans definiert
public class RabbitMQConfig {

    public static final String Q_USAGE = "energy.usage"; // Definiert den Namen der Queue für 'Usage'-Daten
    public static final String Q_PERCENTAGE = "energy.percentage"; // Definiert den Namen der Queue für 'Percentage'-Daten

    // Definiert eine Bean für die 'Usage'-Queue, die von Spring RabbitMQ verwaltet wird
    @Bean
    public Queue usageQueue() {
        // Die Queue ist persistent (d.h. sie überlebt einen Neustart des RabbitMQ-Servers)
        return new Queue(Q_USAGE, true);
    }

    // Definiert eine Bean für die 'Percentage'-Queue, die ebenfalls persistent ist
    @Bean
    public Queue percentageQueue() {
        // Die Queue ist persistent
        return new Queue(Q_PERCENTAGE, true);
    }

    // Definiert eine Bean für den Jackson2JsonMessageConverter, der die Nachricht von und nach JSON konvertiert
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        // Erzeugt ein neues 'ObjectMapper'-Objekt für die JSON-Konvertierung
        ObjectMapper mapper = new ObjectMapper();

        // Registriert das 'JavaTimeModule', um mit `Instant` und anderen Java-Zeit-APIs zu arbeiten
        mapper.registerModule(new JavaTimeModule());

        // Gibt einen neuen Jackson2JsonMessageConverter zurück, der das konstruierte ObjectMapper verwendet
        return new Jackson2JsonMessageConverter(mapper);
    }
}
