package com.pei.service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pei.domain.Scoring;
import com.pei.repository.ScoringRepository;

@ExtendWith(MockitoExtension.class)
class ScoringServiceTest {

    @Mock
    private ScoringRepository scoringRepository;

    @InjectMocks
    private ScoringService scoringService;

    @Test
    void getScoringColorBasedInUserScore_CuandoExisteScoring_RetornaColor() {
        LocalDateTime now = LocalDateTime.now();
        Scoring scoring = new Scoring("Verde", 70, 100, now.minusDays(1), now.plusDays(1));
        when(scoringRepository.findByStartDateBeforeAndEndDateAfterAndScoreStartLessThanEqualAndScoreEndGreaterThanEqual(
                any(), any(), eq(80), eq(80)))
            .thenReturn(Optional.of(scoring));

        String color = scoringService.getScoringColorBasedInUserScore(80);

        assertEquals("Verde", color);
        verify(scoringRepository).findByStartDateBeforeAndEndDateAfterAndScoreStartLessThanEqualAndScoreEndGreaterThanEqual(any(), any(), eq(80), eq(80));
    }

    @Test
    void getScoringColorBasedInUserScore_CuandoNoExisteScoring_RetornaSinScoring() {
        when(scoringRepository.findByStartDateBeforeAndEndDateAfterAndScoreStartLessThanEqualAndScoreEndGreaterThanEqual(
                any(), any(), anyInt(), anyInt()))
            .thenReturn(Optional.empty());

        String color = scoringService.getScoringColorBasedInUserScore(50);

        assertEquals("Sin scoring asignado", color);
    }

    @Test
    void createPeriodScorings_CuandoNoSolapa_RetornaOk() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(30);

        when(scoringRepository.existsByColorAndStartDateLessThanEqualAndEndDateGreaterThanEqual(anyString(), any(), any()))
            .thenReturn(false);

        scoringService.createPeriodScorings(start, end, 0, 49, 50, 69, 70, 100);

        verify(scoringRepository, times(3)).save(any(Scoring.class));
    }

    @Test
    void createPeriodScorings_CuandoSolapa_ArrojaExcepcion() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(30);

        when(scoringRepository.existsByColorAndStartDateLessThanEqualAndEndDateGreaterThanEqual(eq("Rojo"), any(), any()))
            .thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            scoringService.createPeriodScorings(start, end, 0, 49, 50, 69, 70, 100)
        );

        assertTrue(ex.getMessage().contains("Rojo"));
        verify(scoringRepository, never()).save(any(Scoring.class));
    }
}