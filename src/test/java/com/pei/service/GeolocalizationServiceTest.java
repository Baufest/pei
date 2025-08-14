package com.pei.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pei.dto.Alert;
import com.pei.dto.Logins;
import com.pei.repository.LoginsRepository;

@ExtendWith(MockitoExtension.class) // no olvidar
public class GeolocalizationServiceTest {
 
    @Mock
    private GeoSimService geoSimService;

    @Mock
    private LoginsRepository loginsRepository;

    @InjectMocks
    private GeolocalizationService geolocalizationService;

    private Logins login;

    @BeforeEach
    void setUp() {
        login = new Logins(
                100L,
                1L,
                "DEVICE-123",
                "Canada",
                LocalDateTime.now(),
                true
        );
    }

    @Test
    void shouldReturnFraudAlertWhenNoPreviousLoginsFound() {
        // previo
        when(geoSimService.getCountryFromIP("Canada")).thenReturn("Canada");
        when(loginsRepository.findLoginsByUserAndCountryAndDevice(1L, "Canada", "DEVICE-123", true))
                .thenReturn(List.of()); // lista vacía
        when(loginsRepository.findAll()).thenReturn(List.of(
                new Logins(50L, 2L, "OTHER", "Canada", LocalDateTime.now(), true)
        ));

        // act
        Alert alert = geolocalizationService.verifyFraudOfDeviceAndGeolocation(login);

        // checks
        assertEquals(1L, alert.userId());
        assertEquals("Device and geolocalization problem detected for 1", alert.description());
        verify(loginsRepository).save(any(Logins.class));
    }

    @Test
    void shouldReturnNoFraudAlertWhenPreviousLoginsExist() {
        // previo
        when(geoSimService.getCountryFromIP("Canada")).thenReturn("Canada");
        when(loginsRepository.findLoginsByUserAndCountryAndDevice(1L, "Canada", "DEVICE-123", true))
                .thenReturn(List.of(login)); // lista con un login
        when(loginsRepository.findAll()).thenReturn(List.of(
                new Logins(50L, 2L, "OTHER", "Canada", LocalDateTime.now(), true)
        ));

        // act
        Alert alert = geolocalizationService.verifyFraudOfDeviceAndGeolocation(login);

        // checksa
        assertEquals(0L, alert.userId());
        assertEquals("Something else", alert.description());
        verify(loginsRepository).save(any(Logins.class));
    }

    @Test
    void shouldAssignIdOneWhenNoLoginsExistInRepository() {
        // previo
        when(geoSimService.getCountryFromIP("Canada")).thenReturn("Canada");
        when(loginsRepository.findLoginsByUserAndCountryAndDevice(anyLong(), anyString(), anyString(), anyBoolean()))
                .thenReturn(List.of());
        when(loginsRepository.findAll()).thenReturn(List.of()); // lista vacía

        // act
        geolocalizationService.verifyFraudOfDeviceAndGeolocation(login);

        // checks
        ArgumentCaptor<Logins> captor = ArgumentCaptor.forClass(Logins.class);
        verify(loginsRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().id());
    }

    @Test
    void shouldAssignIdAsLastPlusOneWhenLoginsExist() {
        // previo
        when(geoSimService.getCountryFromIP("Canada")).thenReturn("Canada");
        when(loginsRepository.findLoginsByUserAndCountryAndDevice(anyLong(), anyString(), anyString(), anyBoolean()))
                .thenReturn(List.of());
        when(loginsRepository.findAll()).thenReturn(List.of(
                new Logins(10L, 2L, "OTHER", "Canada", LocalDateTime.now(), true)
        ));

        // act
        geolocalizationService.verifyFraudOfDeviceAndGeolocation(login);

        // checks
        ArgumentCaptor<Logins> captor = ArgumentCaptor.forClass(Logins.class);
        verify(loginsRepository).save(captor.capture());
        assertEquals(11L, captor.getValue().id());
    }
}
