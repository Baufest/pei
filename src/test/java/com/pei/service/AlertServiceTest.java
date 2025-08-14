package com.pei.service;

import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@SpringBootTest
class AlertServiceTest {
    @InjectMocks
    AlertService alertService;

    @Mock
    TransactionService transactionService;

    User userA, userB, userC;
    Account accountA1, accountA2, accountB1, accountC1;

    @BeforeEach
    void setUp() {
        userA = new User();
        userB = new User();
        userC = new User();
        accountA1 = new Account(userA);
        accountA2 = new Account(userA);
        accountB1 = new Account(userB);
        accountC1 = new Account(userC);

    }

    @Test
    void Should_ReturnMoneyMuleDetected() {
        // Given
        // Simulamos que el usuario A tiene 2 cuentas y el usuario B tiene 1 cuenta
        // y que el usuario A ha realizado depósitos y transferencias en las últimas 24 horas.

        // Depósitos a las Cuentas del Usuario A (A1 y A2) por parte del Usuario B
        Transaction deposit1 = new Transaction(userA, new BigDecimal("5.00"), LocalDateTime.now(), accountB1, accountA1);
        Transaction deposit2 = new Transaction(userA, new BigDecimal("1.00"), LocalDateTime.now(), accountB1, accountA2);

        // Transferencias desde las Cuentas del Usuario A (A1 y A2) a la Cuenta del Usuario C
        Transaction transfer1 = new Transaction(userA, new BigDecimal("20.00"), LocalDateTime.now(), accountA1, accountC1);
        Transaction transfer2 = new Transaction(userA, new BigDecimal("30.00"), LocalDateTime.now(), accountA1, accountB1);

        List<Transaction> transactions = List.of(
            deposit1,
            deposit2,
            transfer1,
            transfer2
        );

        when(transactionService.getLast24HoursTransactions(anyList()))
            .thenReturn(transactions);

        when(transactionService.totalDeposits(anyList()))
            .thenReturn(new BigDecimal("6.00"));

        when(transactionService.totalTransfers(anyList()))
            .thenReturn(new BigDecimal("50.00"));

        // When
        boolean actualResult = alertService.verifyMoneyMule(List.of());

        // Then
        assertTrue(actualResult);
    }

    @Test
    void Should_ReturnNotMoneyMuleDetected() {
        // Given
        // Simulamos que el usuario A tiene 1 cuenta y el usuario B tiene 1 cuenta
        // y que el usuario A ha realizado depósitos y transferencias en las últimas 24 horas.

        // Depósitos a las Cuentas del Usuario A (A1 y A2) por parte del Usuario B
        Transaction deposit1 = new Transaction(userA, new BigDecimal("4.00"), LocalDateTime.now(), accountB1, accountA1);

        // Transferencias desde las Cuentas del Usuario A (A1 y A2) a la Cuenta del Usuario C
        Transaction transfer1 = new Transaction(userA, new BigDecimal("20.00"), LocalDateTime.now(), accountA1, accountC1);

        List<Transaction> transactions = List.of(
            deposit1,
            transfer1
        );

        when(transactionService.getLast24HoursTransactions(anyList()))
            .thenReturn(transactions);

        when(transactionService.totalDeposits(anyList()))
            .thenReturn(new BigDecimal("4.00"));

        when(transactionService.totalTransfers(anyList()))
            .thenReturn(new BigDecimal("20.00"));

        // When
        boolean actualResult = alertService.verifyMoneyMule(List.of());

        // Then
        assertFalse(actualResult);
    }

    @Test
    void Should_ReturnNotMoneyMuleDetected_When_NotContainsLast24HsTransactions() {
        // Given
        // Simulamos que el usuario A tiene 1 cuenta y el usuario B tiene 1 cuenta
        // y que el usuario A ha realizado depósitos y transferencias en las últimas 24 horas.

        // Depósitos a las Cuentas del Usuario A (A1 y A2) por parte del Usuario B
        Transaction deposit1 = new Transaction(userA, new BigDecimal("6.00"), LocalDateTime.now().minusDays(3), accountB1, accountA1);

        // Transferencias desde las Cuentas del Usuario A (A1 y A2) a la Cuenta del Usuario C
        Transaction transfer1 = new Transaction(userA, new BigDecimal("50.00"), LocalDateTime.now().minusDays(7), accountA1, accountC1);

        List<Transaction> transactions = List.of(
            deposit1,
            transfer1
        );

        when(transactionService.getLast24HoursTransactions(anyList()))
            .thenReturn(List.of());

        when(transactionService.totalDeposits(anyList()))
            .thenReturn(new BigDecimal("6.00"));

        when(transactionService.totalTransfers(anyList()))
            .thenReturn(new BigDecimal("50.00"));

        // When
        boolean actualResult = alertService.verifyMoneyMule(List.of());

        // Then
        assertFalse(actualResult);
    }
}
