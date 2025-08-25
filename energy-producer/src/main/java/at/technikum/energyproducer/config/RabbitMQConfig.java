package at.technikum.energyproducer.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Stellt Queue + JSON-Konverter bereit. */
@Configuration
public class RabbitMQConfig {

    // Gemeinsamer Queue-Name (kann in application.properties überschrieben werden)
    @Value("${app.queue:energyQueue}")
    private String queueName;

    @Bean
    public Queue queue() {
        // non-durable reicht hier (Demo). Für Produktion -> true.
        return new Queue(queueName, false);
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonConverter() {
        // sorgt dafür, dass Events als JSON gesendet werden
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(org.springframework.amqp.rabbit.connection.ConnectionFactory cf) {
        RabbitTemplate tpl = new RabbitTemplate(cf);
        tpl.setMessageConverter(jacksonConverter());
        return tpl;
    }
}
