package com.pei.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import com.pei.domain.ScoringRanges;
import com.pei.service.ScoringRangesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ScoringRangesControllerTest {

    @Mock
    private ScoringRangesService scoringRangesService;

    @InjectMocks
    private ScoringRangesController scoringRangesController;

    private final String startDateStr = "2025-08-14T00:00:00";
    private final String endDateStr = "2025-09-14T00:00:00";
    private final Integer rojoEnd = 49;
    private final Integer amarilloEnd = 69;
    private final String clientType = "VIP";

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDateTime.parse(startDateStr);
        endDate = LocalDateTime.parse(endDateStr);
    }

    @Test
    void createScoringRange_CuandoDatosValidos_RetornaOkConEntidad() {
        ScoringRanges scoringRanges = mock(ScoringRanges.class);
        when(scoringRangesService.createScoringRange(rojoEnd, amarilloEnd, startDate, endDate, clientType))
                .thenReturn(scoringRanges);

        ResponseEntity<ScoringRanges> response = scoringRangesController.createScoringRange(
                startDateStr, endDateStr, rojoEnd, amarilloEnd, clientType);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(scoringRanges, response.getBody());
        verify(scoringRangesService).createScoringRange(rojoEnd, amarilloEnd, startDate, endDate, clientType);
    }

    @Test
    void createScoringRange_CuandoServicioLanzaIllegalArgumentException_Retorna409() {
        when(scoringRangesService.createScoringRange(rojoEnd, amarilloEnd, startDate, endDate, clientType))
                .thenThrow(new IllegalArgumentException("Invalid range"));

        ResponseEntity<ScoringRanges> response = scoringRangesController.createScoringRange(
                startDateStr, endDateStr, rojoEnd, amarilloEnd, clientType);

        assertEquals(409, response.getStatusCode().value());
        assertNull(response.getBody());
        verify(scoringRangesService).createScoringRange(rojoEnd, amarilloEnd, startDate, endDate, clientType);
    }
}