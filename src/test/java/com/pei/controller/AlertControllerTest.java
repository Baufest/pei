package com.pei.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import com.pei.service.AlertService;
import com.pei.service.TransactionService;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlertController.class)
@ExtendWith(MockitoExtension.class)
class AlertControllerTest {

    @MockitoBean
    private AlertService alertService;

    @MockitoBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Nested
    @DisplayName("Test para validar Fraude en Red de Transacciones")
    class ValidarFraudeRedTransferencias {
        private User user1, user2, user3, user4;
        private Account acc1, acc2, acc3, acc4;

        @BeforeEach
        void setUp() {
            user1 = new User(1L);
            user2 = new User(2L);
            user3 = new User(3L);
            user4 = new User(4L);

            acc1 = new Account(1L, user1);
            acc2 = new Account(2L, user2);
            acc3 = new Account(3L, user3);
            acc4 = new Account(4L, user4);
        }

        @Test
        void shouldReturnAlertWhenAccountFound() throws Exception {
            Account destino = new Account(1L, user1);

            List<Transaction> allTransactions = List.of(
                    new Transaction(user2, new BigDecimal("100"), LocalDateTime.now(), acc2, acc1),
                    new Transaction(user3, new BigDecimal("200"), LocalDateTime.now(), acc3, acc1),
                    new Transaction(user4, new BigDecimal("300"), LocalDateTime.now(), acc4, acc1));

            when(alertService.verifyMultipleAccountsCashNotRelated(allTransactions))
                    .thenReturn(List.of(destino));

            mockMvc.perform(post("/api/alerta-red-transacciones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(allTransactions)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(1))
                    .andExpect(jsonPath("$.description")
                            .value("Alert: Multiples transactions not related to the account of 1 detected"));

            verify(alertService, times(1)).verifyMultipleAccountsCashNotRelated(anyList());
        }

        @Test
        void shouldReturnBadRequestWhenNotListParameter() throws Exception {
            when(alertService.verifyMultipleAccountsCashNotRelated(List.of()))
                    .thenReturn(List.of());

            mockMvc.perform(post("/api/alerta-red-transacciones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(List.of())))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnNotFoundWhenAccountNotFound() throws Exception {

            List<Transaction> allTransactions = List.of(
                    new Transaction(user2, new BigDecimal("100"), LocalDateTime.now(), acc2, acc1),
                    new Transaction(user3, new BigDecimal("200"), LocalDateTime.now(), acc3, acc4),
                    new Transaction(user4, new BigDecimal("300"), LocalDateTime.now(), acc4, acc2));

            when(alertService.verifyMultipleAccountsCashNotRelated(allTransactions))
                    .thenReturn(List.of());

            mockMvc.perform(post("/api/alerta-red-transacciones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(allTransactions)))
                    .andExpect(status().isNotFound());
        }
    }
}
