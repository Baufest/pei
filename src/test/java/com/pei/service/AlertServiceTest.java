package com.pei.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AlertService alertService;

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
    void shouldReturnAccountsReceivingFromMoreThanTwoDifferentSources() {
        List<Transaction> allTransactions = List.of(
                new Transaction(user1, new BigDecimal("100"), LocalDateTime.now(), acc1, acc2),
                new Transaction(user3, new BigDecimal("200"), LocalDateTime.now(), acc3, acc2),
                new Transaction(user4, new BigDecimal("300"), LocalDateTime.now(), acc4, acc2));

        List<Transaction> recentTransactions = List.of(
                new Transaction(user1, new BigDecimal("100"), LocalDateTime.now(), acc1, acc2),
                new Transaction(user3, new BigDecimal("200"), LocalDateTime.now(), acc3, acc2),
                new Transaction(user4, new BigDecimal("300"), LocalDateTime.now(), acc4, acc2));

        when(transactionService.getLast24HoursTransactions(allTransactions))
                .thenReturn(recentTransactions);

        List<Account> result = alertService.verifyMultipleAccountsCashNotRelated(allTransactions);

        assertEquals(1, result.size());
        assertTrue(result.contains(acc2));
        verify(transactionService).getLast24HoursTransactions(allTransactions); // si se llamo a getLast...
    }

    @Test
    void shouldReturnEmptyListWhenNoAccountsMatchCondition() {
        List<Transaction> allTransactions = List.of(
                new Transaction(user1, new BigDecimal("100"), LocalDateTime.now(), acc1, acc2),
                new Transaction(user2, new BigDecimal("200"), LocalDateTime.now(), acc3, acc1));

        List<Transaction> recentTransactions = List.of(
                new Transaction(user1, new BigDecimal("100"), LocalDateTime.now(), acc1, acc2),
                new Transaction(user2, new BigDecimal("200"), LocalDateTime.now(), acc3, acc1));

        when(transactionService.getLast24HoursTransactions(allTransactions))
                .thenReturn(recentTransactions);

        List<Account> result = alertService.verifyMultipleAccountsCashNotRelated(allTransactions);

        assertTrue(result.isEmpty());
    }
}
