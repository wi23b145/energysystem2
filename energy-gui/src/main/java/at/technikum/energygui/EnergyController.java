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
    @FXML private Label lblCommunityDepleted;
    @FXML private Label lblGridPortion;
    @FXML private ProgressBar pbCommunity;
    @FXML private ProgressBar pbGrid;

    @FXML private CheckBox cbAutoRefresh;

    @FXML private DatePicker dpStart, dpEnd;
    @FXML private ComboBox<Integer> cbStartHour, cbStartMin, cbEndHour, cbEndMin;

    @FXML private TableView<HistoryRow> tblHistory;
    @FXML private TableColumn<HistoryRow, String> colHour;
    @FXML private TableColumn<HistoryRow, Double> colProduced, colUsed, colGrid;

    @FXML private Label lblSumProduced, lblSumUsed, lblSumGrid;

    // --- HTTP/JSON ---
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper om = new ObjectMapper().registerModule(new JavaTimeModule());
    private final String baseUrl = System.getProperty("energy-api", "http://localhost:8080");

    private Timeline autoTimer;

    @FXML
    public void initialize() {
        // Zeit-Combos
        for (int h = 0; h < 24; h++) { cbStartHour.getItems().add(h); cbEndHour.getItems().add(h); }
        for (int m : new int[]{0, 15, 30, 45}) { cbStartMin.getItems().add(m); cbEndMin.getItems().add(m); }

        // Defaults
        dpStart.setValue(LocalDate.now().minusDays(1));
        dpEnd.setValue(LocalDate.now());
        cbStartHour.getSelectionModel().select(0);
        cbStartMin.getSelectionModel().select(Integer.valueOf(0));
        cbEndHour.getSelectionModel().select(23);
        cbEndMin.getSelectionModel().select(Integer.valueOf(45));

        // Tabelle
        colHour.setCellValueFactory(new PropertyValueFactory<>("hour"));
        colProduced.setCellValueFactory(new PropertyValueFactory<>("communityProduced"));
        colUsed.setCellValueFactory(new PropertyValueFactory<>("communityUsed"));
        colGrid.setCellValueFactory(new PropertyValueFactory<>("gridUsed"));

        // optionaler Auto-Refresh (5s) für CURRENT
        cbAutoRefresh.selectedProperty().addListener((obs, oldV, enabled) -> toggleAutoRefresh(enabled));

        // beim Start einmal laden
        onRefresh();
    }
    private enum Mode { CURRENT, HISTORICAL }
    private Mode mode = Mode.CURRENT;
    // === FXML-Handler ===
    @FXML
    private void onRefresh() {
        loadCurrent();
        if (mode == Mode.HISTORICAL) {
            loadHistorical();
        }
    }

    @FXML
    private void onShowData() {
        mode = Mode.HISTORICAL;
        loadHistorical();
    }

    // === API Calls ===
    private void loadCurrent() {
        runAsync(() -> {
            String url = baseUrl + "/energy/current";
            var res = http.send(HttpRequest.newBuilder(URI.create(url)).GET().build(),
                    HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) { showError("HTTP "+res.statusCode()+"\n"+res.body()); return; }
            var dto = om.readValue(res.body(), CurrentDTO.class);

            BigDecimal pool = nz(dto.community_depleted);
            BigDecimal grid = nz(dto.grid_portion);

            Platform.runLater(() -> {
                lblCommunityDepleted.setText(fmtPercent(pool));
                lblGridPortion.setText(fmtPercent(grid));
                pbCommunity.setProgress(pool.doubleValue()/100);
                pbGrid.setProgress(grid.doubleValue()/100);
            });
        });
    }

    private void loadHistorical() {
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
            var dto = om.readValue(res.body(), HistoricalDTO.class);

            Platform.runLater(() -> {
                lblSumProduced.setText(fmtKwh(dto.community_produced));
                lblSumUsed.setText(fmtKwh(dto.community_used));
                lblSumGrid.setText(fmtKwh(dto.grid_used));

                // eine Gesamt-Zeile in der Tabelle (falls du stündlich willst, muss die API stündliche Daten liefern)
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

    // === Helpers ===
    private void toggleAutoRefresh(boolean enabled) {
        if (enabled) {
            if (autoTimer != null) autoTimer.stop();
            autoTimer = new Timeline(new KeyFrame(Duration.seconds(5), e -> loadCurrent()));
            autoTimer.setCycleCount(Timeline.INDEFINITE);
            autoTimer.playFromStart();
        } else if (autoTimer != null) { autoTimer.stop(); }
    }

    private static Instant toInstant(LocalDate d, Integer hour, Integer min) {
        int h = hour == null ? 0 : hour, m = min == null ? 0 : min;
        return LocalDateTime.of(d, LocalTime.of(h, m)).atZone(ZoneId.systemDefault()).toInstant();
    }

    private static BigDecimal nz(BigDecimal v){ return v == null ? BigDecimal.ZERO : v; }
    private static String fmtPercent(BigDecimal p){
        double v = p == null ? 0.0 : p.doubleValue();
        return String.format("%.2f%%", v);
    }
    private static String fmtKwh(BigDecimal v){ return String.format("%.3f kWh", v == null ? BigDecimal.ZERO : v); }
    private static double clamp01(double v){ return Math.max(0, Math.min(1, v)); }
    private static String pad(Integer n){ return String.format("%02d", n == null ? 0 : n); }

    private void runAsync(ThrowingRunnable r){
        new Thread(() -> {
            try { r.run(); }
            catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.getClass().getSimpleName()+": "+(ex.getMessage()==null?"(keine Message)":ex.getMessage()));
            }
        }).start();
    }
    private void showError(String msg){
        Platform.runLater(() -> {
            var a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Fehler"); a.setHeaderText("Fehler");
            a.setContentText(msg); a.setResizable(true);
            a.getDialogPane().setPrefSize(640, 380);
            a.showAndWait();
        });
    }
    @FunctionalInterface private interface ThrowingRunnable { void run() throws Exception; }

    // --- DTOs (snake_case wie API) ---
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentDTO { public BigDecimal community_depleted, grid_portion; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HistoricalDTO { public BigDecimal community_produced, community_used, grid_used; }

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
        public String getHour(){ return hour; }
        public double getCommunityProduced(){ return communityProduced; }
        public double getCommunityUsed(){ return communityUsed; }
        public double getGridUsed(){ return gridUsed; }
    }
}