package at.technikum.energygui;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class EnergyController {

    // --- UI ---
    @FXML private Label lblCommunityDepleted, lblGridPortion;
    @FXML private DatePicker dpStart, dpEnd;
    @FXML private TableView<HistoryRow> tblHistory;
    @FXML private TableColumn<HistoryRow, String> colHour;
    @FXML private TableColumn<HistoryRow, Double> colProduced, colUsed, colGrid;
    @FXML private Label lblSumProduced, lblSumUsed, lblSumGrid;
    @FXML private ProgressBar pbCommunity;
    @FXML private ProgressBar pbGrid;
    @FXML private CheckBox cbAutoRefresh;
    @FXML private ComboBox<String> cbStartHour;
    @FXML private ComboBox<String> cbStartMin;
    @FXML private ComboBox<String> cbEndHour;
    @FXML private ComboBox<String> cbEndMin;

    // --- HTTP + JSON ---
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // Basis-URL deiner REST-API (ggf. anpassen!)
    private final String baseUrl = "http://localhost:8080";

    @FXML
    public void initialize() {
        colHour.setCellValueFactory(new PropertyValueFactory<>("hour"));
        colProduced.setCellValueFactory(new PropertyValueFactory<>("communityProduced"));
        colUsed.setCellValueFactory(new PropertyValueFactory<>("communityUsed"));
        colGrid.setCellValueFactory(new PropertyValueFactory<>("gridUsed"));

        dpStart.setValue(LocalDate.now());
        dpEnd.setValue(LocalDate.now());

        // Initialer Refresh → lädt Daten vom Backend
        onRefresh();
    }

    @FXML
    public void onRefresh() {
        runAsync(() -> {
            try {
                var req = HttpRequest.newBuilder(URI.create(baseUrl + "/energy/current")).GET().build();
                var res = http.send(req, HttpResponse.BodyHandlers.ofString());
                CurrentDTO dto = mapper.readValue(res.body(), CurrentDTO.class);

                Platform.runLater(() -> {
                    lblCommunityDepleted.setText(String.format("%.2f %%", dto.community_depleted));
                    pbCommunity.setProgress(dto.community_depleted / 100.0);

                    lblGridPortion.setText(String.format("%.2f %%", dto.grid_portion));
                    pbGrid.setProgress(dto.grid_portion / 100.0);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @FXML
    public void onShowData() {
        LocalDate s = dpStart.getValue();
        LocalDate e = dpEnd.getValue();
        if (s == null || e == null) return;

        String url = String.format("%s/energy/historical?start=%s&end=%s", baseUrl, s, e);
        runAsync(() -> {
            try {
                var req = HttpRequest.newBuilder(URI.create(url)).GET().build();
                var res = http.send(req, HttpResponse.BodyHandlers.ofString());
                HistoryRow[] rows = mapper.readValue(res.body(), HistoryRow[].class);
                List<HistoryRow> list = Arrays.asList(rows);

                double sumP = list.stream().mapToDouble(HistoryRow::getCommunityProduced).sum();
                double sumU = list.stream().mapToDouble(HistoryRow::getCommunityUsed).sum();
                double sumG = list.stream().mapToDouble(HistoryRow::getGridUsed).sum();

                Platform.runLater(() -> {
                    tblHistory.getItems().setAll(list);
                    lblSumProduced.setText(String.format("%.3f kWh", sumP));
                    lblSumUsed.setText(String.format("%.3f kWh", sumU));
                    lblSumGrid.setText(String.format("%.3f kWh", sumG));
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void runAsync(Runnable r) {
        Thread t = new Thread(r, "http-thread");
        t.setDaemon(true);
        t.start();
    }

    // --- DTOs ---
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurrentDTO {
        public String hour;
        public double community_depleted;
        public double grid_portion;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class HistoryRow {
        public String hour;
        public double community_produced;
        public double community_used;
        public double grid_used;

        public String getHour() { return hour; }
        public double getCommunityProduced() { return community_produced; }
        public double getCommunityUsed() { return community_used; }
        public double getGridUsed() { return grid_used; }
    }
}
