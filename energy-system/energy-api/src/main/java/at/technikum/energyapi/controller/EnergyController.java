package at.technikum.energyapi.controller;

import at.technikum.energyapi.dto.CurrentDto;
import at.technikum.energyapi.dto.HistoricalDto;
import at.technikum.energyapi.repo.EnergyReadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestController
@RequestMapping("/energy")
public class EnergyController {
    private final EnergyReadRepository repo;
    public EnergyController(EnergyReadRepository repo) { this.repo = repo; }

    @GetMapping("/current")
    public CurrentDto current() {
        return repo.getCurrent();
    }

    @GetMapping("/historical")
    public HistoricalDto historical(@RequestParam Instant start,
                                    @RequestParam Instant end) {
        if (!end.isAfter(start))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end must be after start");
        return repo.getHistorical(start, end);
    }
}




