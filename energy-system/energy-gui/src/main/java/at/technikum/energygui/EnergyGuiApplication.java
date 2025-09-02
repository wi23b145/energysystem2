package at.technikum.energygui;

import javafx.application.Application; // Importiere die Application-Klasse von JavaFX
import javafx.fxml.FXMLLoader; // Importiere den FXMLLoader für das Laden von FXML-Dateien
import javafx.scene.Parent; // Importiere die Parent-Klasse, um den Wurzelknoten der Szene zu definieren
import javafx.scene.Scene; // Importiere die Scene-Klasse für die Szene, die auf dem Stage angezeigt wird
import javafx.stage.Stage; // Importiere die Stage-Klasse, die das Fenster repräsentiert

// Die Hauptklasse für die JavaFX-Anwendung, die die "Application"-Klasse erweitert
public class EnergyGuiApplication extends Application {

    // Die start-Methode ist der Einstiegspunkt der Anwendung, die beim Starten aufgerufen wird.
    @Override
    public void start(Stage stage) throws Exception {
        // Setze die Default-API-URL, die durch eine VM-Option überschrieben werden kann
        // Über die VM-Option "-Denergy.api" kann die URL zur API angegeben werden, ansonsten wird der Standardwert verwendet.
        System.setProperty("energy.api",
                System.getProperty("energy.api", "http://localhost:8080")); // Standard-API: "http://localhost:8080"

        // Lade die FXML-Datei, die das Layout der Benutzeroberfläche beschreibt.
        // getResource() lädt die FXML-Datei aus dem Ressourcenordner.
        var url = getClass().getResource("/at/technikum/energygui/energy.fxml");

        // Wenn die URL null ist, wurde die FXML-Datei nicht gefunden, und eine Ausnahme wird ausgelöst.
        if (url == null) throw new IllegalStateException(
                "FXML fehlt: /at/technikum/energygui/energy.fxml"); // Fehler: Datei nicht gefunden

        // Lade die FXML-Datei in das Parent-Element der Scene
        Parent root = FXMLLoader.load(url);

        // Setze den Titel des Fensters
        stage.setTitle("Energy GUI");

        // Erstelle eine neue Szene mit der geladenen Benutzeroberfläche (root) und einer festen Größe (860x520).
        stage.setScene(new Scene(root, 860, 520));

        // Zeige das Fenster (Stage) an
        stage.show();
    }

    // Der main()-Methodenaufruf startet die JavaFX-Anwendung
    public static void main(String[] args) {
        launch(args); // Starte die Anwendung mit der launch()-Methode von Application
    }
}




