package com.pei.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    TransactionService transactionService;

    @Autowired
    TransactionService service;

    User user, user2;

    @BeforeEach
    void setup() {
        user = new User(1L);
        user2 = new User(2L);
    }

    @Test
    void shouldReturnOnlyTransactionsInLast24Hours() {

        Transaction recentTransaction = new Transaction(user, new BigDecimal(200L),
                LocalDateTime.now().minusHours(5),
                new Account(1L, user),
                new Account(2L, user2));

        Transaction oldTransaction = new Transaction(user, new BigDecimal(200L),
                LocalDateTime.now().minusHours(25),
                new Account(1L, user),
                new Account(2L, user2));

        List<Transaction> result = service.getLast24HoursTransactions(List.of(recentTransaction, oldTransaction));

        assertEquals(1, result.size());
        assertTrue(result.contains(recentTransaction));
    }

    @Test
    void Should_GetLast24HoursTransactions_EmptyList() {
        List<Transaction> transactions = new ArrayList<>();
        List<Transaction> result = transactionService.getLast24HoursTransactions(transactions);
        assertTrue(result.isEmpty());
    }
}
