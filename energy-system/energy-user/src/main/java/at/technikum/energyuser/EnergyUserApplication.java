package at.technikum.energyuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication // Diese Annotation markiert die Klasse als Spring Boot-Anwendung.
@EnableScheduling // Aktiviert die Unterstützung für geplante Aufgaben in Spring, damit Aufgaben in festgelegten Intervallen ausgeführt werden können.
public class EnergyUserApplication {

    // Einstiegspunkt der Spring Boot Anwendung.
    public static void main(String[] args) {
        SpringApplication.run(EnergyUserApplication.class, args); // Startet die Spring Boot Anwendung.
    }
}
