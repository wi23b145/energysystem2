package at.technikum.energyuser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class EnergyUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnergyUserApplication.class, args);
    }

    @Bean
    CommandLineRunner declareAndSend(AmqpAdmin admin,
                                     Exchange ex,
                                     Queue q,
                                     Binding b) {
        return args -> {
            admin.declareExchange(ex);
            admin.declareQueue(q);
            admin.declareBinding(b);
            // (Danach läuft dein bestehender runner() wie gehabt)
        };
    }

    // --- AMQP-Infrastruktur minimal: Direct-Exchange + Queue + Binding ---
    @Bean
    Exchange exchange(@Value("${app.amqp.exchange}") String name) {
        return ExchangeBuilder.directExchange(name).durable(true).build();
    }

    @Bean
    Queue queue(@Value("${app.amqp.queue}") String name) {
        return QueueBuilder.durable(name).build();
    }

    @Bean
    Binding binding(Queue q, Exchange ex, @Value("${app.amqp.routing}") String key) {
        return BindingBuilder.bind(q).to(ex).with(key).noargs();
    }

    @Bean
    com.fasterxml.jackson.databind.ObjectMapper objectMapper() {
        return new com.fasterxml.jackson.databind.ObjectMapper();
    }

    // --- Sender: alle X Sekunden eine USER-Message im verlangten Format ---
    @Bean
    CommandLineRunner runner(
            RabbitTemplate rabbit,
            ObjectMapper mapper,
            @Value("${app.amqp.exchange}") String exchange,
            @Value("${app.amqp.routing}") String routingKey,
            @Value("${app.user.interval-seconds}") int intervalSec
    ) {
        return args -> {
            var scheduler = Executors.newSingleThreadScheduledExecutor();
            var rnd = new Random();
            var generator = new UsageGenerator();

            scheduler.scheduleWithFixedDelay(() -> {
                try {
                    double kwh = generator.generateUserKwh(rnd);
                    Map<String, Object> message = Map.of(
                            "type", "USER",
                            "association", "COMMUNITY",
                            "kwh", kwh, // kWh in einer Minute (z. B. 0.007)
                            "datetime", OffsetDateTime.now(ZoneOffset.UTC).toString()
                    );
                    String json = mapper.writeValueAsString(message);
                    rabbit.convertAndSend(exchange, routingKey, json);
                    System.out.println("[USER -> MQ] " + json);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, intervalSec, TimeUnit.SECONDS);
        };
    }
}
