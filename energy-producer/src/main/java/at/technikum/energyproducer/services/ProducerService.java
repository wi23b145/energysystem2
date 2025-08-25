package at.technikum.energyproducer.services;

import at.technikum.energyproducer.model.EnergyEvent;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

/** Schickt in festen Intervallen Test-Events in die Queue. */
@Service
public class ProducerService {

    private final RabbitTemplate rabbit;
    private final Queue queue;

    // Default-User für die Demo
    @Value("${producer.userId:user-1}")
    private String defaultUserId;

    public ProducerService(RabbitTemplate rabbit, Queue queue) {
        this.rabbit = rabbit;
        this.queue = queue;
    }

    /** Ein Event gezielt senden (kannst du auch aus Tests aufrufen). */
    public EnergyEvent sendOnce(String userId, double energyUsed) {
        EnergyEvent e = new EnergyEvent(userId, energyUsed, LocalDateTime.now());
        rabbit.convertAndSend(queue.getName(), e);
        return e;
    }

    /** Demo-Timer: alle 5s wird ein zufälliges Event gesendet. Abschaltbar per -Dproducer.enabled=false */
    @Scheduled(fixedRateString = "${producer.fixedRateMillis:5000}")
    public void scheduledSend() {
        if (!Boolean.parseBoolean(System.getProperty("producer.enabled", "true"))) return;
        double energy = ThreadLocalRandom.current().nextDouble(1.0, 10.0); // 1..10
        sendOnce(defaultUserId, energy);
    }
}
