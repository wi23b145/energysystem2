package at.technikum.energyapi.controller;

import at.technikum.energyapi.dto.CurrentDto;
import at.technikum.energyapi.dto.HistoricalDto;
import at.technikum.energyapi.service.EnergyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

// Der Controller stellt Endpunkte für den Zugriff auf die Energieservices zur Verfügung...
@RestController
@RequestMapping("/energy") // Basis-URL für alle Endpunkte dieser Klasse
public class EnergyController {

    // Energie-Service, der die Geschäftslogik enthält
    private final EnergyService service;

    // Konstruktor, um den Service zu initialisieren
    public EnergyController(EnergyService service) {
        this.service = service;
    }

    // Endpunkt, um den aktuellen Energieverbrauch abzurufen
    @GetMapping("/current")
    public CurrentDto current() {
        // Ruft die aktuelle Energieinformation vom Service ab
        return service.getCurrent();
    }

    // Endpunkt, um historische Energiedaten zu holen
    @GetMapping("/historical")
    public HistoricalDto historical(@RequestParam("start") Instant start,
                                    @RequestParam("end") Instant end) {
        // Überprüft, ob das Enddatum nach dem Startdatum liegt
        if (!end.isAfter(start))
            // Wenn das Enddatum nicht nach dem Startdatum ist, wird ein Fehler zurückgegeben
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end must be after start");

        // Ruft historische Energieinformationen vom Service ab
        return service.getHistorical(start, end);
    }
}
