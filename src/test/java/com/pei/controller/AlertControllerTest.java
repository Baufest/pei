package com.pei.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import com.pei.service.ClienteService;
import com.pei.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import com.pei.dto.Alert;
import com.pei.service.AccountService;
import com.pei.service.AlertService;

@WebMvcTest(AlertController.class)
@ExtendWith(MockitoExtension.class)
public class AlertControllerTest {

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private AlertService alertService;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void Should_ReturnEmailAlert_When_CriticalityIsHigh() throws Exception {
        // Given
        User originUser = new User();
        originUser.setName("Juan");
        originUser.setEmail("juan@mail.com");

        User destinationUser = new User();
        destinationUser.setName("Pepo");
        destinationUser.setEmail("pepo@mail.com");

        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(2_000_000));
        transaction.setUser(originUser);
        transaction.setDestinationAccount(new Account(destinationUser));

        // When
        when(alertService.alertCriticality(any(Transaction.class)))
                .thenReturn(new Alert(10L,
                        "Transacción de alta criticidad. Se notificará por Mail."));

        // Then
        mockMvc.perform(post("/api/alerta-canales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(
                        jsonPath("$.description").value(
                                "Transacción de alta criticidad. Se notificará por Mail."))
                .andDo(print());

        verify(alertService).alertCriticality(any(Transaction.class));
    }

    @Test
    void Should_ReturnNotFound_When_CriticalityIsLow() throws Exception {
        when(alertService.alertCriticality(any(Transaction.class))).thenReturn(null);

        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(1000)); // monto bajo

        mockMvc.perform(post("/api/alerta-canales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(alertService).alertCriticality(any(Transaction.class));
    }

    @Test
    void checkProccesTransaction_CuandoTransaccionExitosa_RetornaResponseOk() throws Exception {
        // Arrange
        Long idCliente = 1L;
        Alert alertaMock = new Alert(idCliente,
                "Alerta: Transaccion aprobada para cliente " + idCliente
                        + " con scoring de: 90");

        when(transactionService.processTransaction(idCliente)).thenReturn(alertaMock);

        mockMvc.perform(post("/api/alerta-scoring")
                .contentType(MediaType.APPLICATION_JSON)
                .content("1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(idCliente))
                .andExpect(jsonPath("$.description").value(
                        "Alerta: Transaccion aprobada para cliente " + idCliente
                                + " con scoring de: 90"));
    }

    @Test
    void checkProccesTransaction_CuandoAlertNull_RetornaNotFound() throws Exception {
        // Arrange
        Long idCliente = 2L;
        when(transactionService.processTransaction(anyLong()))
                .thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/api/alerta-scoring")
                .contentType(MediaType.APPLICATION_JSON)
                .content("2"))
                .andExpect(status().isNotFound());

        verify(transactionService).processTransaction(idCliente);
    }

}
