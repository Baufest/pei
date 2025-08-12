package com.pei.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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
            Alert alertaEsperada = new Alert("Alerta de prueba");
    
            when(accountService.validarTransferenciasCuentasRecienCreadas(any(Account.class), any(Transaction.class)))
                    .thenReturn(alertaEsperada);
    
            String cuentaJson = objectMapper.writeValueAsString(cuenta);
            String transaccionJson = objectMapper.writeValueAsString(transaccion);
            String requestBody = "{" +
                    "\"cuentaDestino\":" + cuentaJson + "," +
                    "\"transaccionActual\":" + transaccionJson +
                    "}";
    
            
            mockMvc.perform(post("/api/alerta-cuenta-nueva")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isOk());
        }
    }

}
