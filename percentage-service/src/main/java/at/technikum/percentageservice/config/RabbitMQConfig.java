package at.technikum.percentageservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Mit @EnableRabbit wird RabbitMQ für die Anwendung aktiviert. Dies ermöglicht es,
// RabbitMQ-Nachrichten zu empfangen und zu senden, die in der Anwendung verwendet werden.
@EnableRabbit
@Configuration
public class RabbitMQConfig {

    // Name der RabbitMQ-Warteschlange, in der die Prozentsätze gespeichert werden.
    public static final String Q_PERCENTAGE = "energy.percentage";

    // Diese Bean erstellt eine RabbitMQ-Warteschlange namens "energy.percentage",
    // die Nachrichten vom Typ "EnergyEvent" empfängt.
    @Bean
    public Queue usageQueue() {
        // Die Warteschlange ist persistiert, d.h. sie bleibt auch nach einem Neustart von RabbitMQ erhalten.
        return new Queue(Q_PERCENTAGE, true);  // true bedeutet, dass die Warteschlange persistent ist.
    }

    // Diese Bean konvertiert Nachrichten von und zu JSON.
    // Der Jackson2JsonMessageConverter wird verwendet, um sicherzustellen, dass Nachrichten
    // im JSON-Format korrekt in Java-Objekte (und umgekehrt) konvertiert werden.
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        // Ein ObjectMapper wird verwendet, um die JSON-Nachricht in Java-Objekte umzuwandeln.
        // Der ObjectMapper ist Teil von Jackson und ermöglicht das Mapping zwischen Java und JSON.
        ObjectMapper mapper = new ObjectMapper();

        // Der JavaTimeModule wird hier registriert, um mit Zeitstempeln (z.B. Instant) korrekt umzugehen.
        // Dieser Schritt stellt sicher, dass Datums- und Zeitangaben richtig serialisiert und deserialisiert werden.
        // Ohne dieses Modul würde der ObjectMapper Probleme mit Zeitstempeln haben.
        mapper.registerModule(new JavaTimeModule());

        // Der Jackson2JsonMessageConverter verwendet diesen konfigurierten ObjectMapper,
        // um JSON-Nachrichten zu konvertieren, die an und von RabbitMQ gesendet werden.
        return new Jackson2JsonMessageConverter(mapper);
    }
}
