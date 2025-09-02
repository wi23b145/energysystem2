package at.technikum.energyapi.repo;

import at.technikum.energyapi.entities.Usage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsageRepository extends CrudRepository<Usage, UUID> {
    Optional<Usage> findTopByOrderByHourDesc();
    List<Usage> findAllByHourGreaterThanEqualAndHourLessThan(Instant start, Instant endExclusive);
}
