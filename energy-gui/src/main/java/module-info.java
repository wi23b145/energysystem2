module at.technikum.energygui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens at.technikum.energygui to javafx.fxml, com.fasterxml.jackson.databind;
    exports at.technikum.energygui;
}