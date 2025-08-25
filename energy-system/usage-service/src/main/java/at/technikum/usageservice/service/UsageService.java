package at.technikum.usageservice.service;

import at.technikum.usageservice.dto.EnergyEvent;
import at.technikum.usageservice.repository.UsageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UsageService {
    private static final Logger log = LoggerFactory.getLogger(UsageService.class);
    private final UsageRepository repo;

    public UsageService(UsageRepository repo) {
        this.repo = repo;
    }

    // entry point used by both listeners
    public void processEvent(EnergyEvent e) {
        String t = (e.type == null) ? "" : e.type.toUpperCase();

        if ("PRODUCER".equals(t)) {
            repo.addProducer(e.datetime, e.kwh);
        } else if ("USER".equals(t)) {
            repo.addUser(e.datetime, e.kwh);
        } else {
            log.warn("Unknown event type: {}", e.type);
        }
    }
}
