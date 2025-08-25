package at.technikum.percentageservice.core;

import at.technikum.percentageservice.model.EnergyEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;

/** Einfache Sliding-Window-Aggregation über die letzten N Sekunden. */
@Service
public class AggregationService {

    @Value("${percentage.windowSeconds:60}")
    private long windowSeconds;

    @Value("${percentage.capacity:100.0}")
    private double capacity;

    private final Deque<EnergyEvent> events = new ArrayDeque<>();
    private double sum = 0.0;

    public synchronized void add(EnergyEvent e) {
        events.addLast(e);
        sum += e.getEnergyUsed();
        purge();
    }

    private void purge() {
        LocalDateTime cutoff = LocalDateTime.now().minusSeconds(windowSeconds);
        while (!events.isEmpty() && events.peekFirst().getTimestamp().isBefore(cutoff)) {
            sum -= events.removeFirst().getEnergyUsed();
        }
        if (sum < 0) sum = 0;
    }

    public synchronized double total() { purge(); return sum; }
    public synchronized int count()    { purge(); return events.size(); }
    public long  windowSeconds()       { return windowSeconds; }
    public double capacity()           { return capacity; }

    public synchronized double percentage() {
        purge();
        if (capacity <= 0) return 0.0;
        double p = (sum / capacity) * 100.0;
        return Math.min(100.0, Math.max(0.0, p));
    }
}
