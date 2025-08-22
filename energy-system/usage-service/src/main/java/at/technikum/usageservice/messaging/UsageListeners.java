package at.technikum.usageservice.messaging;

import at.technikum.usageservice.config.RabbitMQConfig;
import at.technikum.usageservice.dto.EnergyEvent;
import at.technikum.usageservice.service.UsageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UsageListeners {

    private final UsageService usageService;

    public UsageListeners(UsageService usageService) {
        this.usageService = usageService;
    }

    @RabbitListener(queues = RabbitMQConfig.Q_PRODUCER)
    public void onProducer(EnergyEvent event) {
        usageService.processEvent(event);
    }

    @RabbitListener(queues = RabbitMQConfig.Q_USER)
    public void onUser(EnergyEvent event) {
        usageService.processEvent(event);
    }
}
