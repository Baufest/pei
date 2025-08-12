package com.pei.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import com.pei.dto.Alert;
import com.pei.service.AlertService;
import com.pei.service.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AlertControllerTest {

    AlertController controller;
    User user, user2, user3, user4;

    @BeforeEach
    void setup() {
        user = new User(1L);
        user2 = new User(2L);
        user3 = new User(3L);
        user4 = new User(4L);

        controller = new AlertController(
                new AlertService(new TransactionService()));
    }

    @Test
    void shouldReturnAlert_When_AccountsFound() {
        Account destino = new Account(1L, user);

        Transaction t1 = new Transaction(user, new BigDecimal(200L),
                LocalDateTime.now(),
                new Account(2L, user2),
                destino);
        Transaction t2 = new Transaction(user, new BigDecimal(200L),
                LocalDateTime.now(),
                new Account(3L, user3),
                destino);
        Transaction t3 = new Transaction(user, new BigDecimal(200L),
                LocalDateTime.now(),
                new Account(4L, user4),
                destino);

        Alert alertExpected = new Alert(user.getId(),
                "Alert: Multiples transactions not related to the account of " + user.getId() + " detected");

        ResponseEntity<Alert> alertResponse = controller.checkMultipleAccountsCashNotRelated(List.of(t1, t2, t3));

        assertEquals(HttpStatus.OK, alertResponse.getStatusCode());
        assertNotNull(alertResponse.getBody());
        assertEquals(user.getId(), alertResponse.getBody().userId());
        assertEquals(alertExpected,
                alertResponse.getBody());
    }

    @Test
    void shouldReturnNotFound_When_NoAccountsFound() {
        Account destino = new Account(1L, user);

        Transaction t1 = new Transaction(user, new BigDecimal(200L),
                LocalDateTime.now(),
                new Account(2L, user2),
                destino);
        Transaction t2 = new Transaction(user, new BigDecimal(200L),
                LocalDateTime.now(),
                new Account(3L, user3),
                destino);

        ResponseEntity<Alert> alertResponse = controller.checkMultipleAccountsCashNotRelated(List.of(t1, t2));

        assertEquals(HttpStatus.NOT_FOUND, alertResponse.getStatusCode());
        assertFalse(alertResponse.hasBody());
    }
}
