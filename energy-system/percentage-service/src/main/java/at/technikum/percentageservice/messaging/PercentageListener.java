package at.technikum.percentageservice.messaging;

import at.technikum.percentageservice.config.RabbitMQConfig;
import at.technikum.percentageservice.dto.PercentageEvent;
import at.technikum.percentageservice.service.PercentageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PercentageListener {

    private final PercentageService percentageService;

    @RabbitListener(queues = RabbitMQConfig.Q_PERCENTAGE)
    public void onPercentageEvent(PercentageEvent event) {
        percentageService.processPercentageEvent(event);
    }
}
