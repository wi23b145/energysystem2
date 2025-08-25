package at.technikum.percentageservice;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;

@EnableRabbit
@SpringBootApplication
public class PercentageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PercentageServiceApplication.class, args);
    }

    // Gemeinsamer Queue-Name (kann in application.properties überschrieben werden)
    @Bean
    public Queue queue(org.springframework.core.env.Environment env) {
        String name = env.getProperty("app.queue", "energyQueue");
        return new Queue(name, false);
    }

    // JSON <-> Objekt Mapping
    @Bean
    public Jackson2JsonMessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // @RabbitListener soll JSON automatisch in EnergyEvent mappen
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            org.springframework.amqp.rabbit.connection.ConnectionFactory cf,
            Jackson2JsonMessageConverter converter
    ) {
        SimpleRabbitListenerContainerFactory f = new SimpleRabbitListenerContainerFactory();
        f.setConnectionFactory(cf);
        f.setMessageConverter(converter);
        return f;
    }
}

