package at.technikum.usageservice.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Repository
public class UsageRepository {
    private final JdbcTemplate jdbc;
    public UsageRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Transactional
    public void addProducer(Instant ts, double kwh) {
        Instant hour = ts.truncatedTo(ChronoUnit.HOURS);

        // try update
        int rows = jdbc.update(
                "UPDATE usage_hour SET community_produced = community_produced + ? WHERE hour = ?",
                kwh, Timestamp.from(hour)
        );

        // if no row updated → insert new
        if (rows == 0) {
            jdbc.update(
                    "INSERT INTO usage_hour (hour, community_produced, community_used, grid_used) VALUES (?, ?, 0, 0)",
                    Timestamp.from(hour), kwh
            );
        }

        recomputeGrid(hour);
    }

    @Transactional
    public void addUser(Instant ts, double kwh) {
        Instant hour = ts.truncatedTo(ChronoUnit.HOURS);

        int rows = jdbc.update(
                "UPDATE usage_hour SET community_used = community_used + ? WHERE hour = ?",
                kwh, Timestamp.from(hour)
        );

        if (rows == 0) {
            jdbc.update(
                    "INSERT INTO usage_hour (hour, community_produced, community_used, grid_used) VALUES (?, 0, ?, 0)",
                    Timestamp.from(hour), kwh
            );
        }

        recomputeGrid(hour);
    }

    private void recomputeGrid(Instant hour) {
        jdbc.update(
                "UPDATE usage_hour SET grid_used = GREATEST(0, community_used - community_produced) WHERE hour = ?",
                Timestamp.from(hour)
        );
    }
}
