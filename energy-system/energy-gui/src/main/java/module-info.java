module at.technikum.energygui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens at.technikum.energygui to javafx.fxml, com.fasterxml.jackson.databind;
    exports at.technikum.energygui;
}