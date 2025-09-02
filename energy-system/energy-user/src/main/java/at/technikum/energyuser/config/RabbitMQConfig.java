package at.technikum.energyuser.config; // Paketdeklaration: Die Klasse gehört zum Package 'config' in 'energyuser'

import com.fasterxml.jackson.databind.ObjectMapper; // Importiert die ObjectMapper-Klasse von Jackson zum Umwandeln von Objekten in JSON und umgekehrt
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Importiert das Modul für die Behandlung von Java 8 Zeit-Typen (z. B. Instant)
import org.springframework.amqp.core.Queue; // Importiert die Queue-Klasse aus Spring AMQP, um eine Nachrichtenschlange zu erstellen
import org.springframework.amqp.rabbit.annotation.EnableRabbit; // Ermöglicht RabbitMQ-fähige Nachrichten in Spring
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter; // Importiert den JSON-Konverter für die Verarbeitung von Nachrichten
import org.springframework.context.annotation.Bean; // Importiert die Annotation zum Erstellen von Bean-Definitionen in Spring
import org.springframework.context.annotation.Configuration; // Importiert die Annotation zur Kennzeichnung einer Konfigurationsklasse

// Die Klasse ist als Konfigurationsklasse für Spring markiert. Das bedeutet, dass sie beans für die Spring-Anwendung bereitstellt.
@EnableRabbit // Diese Annotation ermöglicht es Spring, RabbitMQ zu verwenden und die Nachrichtenverarbeitung zu unterstützen.
@Configuration // Diese Annotation markiert die Klasse als Konfigurationsklasse für Spring, d. h., sie definiert Beans.
public class RabbitMQConfig {

    // Die Konstante für die RabbitMQ-Warteschlange, in der Nachrichten gespeichert werden.
    public static final String Q_USAGE = "energy.usage"; // Der Name der RabbitMQ-Warteschlange für 'Usage' (Energieverbrauch)

    // --- Bean für die RabbitMQ-Warteschlange ---
    // Diese Methode erstellt und registriert eine RabbitMQ-Warteschlange namens 'energy.usage'.
    // Die Warteschlange ist persistent (d.h., sie wird über Neustarts hinweg aufrechterhalten).
    @Bean
    public Queue usageQueue() {
        // Erzeugt und gibt eine neue RabbitMQ-Warteschlange zurück, die unter 'energy.usage' registriert wird.
        // Der Parameter 'true' bedeutet, dass die Warteschlange persistiert wird, also auch nach einem Neustart der Anwendung erhalten bleibt.
        return new Queue(Q_USAGE, true);
    }

    // --- Bean für den JSON-Nachrichten-Konverter ---
    // Diese Methode stellt einen JSON-Nachrichten-Konverter bereit, der in RabbitMQ verwendet wird, um Nachrichten zu serialisieren und deserialisieren.
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        // Erstelle einen ObjectMapper, der für die Serialisierung und Deserialisierung von Java-Objekten in JSON und zurück zuständig ist.
        ObjectMapper mapper = new ObjectMapper();

        // Registriere das JavaTimeModule, um mit Java 8 Zeit- und Datums-Typen wie 'Instant' zu arbeiten.
        // Ohne das Modul würde Jackson Schwierigkeiten haben, mit Java 8 Zeittypen wie 'Instant' umzugehen.
        mapper.registerModule(new JavaTimeModule());

        // Gib den JSON-Nachrichten-Konverter zurück, der den ObjectMapper nutzt.
        // Dieser Konverter wird dann von RabbitMQ verwendet, um Nachrichten in und aus JSON zu konvertieren.
        return new Jackson2JsonMessageConverter(mapper);
    }
}
