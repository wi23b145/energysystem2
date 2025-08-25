package at.technikum.percentageservice.mq;

import at.technikum.percentageservice.model.EnergyEvent;
import at.technikum.percentageservice.core.AggregationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Liest Events aus der Queue und übergibt sie an die Aggregation. */
@Component
public class EventListener {
    private final AggregationService agg;

    public EventListener(AggregationService agg) { this.agg = agg; }

    @RabbitListener(queues = "${app.queue:energyQueue}")
    public void onMessage(EnergyEvent event) {
        agg.add(event);
    }
}
