package at.technikum.energygui;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;
import java.util.List;

public class EnergyController {

    // --- IDs aus deinem FXML ---
    @FXML private Label lblCommunityDepleted; // Label für den Anteil der erschöpften Gemeinschaftsressourcen
    @FXML private Label lblGridPortion; // Label für den Anteil des Netzverbrauchs
    @FXML private ProgressBar pbCommunity; // Fortschrittsbalken für die Community-Ressourcen
    @FXML private ProgressBar pbGrid; // Fortschrittsbalken für den Netzverbrauch

    @FXML private CheckBox cbAutoRefresh; // CheckBox für automatisches Refresh

    @FXML private DatePicker dpStart, dpEnd; // DatePicker für Start- und Enddatum der historischen Daten
    @FXML private ComboBox<Integer> cbStartHour, cbStartMin, cbEndHour, cbEndMin; // ComboBox für Start- und Endzeit (Stunden und Minuten)

    @FXML private TableView<HistoryRow> tblHistory; // Tabelle für historische Daten
    @FXML private TableColumn<HistoryRow, String> colHour; // Spalte für die Stunde
    @FXML private TableColumn<HistoryRow, Double> colProduced, colUsed, colGrid; // Spalten für produzierte, verbrauchte und Netz-Nutzung

    @FXML private Label lblSumProduced, lblSumUsed, lblSumGrid; // Label für die Gesamtsumme der produzierten, verbrauchten und Netz-Nutzung

    // --- HTTP/JSON ---
    private final HttpClient http = HttpClient.newHttpClient(); // HTTP-Client für API-Anfragen
    private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule()); // Jackson-ObjectMapper für JSON-Verarbeitung
    private final String baseUrl = System.getProperty("energy-api", "http://localhost:8080"); // Basis-URL für die API

    private Timeline autoTimer; // Timeline für Auto-Refresh

    // --- Initialisierungsmethoden und Standardwerte ---
    @FXML
    public void initialize() {
        // Zeit-Combos: Stunden und Minuten füllen
        for (int h = 0; h < 24; h++) {
            cbStartHour.getItems().add(h);
            cbEndHour.getItems().add(h);
        }
        for (int m : new int[]{0, 15, 30, 45}) {
            cbStartMin.getItems().add(m);
            cbEndMin.getItems().add(m);
        }

        // Setze Standardwerte für Datum und Uhrzeit
        dpStart.setValue(LocalDate.now().minusDays(1)); // Standard: 1 Tag vor dem aktuellen Datum
        dpEnd.setValue(LocalDate.now()); // Standard: Heute
        cbStartHour.getSelectionModel().select(0); // Standard: 00:00 Uhr
        cbStartMin.getSelectionModel().select(0); // Standard: 00 Minuten
        cbEndHour.getSelectionModel().select(23); // Standard: 23:00 Uhr
        cbEndMin.getSelectionModel().select(45); // Standard: 45 Minuten

        // Tabellenkonfiguration
        colHour.setCellValueFactory(new PropertyValueFactory<>("hour"));
        colProduced.setCellValueFactory(new PropertyValueFactory<>("communityProduced"));
        colUsed.setCellValueFactory(new PropertyValueFactory<>("communityUsed"));
        colGrid.setCellValueFactory(new PropertyValueFactory<>("gridUsed"));

        // Optionaler Auto-Refresh (alle 5 Sekunden) für aktuelle Daten
        cbAutoRefresh.selectedProperty().addListener((obs, oldV, enabled) -> toggleAutoRefresh(enabled));

        // Beim Start einmal die Daten laden
        onRefresh();
    }

    private enum Mode { CURRENT, HISTORICAL } // Enum für die Anzeige von aktuellen und historischen Daten
    private Mode mode = Mode.CURRENT;

    // --- FXML-Handler ---
    @FXML
    private void onRefresh() {
        loadCurrent(); // Aktuelle Daten laden
        if (mode == Mode.HISTORICAL) {
            loadHistorical(); // Historische Daten laden, wenn der Modus so gesetzt ist
        }
    }

    @FXML
    private void onShowData() {
        mode = Mode.HISTORICAL; // Setze den Modus auf HISTORICAL
        loadHistorical(); // Historische Daten laden
    }

    // --- API Calls ---
    private void loadCurrent() {
        // API-Aufruf, um die aktuellen Daten zu laden
        runAsync(() -> {
            String url = baseUrl + "/energy/current"; // API-Endpunkt für aktuelle Daten
            var res = http.send(HttpRequest.newBuilder(URI.create(url)).GET().build(),
                    HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                showError("HTTP "+res.statusCode()+"\n"+res.body());
                return;
            }
            var dto = om.readValue(res.body(), CurrentDTO.class); // JSON-Daten deserialisieren

            BigDecimal pool = nz(dto.community_depleted); // Erschöpfung der Gemeinschaftsressourcen
            BigDecimal grid = nz(dto.grid_portion); // Anteil des Netzverbrauchs

            // GUI mit den geladenen Daten aktualisieren
            Platform.runLater(() -> {
                lblCommunityDepleted.setText(fmtPercent(pool));
                lblGridPortion.setText(fmtPercent(grid));
                pbCommunity.setProgress(pool.doubleValue()/100); // Fortschrittsbalken
                pbGrid.setProgress(grid.doubleValue()/100); // Fortschrittsbalken
            });
        });
    }

    private void loadHistorical() {
        // API-Aufruf, um historische Daten zu laden
        runAsync(() -> {
            if (dpStart.getValue() == null || dpEnd.getValue() == null) {
                showError("Bitte Start- und Enddatum wählen."); return;
            }
            Instant start = toInstant(dpStart.getValue(), cbStartHour.getValue(), cbStartMin.getValue());
            Instant end   = toInstant(dpEnd.getValue(),   cbEndHour.getValue(),  cbEndMin.getValue());
            if (!end.isAfter(start)) { showError("End muss nach Start liegen."); return; }

            String url = baseUrl + "/energy/historical?start="+start+"&end="+end;
            var res = http.send(HttpRequest.newBuilder(URI.create(url)).GET().build(),
                    HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) { showError("HTTP "+res.statusCode()+"\n"+res.body()); return; }
            var dto = om.readValue(res.body(), HistoricalDTO.class); // JSON-Daten deserialisieren

            // GUI mit den geladenen historischen Daten aktualisieren
            Platform.runLater(() -> {
                lblSumProduced.setText(fmtKwh(dto.community_produced));
                lblSumUsed.setText(fmtKwh(dto.community_used));
                lblSumGrid.setText(fmtKwh(dto.grid_used));

                // Eine Gesamt-Zeile in der Tabelle (optional, API muss stündliche Daten liefern)
                var row = new HistoryRow(
                        dpStart.getValue() + " " + pad(cbStartHour.getValue()) + ":" + pad(cbStartMin.getValue())
                                + " – " +
                                dpEnd.getValue() + " " + pad(cbEndHour.getValue()) + ":" + pad(cbEndMin.getValue()),
                        dto.community_produced, dto.community_used, dto.grid_used
                );
                tblHistory.getItems().setAll(List.of(row));
            });
        });
    }

    // --- Helper-Methoden ---
    private void toggleAutoRefresh(boolean enabled) {
        // Auto-Refresh aktivieren/deaktivieren
        if (enabled) {
            if (autoTimer != null) autoTimer.stop(); // Stoppe den alten Timer
            autoTimer = new Timeline(new KeyFrame(Duration.seconds(5), e -> loadCurrent())); // Setze neuen Timer
            autoTimer.setCycleCount(Timeline.INDEFINITE);
            autoTimer.playFromStart(); // Starte den Timer
        } else if (autoTimer != null) {
            autoTimer.stop(); // Stoppe den Timer
        }
    }

    private static Instant toInstant(LocalDate d, Integer hour, Integer min) {
        // Hilfsmethode, um LocalDate und Uhrzeit in einen Instant zu konvertieren
        int h = hour == null ? 0 : hour, m = min == null ? 0 : min;
        return LocalDateTime.of(d, LocalTime.of(h, m)).atZone(ZoneId.systemDefault()).toInstant();
    }

    private static BigDecimal nz(BigDecimal v) {
        // Rückgabe 0, wenn der Wert null ist
        return v == null ? BigDecimal.ZERO : v;
    }

    private static String fmtPercent(BigDecimal p) {
        // Prozentwert formatieren
        double v = p == null ? 0.0 : p.doubleValue();
        return String.format("%.2f%%", v);
    }

    private static String fmtKwh(BigDecimal v) {
        // kWh-Wert formatieren
        return String.format("%.3f kWh", v == null ? BigDecimal.ZERO : v);
    }

    private static String pad(Integer n) {
        // Zahl auf zwei Stellen formatieren
        return String.format("%02d", n == null ? 0 : n);
    }

    private void runAsync(ThrowingRunnable r) {
        // Asynchrone Ausführung von Methoden
        new Thread(() -> {
            try { r.run(); }
            catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.getClass().getSimpleName() + ": " + (ex.getMessage() == null ? "(keine Message)" : ex.getMessage()));
            }
        }).start();
    }

    private void showError(String msg) {
        // Fehler anzeigen
        Platform.runLater(() -> {
            var a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Fehler");
            a.setHeaderText("Fehler");
            a.setContentText(msg);
            a.setResizable(true);
            a.getDialogPane().setPrefSize(640, 380);
            a.showAndWait();
        });
    }

    @FunctionalInterface private interface ThrowingRunnable {
        void run() throws Exception;
    }

    // --- DTOs (snake_case wie API) ---
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentDTO {
        public BigDecimal community_depleted, grid_portion;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HistoricalDTO {
        public BigDecimal community_produced, community_used, grid_used;
    }

    // --- Tabellenmodell ---
    public static class HistoryRow {
        private final String hour;
        private final double communityProduced, communityUsed, gridUsed;

        public HistoryRow(String hour, BigDecimal produced, BigDecimal used, BigDecimal grid) {
            this.hour = hour;
            this.communityProduced = produced == null ? 0.0 : produced.doubleValue();
            this.communityUsed = used == null ? 0.0 : used.doubleValue();
            this.gridUsed = grid == null ? 0.0 : grid.doubleValue();
        }

        public String getHour() { return hour; }
        public double getCommunityProduced() { return communityProduced; }
        public double getCommunityUsed() { return communityUsed; }
        public double getGridUsed() { return gridUsed; }
    }
}






