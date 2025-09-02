package at.technikum.percentageservice;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;

/**
 * Hauptanwendung für den Percentage Service.
 */
@EnableRabbit  // Aktiviert RabbitMQ
@SpringBootApplication  // Startet die Spring Boot-Anwendung
public class PercentageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PercentageServiceApplication.class, args);  // Startet die Anwendung
    }

    @Bean
    public Queue usageQueue() {
        return new Queue("energy.percentage", true);  // Definiert eine persistente RabbitMQ-Warteschlange
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();  // JSON-Konverter für Nachrichten
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setMessageConverter(jackson2JsonMessageConverter());  // Konfiguriert den Message-Converter
        return factory;
    }
}


