package at.technikum.usageservice.dto;

import java.time.Instant;

public class EnergyEvent {
    public String type;        // "PRODUCER" or "USER"
    public String association; // e.g. "COMMUNITY"
    public double kwh;
    public Instant datetime;
}
