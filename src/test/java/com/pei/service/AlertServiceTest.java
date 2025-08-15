package com.pei.service;

import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;


@ExtendWith(MockitoExtension.class)
class AlertServiceTest {
    @InjectMocks
    AlertService alertService;

    @Mock
    TransactionService transactionService;

    @Nested
    @DisplayName("Test sobre la validación y detección de Money Mule")
    class MoneyMuleTest {
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

            List<Transaction> transactions = List.of( // TODO:Unused
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

    @Nested
    @DisplayName("Red de Transferencias Fraude Test")
    class RedTransferenciasFraudeServiceTest {
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
}
