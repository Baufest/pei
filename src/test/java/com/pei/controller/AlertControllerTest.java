package com.pei.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
import com.pei.dto.UserTransaction;
import com.pei.service.AccountService;
import com.pei.service.AlertService;


@WebMvcTest(AlertController.class)
class AlertControllerTest {

    @MockitoBean
    private AlertService service;

    @MockitoBean
    private AccountService accountService;

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

        mockMvc.perform(post("/api/alerta-money-mule")
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

        mockMvc.perform(post("/api/alerta-money-mule")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))// Simular una solicitud POST con un cuerpo vacío, ya que esta Mockeado el Service
            .andExpect(status().isNotFound())
            .andDo(print());
    }


    //TEST GONZA
    @Nested
    @DisplayName("Tests para validarTransferenciasCuentasRecienCreadas")
    class ValidarTransferenciasCuentasRecienCreadasTests {

        @BeforeEach
        void setUp() {
            objectMapper = new ObjectMapper();
        }

        @Test
        @DisplayName("POST /api/alerta-cuenta-nueva - éxito")
        void validarTransferenciasCuentasRecienCreadas_CuandoOk_RetornaAlerta() throws Exception {
            Account cuenta = new Account();
            Transaction transaccion = new Transaction();
            Alert alertaEsperada = new Alert(null, "Alerta de prueba");

            when(accountService.validateNewAccountTransfers(any(Account.class), any(Transaction.class)))
                    .thenReturn(alertaEsperada);

            String cuentaJson = objectMapper.writeValueAsString(cuenta);
            String transaccionJson = objectMapper.writeValueAsString(transaccion);
            String requestBody = "{" +
                    "\"destinationAccount\":" + cuentaJson + "," +
                    "\"currentTransaction\":" + transaccionJson +
                    "}";

            mockMvc.perform(post("/api/alerta-cuenta-nueva")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk());
        }

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
        @DisplayName("POST /api/alerta-perfil - éxito")
        void validateUserProfileTransaction() throws Exception {
            UserTransaction userTransaction = new UserTransaction();
            userTransaction.setTransaction(new Transaction());
            userTransaction.setUser(new User());

            Alert alertaEsperada = new Alert(null, "Perfil de usuario validado para la transacción.");

            when(accountService.validateUserProfileTransaction(any(User.class), any(Transaction.class)))
                    .thenReturn(alertaEsperada);

            mockMvc.perform(post("/api/alerta-perfil")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(userTransaction)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.description").value("Perfil de usuario validado para la transacción."));

        }
    }
}
