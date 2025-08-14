package com.pei.controller;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pei.dto.Alert;
import com.pei.dto.Logins;
import com.pei.repository.LoginsRepository;
import com.pei.service.GeoSimService;
import com.pei.service.GeolocalizationService;


@WebMvcTest(AlertController.class)
@ExtendWith(MockitoExtension.class)
public class AlertControllerTest {

        @MockitoBean
        private GeolocalizationService geolocalizationService;

        @MockitoBean
        private GeoSimService geoSimService;

        @Mock
        private LoginsRepository loginsRepository;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        MockMvc mockMvc;

        @Nested
        @DisplayName("Tests para validar fraude geolocalizacion y dispositivo")
        class ValidarFraudeGeoDisp {

                /* solo probamos el controller que funcione corretamente */
                @Test
                void shouldReturnAlertWhenNoPreviousLoginsFound() throws Exception {
                        Logins login = new Logins(1L, 1L, "qwertasdfgh", "Canada", LocalDateTime.now(), true);

                        when(geolocalizationService.verifyFraudOfDeviceAndGeolocation(login))
                                        .thenReturn(new Alert(login.userId(),
                                                        "Device and geolocalization problem detected for "
                                                                        + login.userId()));

                        mockMvc.perform(post("/api/alerta-dispositivo")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(login)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.userId").value(1))
                                        .andExpect(jsonPath("$.description")
                                                        .value("Device and geolocalization problem detected for 1"));
                }

                /* probamos que devuelva something else, correctamente */
                @Test
                void shouldReturnAlertOkWhenPreviousLoginsFound() throws Exception {
                        Logins login = new Logins(1L, 1L, "qwertasdfgh", "Canada", LocalDateTime.now(), true);

                        when(geolocalizationService.verifyFraudOfDeviceAndGeolocation(login))
                                        .thenReturn(new Alert(1L, "Something Else"));

                        mockMvc.perform(post("/api/alerta-dispositivo")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(login)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.userId").value(1))
                                        .andExpect(jsonPath("$.description")
                                                        .value("Something Else"));
                }

                @Test
                void shouldReturn404WhenLoginNull() throws Exception {
                        Logins login = null;

                        mockMvc.perform(post("/api/alerta-dispositivo")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(login)))
                                        .andExpect(status().isBadRequest());
                }
        }
}
