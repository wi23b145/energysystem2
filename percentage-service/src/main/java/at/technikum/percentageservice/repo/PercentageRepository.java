package at.technikum.percentageservice.repo;

import at.technikum.percentageservice.entity.Percentage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

/**
 * Das Repository für die Entität Percentage. Es ermöglicht den Zugriff auf die
 * Datenbank und stellt CRUD-Operationen (Create, Read, Update, Delete) für
 * Percentage-Objekte zur Verfügung. Dieses Repository erweitert die Funktionalität
 * von CrudRepository, einer von Spring Data bereitgestellten Schnittstelle.
 */
@Repository  // Markiert das Interface als ein Repository, das Spring als Datenzugriffs-Bean erkennt.
public interface PercentageRepository extends CrudRepository<Percentage, Instant> {

    /**
     * Diese Methode sucht nach einem Percentage-Objekt, das mit der angegebenen Stunde übereinstimmt.
     * Es wird ein Optional zurückgegeben, um anzuzeigen, dass das Ergebnis möglicherweise nicht vorhanden ist.
     *
     * @param instant Der Zeitpunkt (Stunde), zu dem der Prozentsatz abgerufen werden soll.
     * @return Ein Optional, das ein Percentage-Objekt enthält, falls eines mit der angegebenen Stunde gefunden wurde.
     */
    Optional<Percentage> findByHour(Instant instant);
}
