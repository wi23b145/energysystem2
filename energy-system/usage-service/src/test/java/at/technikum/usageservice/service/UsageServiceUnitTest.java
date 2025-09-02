package at.technikum.usageservice.service;

import at.technikum.usageservice.dto.EnergyEvent;
import at.technikum.usageservice.dto.EnergyEventType;
import at.technikum.usageservice.entity.Usage;
import at.technikum.usageservice.repository.UsageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UsageServiceUnitTest {

    @Mock
    private UsageRepository usageRepository;

    @InjectMocks
    private UsageService usageService;

    @Test
    void testCaseUserFromAssignment() {
        // prepare test data
        var usage = Usage.builder()
                .hour(Instant.parse("2025-01-10T14:00:00Z"))
                .communityProduced(new BigDecimal("18.05"))
                .communityUsed(new BigDecimal("18.02"))
                .gridUsed(new BigDecimal("1.056"))
                .build();

        Mockito.when(usageRepository.findByHour(Instant.parse("2025-01-10T14:00:00Z"))).thenReturn(Optional.of(usage));

        var newEvent = EnergyEvent.builder()
                .type(EnergyEventType.USER)
                .association("COMMUNITY")
                .kwh(new BigDecimal("0.05"))
                .datetime(Instant.parse("2025-01-10T14:34:00Z"))
                .build();

        // test
        usageService.processUserEvent(newEvent);

        // assertions
        var argumentCaptor = ArgumentCaptor.forClass(Usage.class);
        Mockito.verify(usageRepository).save(argumentCaptor.capture());

        var usageSaved = argumentCaptor.getValue();
        assertThat(usageSaved.getHour()).isEqualTo(Instant.parse("2025-01-10T14:00:00Z"));
        assertThat(usageSaved.getCommunityProduced()).isEqualTo(new BigDecimal("18.05"));
        assertThat(usageSaved.getCommunityUsed()).isEqualTo(new BigDecimal("18.05"));
        assertThat(usageSaved.getGridUsed()).isEqualTo(new BigDecimal("1.076"));
    }

    @Test
    void testCaseProducer() {
        // prepare test data
        var usage = Usage.builder()
                .hour(Instant.parse("2025-01-10T14:00:00Z"))
                .communityProduced(new BigDecimal("18.05"))
                .communityUsed(new BigDecimal("18.02"))
                .gridUsed(new BigDecimal("1.056"))
                .build();

        Mockito.when(usageRepository.findByHour(Instant.parse("2025-01-10T14:00:00Z"))).thenReturn(Optional.of(usage));

        var newEvent = EnergyEvent.builder()
                .type(EnergyEventType.PRODUCER)
                .association("COMMUNITY")
                .kwh(new BigDecimal("0.27"))
                .datetime(Instant.parse("2025-01-10T14:34:00Z"))
                .build();

        // test
        usageService.processProducerEvent(newEvent);

        // assertions
        var argumentCaptor = ArgumentCaptor.forClass(Usage.class);
        Mockito.verify(usageRepository).save(argumentCaptor.capture());

        var usageSaved = argumentCaptor.getValue();
        assertThat(usageSaved.getHour()).isEqualTo(Instant.parse("2025-01-10T14:00:00Z"));
        assertThat(usageSaved.getCommunityProduced()).isEqualTo(new BigDecimal("18.32"));
        assertThat(usageSaved.getCommunityUsed()).isEqualTo(new BigDecimal("18.02"));
        assertThat(usageSaved.getGridUsed()).isEqualTo(new BigDecimal("1.056"));
    }
}
