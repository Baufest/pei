package com.pei.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.dto.Alert;
import com.pei.service.AccountService;

@ExtendWith(MockitoExtension.class)
class AlertControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AlertController alertController;


    @Nested
    @DisplayName("Tests para validarTransferenciasCuentasRecienCreadas")
    class ValidarTransferenciasCuentasRecienCreadasTests {

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(alertController).build();
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
    }

}
