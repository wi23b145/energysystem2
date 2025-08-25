package at.technikum.percentageservice.api;

import at.technikum.percentageservice.core.AggregationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** Kleiner REST-Endpoint zum Abfragen der aktuellen Prozentzahl. */
@RestController
@RequestMapping("/percentage")
public class PercentageController {
    private final AggregationService agg;

    public PercentageController(AggregationService agg) { this.agg = agg; }

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> current() {
        return ResponseEntity.ok(Map.of(
                "windowSeconds",  agg.windowSeconds(),
                "capacity",       agg.capacity(),
                "eventsInWindow", agg.count(),
                "totalEnergy",    agg.total(),
                "percentage",     agg.percentage()
        ));
    }

    @GetMapping("/ping")
    public String ping() { return "percentage-ok"; }
}
