package at.technikum.usageservice.messaging;

import at.technikum.usageservice.config.RabbitMQConfig;
import at.technikum.usageservice.dto.EnergyEvent;
import at.technikum.usageservice.dto.EnergyEventType;
import at.technikum.usageservice.service.UsageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UsageListeners {
    private static final Logger log = LoggerFactory.getLogger(UsageListeners.class);

    private final UsageService usageService;

    public UsageListeners(UsageService usageService) {
        this.usageService = usageService;
    }

    @RabbitListener(queues = RabbitMQConfig.Q_USAGE)
    public void onUsageEvent(EnergyEvent event) {
        if (event.getType() == EnergyEventType.USER) {
            usageService.processUserEvent(event);
        } else if (event.getType() == EnergyEventType.PRODUCER) {
            usageService.processProducerEvent(event);
        } else {
            log.error("Unhandled event type: {}", event);
        }
    }
}
