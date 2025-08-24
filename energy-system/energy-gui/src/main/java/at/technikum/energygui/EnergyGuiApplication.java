package at.technikum.energygui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class EnergyGuiApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        var url = EnergyGuiApplication.class.getResource(
                "/resources/at/technikum/energygui/hello-view.fxml"   // <— absoluter Pfad!
        );
        if (url == null) {
            throw new IllegalStateException("FXML not found on classpath: /at/technikum/energygui/hello-view.fxml");
        }
        FXMLLoader fxml = new FXMLLoader(url);
        Scene scene = new Scene(fxml.load(), 860, 520);
        stage.setTitle("Energy GUI");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}