package at.technikum.percentageservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "percentage", schema="energysystem")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Percentage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    @Column(name = "id")
    private UUID id;

    @Column(name = "hour", columnDefinition = "TIMESTAMP")
    private Instant hour;

    @Column(name = "community_depleted", columnDefinition = "NUMERIC")
    private BigDecimal communityDepleted;

    @Column(name = "grid_portion", columnDefinition = "NUMERIC")
    private BigDecimal gridPortion;
}

