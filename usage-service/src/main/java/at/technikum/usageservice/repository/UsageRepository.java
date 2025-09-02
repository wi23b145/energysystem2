package at.technikum.usageservice.repository;

import at.technikum.usageservice.entity.Usage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsageRepository extends CrudRepository<Usage, UUID> {
    Optional<Usage> findByHour(Instant instant);
}
