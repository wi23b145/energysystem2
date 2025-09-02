package at.technikum.energyapi.repo;

import at.technikum.energyapi.entities.Percentage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

// Markiert dieses Interface als Repository, das Spring Data automatisch implementiert
@Repository
public interface PercentageRepository extends CrudRepository<Percentage, UUID> {

    // Findet das neueste (letzte) 'Percentage'-Objekt nach der Stunde in absteigender Reihenfolge
    Optional<Percentage> findTopByOrderByHourDesc();

    // Findet ein 'Percentage'-Objekt basierend auf der angegebenen Stunde
    Optional<Percentage> findByHour(Instant hour);
}
