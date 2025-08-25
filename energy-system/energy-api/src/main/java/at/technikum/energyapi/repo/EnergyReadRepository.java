package at.technikum.energyapi.repo;

import at.technikum.energyapi.dto.CurrentDto;
import at.technikum.energyapi.dto.HistoricalDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Repository
public class EnergyReadRepository {
    private final JdbcTemplate jdbc;
    public EnergyReadRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public CurrentDto getCurrent() {
        return jdbc.query("""
        SELECT hour,
               CASE WHEN community_produced <= 0 THEN 0
                    ELSE LEAST(100, 100*community_used/community_produced) END AS community_depleted,
               CASE WHEN community_used <= 0 THEN 0
                    ELSE 100*GREATEST(0, community_used - community_produced)/community_used END AS grid_portion
          FROM usage_hour
         WHERE hour = date_trunc('hour', now())
        """,
                rs -> rs.next()
                        ? new CurrentDto(rs.getTimestamp("hour").toInstant(),
                        rs.getDouble("community_depleted"),
                        rs.getDouble("grid_portion"))
                        : new CurrentDto(Instant.now().truncatedTo(ChronoUnit.HOURS), 0, 0)
        );
    }

    public HistoricalDto getHistorical(Instant start, Instant end) {
        return jdbc.query("""
        SELECT COALESCE(SUM(community_produced),0) AS p,
               COALESCE(SUM(community_used),0) AS u,
               COALESCE(SUM(grid_used),0)       AS g
          FROM usage_hour
         WHERE hour >= ? AND hour < ?
        """,
                ps -> {
                    ps.setTimestamp(1, Timestamp.from(start));
                    ps.setTimestamp(2, Timestamp.from(end));
                },
                rs -> {
                    rs.next();
                    return new HistoricalDto(start, end,
                            rs.getDouble("p"), rs.getDouble("u"), rs.getDouble("g"));
                });
    }
}
