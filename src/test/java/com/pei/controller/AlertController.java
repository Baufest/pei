package com.pei.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.pei.dto.Alert;
import com.pei.service.AccountService;

@WebMvcTest(AlertController.class)
@ExtendWith(MockitoExtension.class)
public class AlertController {

    @MockitoBean
    private AccountService accountService;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/alerta-cliente-alto-riesgo/{userId} - éxito")
    void validarClienteAltoRiesgo_CuandoOk_RetornaAlerta() throws Exception {
        Long userId = 1L;
        Alert alertaEsperada = new Alert(userId, "Alerta: El cliente es de alto riesgo.");

        when(accountService.validateHighRiskClient(userId)).thenReturn(alertaEsperada);

        mockMvc.perform(get("/api/alerta-cliente-alto-riesgo/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
