package at.technikum.energyapi.repo;

import at.technikum.energyapi.entities.Percentage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PercentageRepository extends CrudRepository<Percentage, UUID> {
    Optional<Percentage> findTopByOrderByHourDesc();
    Optional<Percentage> findByHour(Instant hour);
}
