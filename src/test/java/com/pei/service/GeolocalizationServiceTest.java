package com.pei.service;

import com.pei.dto.Alert;
import com.pei.dto.Logins;
import com.pei.repository.LoginsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GeolocalizationServiceTest {

    private LoginsRepository loginsRepository;
    private GeolocalizationService geolocalizationService;

    @BeforeEach
    void setUp() {
        loginsRepository = mock(LoginsRepository.class);
        geolocalizationService = new GeolocalizationService(loginsRepository);
    }

    @Test
    void givenDifferentCountries_thenReturnsAlert() {
        List<Logins> logins = Arrays.asList(
                new Logins(1L, null, "Argentina", LocalDateTime.now()),
                new Logins(1L, null, "Chile", LocalDateTime.now().minusMinutes(10))
        );

        when(loginsRepository.findRecentLogins(eq(1L), any(LocalDateTime.class)))
                .thenReturn(logins);

        Alert alert = geolocalizationService.getLoginAlert(1L);

        assertNotNull(alert);
        assertEquals(1L, alert.userId());
        assertEquals("Multiple countries logins detected for user 1", alert.description());
    }

    @Test
    void givenSameCountry_thenReturnsNull() {
        List<Logins> logins = Arrays.asList(
                new Logins(1L, null, "Argentina", LocalDateTime.now()),
                new Logins(1L, null, "Argentina", LocalDateTime.now().minusMinutes(20))
        );

        when(loginsRepository.findRecentLogins(eq(1L), any(LocalDateTime.class)))
                .thenReturn(logins);

        Alert alert = geolocalizationService.getLoginAlert(1L);

        assertNull(alert);
    }

    @Test
    void givenNoLogins_thenReturnsNull() {
        when(loginsRepository.findRecentLogins(eq(1L), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        Alert alert = geolocalizationService.getLoginAlert(1L);

        assertNull(alert);
        verify(loginsRepository, times(1))
                .findRecentLogins(eq(1L), any(LocalDateTime.class));
    }
}
