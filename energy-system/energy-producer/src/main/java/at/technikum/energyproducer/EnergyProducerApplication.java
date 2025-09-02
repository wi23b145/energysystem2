package at.technikum.energyproducer;

import org.springframework.boot.SpringApplication;  // Importiert SpringApplication, um die Spring Boot-Anwendung zu starten
import org.springframework.boot.autoconfigure.SpringBootApplication;  // Importiert die Annotation, die die Klasse als Spring Boot-Anwendung markiert
import org.springframework.scheduling.annotation.EnableScheduling;  // Importiert die Annotation, um die Planungsfunktionen zu aktivieren

// Markiert die Klasse als eine Spring Boot-Anwendung
@SpringBootApplication
// Aktiviert die geplanten Aufgaben (z.B. wiederkehrende Aufgaben wie zeitgesteuerte Ereignisse)
@EnableScheduling
public class EnergyProducerApplication {

    // Die main-Methode zum Starten der Spring Boot-Anwendung
    public static void main(String[] args) {
        SpringApplication.run(EnergyProducerApplication.class, args);  // Startet die Spring Boot-Anwendung
    }
}
