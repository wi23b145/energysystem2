package at.technikum.energyproducer.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class EnergyEvent implements Serializable {

    private String userId;
    private double energyUsed;
    private LocalDateTime timestamp;

    public EnergyEvent() {
    }

    public EnergyEvent(String userId, double energyUsed, LocalDateTime timestamp) {
        this.userId = userId;
        this.energyUsed = energyUsed;
        this.timestamp = timestamp;
    }

    // Getter und Setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getEnergyUsed() {
        return energyUsed;
    }

    public void setEnergyUsed(double energyUsed) {
        this.energyUsed = energyUsed;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
