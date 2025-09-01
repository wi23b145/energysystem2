package at.technikum.usageservice.repository;

import at.technikum.usageservice.entity.Usage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository // Kennzeichnet das Interface als Repository und ermöglicht Spring Data, es zu erkennen und automatisch zu implementieren
public interface UsageRepository extends CrudRepository<Usage, UUID> {

    // Diese Methode sucht nach einem 'Usage'-Objekt, das den angegebenen Zeitpunkt ('Instant') hat
    Optional<Usage> findByHour(Instant instant);
}
