package com.pei.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import com.pei.service.AlertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlertController.class)
class AlertControllerTest {

    @MockitoBean
    private AlertService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    void Should_ReturnOkAlert_When_MoneyMuleDetected() throws Exception {
        // Aquí puedes simular el comportamiento del servicio y probar la lógica del controlador
        // Simular el comportamiento del servicio
        User user = new User(1L);
        Account account = new Account(1L, user);
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> inputTransactions = List.of(new Transaction(user, new BigDecimal("100.00"), now.minusHours(2), account, account));

        when(service.verifyMoneyMule(anyList())).thenReturn(true);

        mockMvc.perform(post("/alerta-money-mule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputTransactions))) // Simular una solicitud POST
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.description").value("Alerta: Posible Money Mule detectado del usuario 1"))
            .andDo(print());
    }

    @Test
    void Should_ReturnNotContent_When_MoneyMuleNotDetected() throws Exception {
        // Aquí puedes simular el comportamiento del servicio y probar la lógica del controlador
        // Simular el comportamiento del servicio
        when(service.verifyMoneyMule(anyList())).thenReturn(false);

        mockMvc.perform(post("/alerta-money-mule")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))// Simular una solicitud POST con un cuerpo vacío, ya que esta Mockeado el Service
            .andExpect(status().isNotFound())
            .andDo(print());
    }
}
