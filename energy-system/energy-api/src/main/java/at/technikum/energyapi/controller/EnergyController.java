package at.technikum.energyapi.controller;

import at.technikum.energyapi.dto.CurrentPercentageDTO;
import at.technikum.energyapi.dto.HistoricalUsageDTO;
import at.technikum.energyapi.service.EnergyProxyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.time.Instant;

@RestController
@RequestMapping("/energy")
@CrossOrigin
public class EnergyController {
    private final EnergyProxyService proxy;
    public EnergyController(EnergyProxyService proxy) { this.proxy = proxy; }

    @GetMapping("/current")
    public Mono<CurrentPercentageDTO> current() { return proxy.current(); }

    @GetMapping("/historical")
    public Mono<HistoricalUsageDTO> historical(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end) {
        return proxy.historical(start, end);
    }
}
