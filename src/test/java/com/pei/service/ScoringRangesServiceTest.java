package com.pei.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.Optional;
import com.pei.domain.ScoringRanges;
import com.pei.repository.ScoringRangesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScoringRangesServiceTest {

    @Mock
    private ScoringRangesRepository scoringRangesRepository;

    @InjectMocks
    private ScoringRangesService scoringRangesService;

    private final String clientType = "PERSONA";
    private ScoringRanges scoringRanges;
    private final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        scoringRanges = new ScoringRanges(400, 700, now.minusDays(1), now.plusDays(1), clientType);
    }

    @Test
    void getScoringColor_CuandoScoringMenorOIgualRedScoreEnd_RetornaRojo() {
        when(scoringRangesRepository.findActiveRangeForDate(eq(clientType), any(LocalDateTime.class)))
                .thenReturn(Optional.of(scoringRanges));

        String color = scoringRangesService.getScoringColor(350, clientType);

        assertEquals("ROJO", color);
        verify(scoringRangesRepository).findActiveRangeForDate(eq(clientType), any(LocalDateTime.class));
    }

    @Test
    void getScoringColor_CuandoScoringEntreRedYYellow_RetornaAmarillo() {
        when(scoringRangesRepository.findActiveRangeForDate(eq(clientType), any(LocalDateTime.class)))
                .thenReturn(Optional.of(scoringRanges));

        String color = scoringRangesService.getScoringColor(500, clientType);

        assertEquals("AMARILLO", color);
        verify(scoringRangesRepository).findActiveRangeForDate(eq(clientType), any(LocalDateTime.class));
    }

    @Test
    void getScoringColor_CuandoScoringMayorYellow_RetornaVerde() {
        when(scoringRangesRepository.findActiveRangeForDate(eq(clientType), any(LocalDateTime.class)))
                .thenReturn(Optional.of(scoringRanges));

        String color = scoringRangesService.getScoringColor(800, clientType);

        assertEquals("VERDE", color);
        verify(scoringRangesRepository).findActiveRangeForDate(eq(clientType), any(LocalDateTime.class));
    }

    @Test
    void getScoringColor_CuandoNoHayRangoActivo_LanzaExcepcion() {
        when(scoringRangesRepository.findActiveRangeForDate(eq(clientType), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> scoringRangesService.getScoringColor(500, clientType));

        assertEquals("No hay rangos de scoring activos para hoy.", ex.getMessage());
        verify(scoringRangesRepository).findActiveRangeForDate(eq(clientType), any(LocalDateTime.class));
    }

    @Test
    void createScoringRange_CuandoNoHaySolapamiento_PersisteYRetornaScoringRange() {
        when(scoringRangesRepository.existsOverlappingRange(eq(clientType), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(false);
        when(scoringRangesRepository.save(any(ScoringRanges.class))).thenReturn(scoringRanges);

        ScoringRanges result = scoringRangesService.createScoringRange(
                400, 700, now.minusDays(1), now.plusDays(1), clientType);

        assertNotNull(result);
        assertEquals(400, result.getRedScoreEnd());
        assertEquals(700, result.getYellowScoreEnd());
        verify(scoringRangesRepository).existsOverlappingRange(eq(clientType), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(scoringRangesRepository).save(any(ScoringRanges.class));
    }

    @Test
    void createScoringRange_CuandoHaySolapamiento_LanzaExcepcion() {
        when(scoringRangesRepository.existsOverlappingRange(eq(clientType), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                scoringRangesService.createScoringRange(400, 700, now.minusDays(1), now.plusDays(1), clientType));

        assertEquals("Ya existe un rango de scoring que se solapa con las fechas proporcionadas.", ex.getMessage());
        verify(scoringRangesRepository).existsOverlappingRange(eq(clientType), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(scoringRangesRepository, never()).save(any(ScoringRanges.class));
    }
}