package at.technikum.energygui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EnergyGuiApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // Default-API (per VM-Option -Denergy.api überschreibbar)
        System.setProperty("energy.api",
                System.getProperty("energy.api", "http://localhost:8080"));

        var url = getClass().getResource("/at/technikum/energygui/energy.fxml");
        if (url == null) throw new IllegalStateException(
                "FXML fehlt: /at/technikum/energygui/energy.fxml");

        Parent root = FXMLLoader.load(url);
        stage.setTitle("Energy GUI");
        stage.setScene(new Scene(root, 860, 520));
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}
