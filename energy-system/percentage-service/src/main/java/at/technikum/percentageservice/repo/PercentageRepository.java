package at.technikum.percentageservice.repo;

import at.technikum.percentageservice.entity.Percentage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface PercentageRepository extends CrudRepository<Percentage, Instant> {
    Optional<Percentage> findByHour(Instant instant);
}
