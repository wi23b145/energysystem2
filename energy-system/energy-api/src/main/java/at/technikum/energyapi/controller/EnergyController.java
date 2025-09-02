package at.technikum.energyapi.controller;

import at.technikum.energyapi.dto.CurrentDto;
import at.technikum.energyapi.dto.HistoricalDto;
import at.technikum.energyapi.service.EnergyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestController
@RequestMapping("/energy")
public class EnergyController {
    private final EnergyService service;
    public EnergyController(EnergyService service) { this.service = service; }

    @GetMapping("/current")
    public CurrentDto current() {
        return service.getCurrent();
    }

    @GetMapping("/historical")
    public HistoricalDto historical(@RequestParam("start") Instant start,
                                    @RequestParam("end") Instant end) {
        if (!end.isAfter(start))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end must be after start");
        return service.getHistorical(start, end);
    }
}
