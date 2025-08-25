package at.technikum.percentageservice.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/** Event-DTO wie vom Producer gesendet. */
public class EnergyEvent implements Serializable {
    private String userId;
    private double energyUsed;
    private LocalDateTime timestamp;

    public EnergyEvent() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public double getEnergyUsed() { return energyUsed; }
    public void setEnergyUsed(double energyUsed) { this.energyUsed = energyUsed; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
