package at.technikum.energyproducer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfigurationsklasse für RabbitMQ, die eine Warteschlange und einen JSON-Nachrichtenkonverter für die Anwendung definiert.
 */
@EnableRabbit  // Aktiviert RabbitMQ in der Anwendung.
@Configuration  // Markiert die Klasse als Spring-Konfigurationsklasse.
public class RabbitMQConfig {

    public static final String Q_USAGE = "energy.usage";  // Name der RabbitMQ-Warteschlange.

    // Erstellt eine persistente RabbitMQ-Warteschlange namens "energy.usage".
    @Bean
    public Queue usageQueue() {
        return new Queue(Q_USAGE, true);  // true bedeutet persistente Warteschlange.
    }

    // Konvertiert Nachrichten von und zu JSON.
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());  // Registriert das Modul für die Unterstützung von Instant.
        return new Jackson2JsonMessageConverter(mapper);  // Gibt den JSON-Nachrichtenkonverter zurück.
    }
}

