package at.technikum.energyapi.repo;

import at.technikum.energyapi.entities.Usage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Kennzeichnet dieses Interface als Repository, das Spring Data automatisch implementiert
@Repository
public interface UsageRepository extends CrudRepository<Usage, UUID> {

    // Findet das neueste 'Usage'-Objekt basierend auf der Stunde, absteigend sortiert
    Optional<Usage> findTopByOrderByHourDesc();

    // Findet alle 'Usage'-Objekte, deren Stunde innerhalb eines bestimmten Zeitrahmens liegt
    // Das 'endExclusive' bedeutet, dass das Enddatum nicht mit einbezogen wird
    List<Usage> findAllByHourGreaterThanEqualAndHourLessThan(Instant start, Instant endExclusive);
}
