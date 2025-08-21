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

import com.pei.domain.*;
import com.pei.domain.User.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pei.dto.Alert;
import com.pei.repository.LoginRepository;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // no olvidar
class GeolocalizationServiceTest {

    @Mock
    private GeoSimService geoSimService;

    @Mock
    private LoginRepository loginRepository;

    @InjectMocks
    private GeolocalizationService geolocalizationService;

    private Login login;
    private User user;
    private Device device;

    @BeforeEach
    void setUp() {
        // Creamos el User con ID 1L
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        // Creamos el Device
        device = new Device();
        device.setDeviceID("DEVICE-123");
        device.setUser(user);

        // Creamos el Login usando el constructor de 6 argumentos
        login = new Login(
            1L,          // ID del login
            user,        // User
            device,      // Device
            "Canada",    // country
            LocalDateTime.now(), // loginTime
            true         // success
        );

    }

    @Test
    void shouldReturnFraudAlertWhenNoPreviousLoginFound() {
        // previo
        when(geoSimService.getCountryFromIP("Canada")).thenReturn("Canada");
        when(loginRepository.findLoginByUserAndCountryAndDevice(1L, "Canada", "DEVICE-123", true))
            .thenReturn(List.of()); // lista vacía
        when(loginRepository.findAll()).thenReturn(List.of(
            new Login(50L, user, device, "Canada", LocalDateTime.now(), true)
        ));

        // act
        Alert alert = geolocalizationService.verifyFraudOfDeviceAndGeolocation(login);

        // checks
        assertEquals(1L, alert.userId());
        assertEquals("Device and geolocalization problem detected for 1", alert.description());
        verify(loginRepository).save(any(Login.class));
    }

    @Test
    void shouldAssignIdOneWhenNoLoginExistInRepository() {
        // previo
        when(geoSimService.getCountryFromIP("Canada")).thenReturn("Canada");
        when(loginRepository.findLoginByUserAndCountryAndDevice(anyLong(), anyString(), anyString(),
            anyBoolean()))
            .thenReturn(List.of());
        when(loginRepository.findAll()).thenReturn(List.of()); // lista vacía

        // act
        geolocalizationService.verifyFraudOfDeviceAndGeolocation(login);

        // checks
        ArgumentCaptor<Login> captor = ArgumentCaptor.forClass(Login.class);
        verify(loginRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getUser().getId());
    }

    @Test
    void shouldAssignIdAsLastPlusOneWhenLoginExist() {
        // previo
        when(geoSimService.getCountryFromIP("Canada")).thenReturn("Canada");
        when(loginRepository.findLoginByUserAndCountryAndDevice(anyLong(), anyString(), anyString(),
            anyBoolean()))
            .thenReturn(List.of());
        when(loginRepository.findAll()).thenReturn(List.of(
            new Login(10L, user, device, "Canada", LocalDateTime.now(), true)));

        // act
        geolocalizationService.verifyFraudOfDeviceAndGeolocation(login);

        // checks
        ArgumentCaptor<Login> captor = ArgumentCaptor.forClass(Login.class);
        verify(loginRepository).save(captor.capture());
        assertEquals(1L, captor.getValue().getUser().getId());
    }

    /* CAMBIAR LLAMADO A NEW FALTA VARIABLE SUCCESS */
        /* @Test
        void givenDifferentCountries_thenReturnsAlert() {
                List<Logins> logins = Arrays.asList(
                                new Logins(1L, null, "Argentina", LocalDateTime.now()),
                                new Logins(1L, null, "Chile", LocalDateTime.now().minusMinutes(10)));

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
                                new Logins(1L, null, "Argentina", LocalDateTime.now().minusMinutes(20)));

                when(loginsRepository.findRecentLogins(eq(1L), any(LocalDateTime.class)))
                                .thenReturn(logins);

                Alert alert = geolocalizationService.getLoginAlert(1L);

                assertNull(alert);
        } */

    @Test
    void givenNoLogins_thenReturnsNull() {
        when(loginRepository.findRecentLogin(eq(1L), any(LocalDateTime.class)))
            .thenReturn(Collections.emptyList());

        Alert alert = geolocalizationService.getLoginAlert(1L);

        assertNull(alert);
        verify(loginRepository, times(1))
            .findRecentLogin(eq(1L), any(LocalDateTime.class));
    }

}
