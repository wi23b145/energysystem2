package at.technikum.energyproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication  // Markiert die Klasse als Spring Boot Startpunkt.
@EnableScheduling  // Aktiviert die Möglichkeit, geplante Aufgaben (z.B. mit @Scheduled) auszuführen.
public class EnergyProducerApplication {


    public static void main(String[] args) {
        SpringApplication.run(EnergyProducerApplication.class, args);  // Startet die Spring Boot-Anwendung.
    }
}
