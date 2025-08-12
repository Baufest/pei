package com.pei.service;

import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {
    TransactionService transactionService;
    User user1, user2;
    Account account1, account2;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService();
        user1 = new User(1L);
        user2 = new User(2L);
        account1 = new Account(1L, user1);
        account2 = new Account(2L, user2);
    }

    @Test
    void Should_GetLast24HoursTransactions_Correct() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Transaction t1 = new Transaction(user1, new BigDecimal("100.00"), now.minusHours(2), account1, account1); // dentro de 24h
        Transaction t2 = new Transaction(user1, new BigDecimal("200.00"), now.minusDays(2), account1, account1); // fuera de 24h
        Transaction t3 = new Transaction(user2, new BigDecimal("300.00"), now.minusHours(10), account2, account2); // dentro de 24h
        List<Transaction> transactions = List.of(t1, t2, t3);

        List<Transaction> expected = List.of(t1, t3);
        // When
        List<Transaction> actual = transactionService.getLast24HoursTransactions(transactions);
        // Then

        assertArrayEquals(expected.toArray(), actual.toArray());
    }

    @Test
    void Should_GetLast24HoursTransactions_EmptyList() {
        List<Transaction> transactions = new ArrayList<>();
        List<Transaction> result = transactionService.getLast24HoursTransactions(transactions);
        assertTrue(result.isEmpty());
    }

    @Test
    void Should_ReturnTotalDeposits_When_ContainsDeposits() {
        // Deposito: destinationAccount.owner == user
        Transaction deposito1 = new Transaction(user1, new BigDecimal("100.00"), LocalDateTime.now(), account2, account1);
        Transaction deposito2 = new Transaction(user1, new BigDecimal("50.00"), LocalDateTime.now(), account2, account1);
        // Transferencia: destinationAccount.owner != user
        Transaction transferencia = new Transaction(user1, new BigDecimal("200.00"), LocalDateTime.now(), account1, account2);
        List<Transaction> transactions = List.of(deposito1, deposito2, transferencia);
        BigDecimal total = transactionService.totalDeposits(transactions);
        assertEquals(new BigDecimal("150.00"), total);
    }

    @Test
    void Should_ReturnZeroDeposits_When_EmptyTransactions() {
        BigDecimal total = transactionService.totalDeposits(new ArrayList<>());
        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void Should_ReturnZeroDeposits_When_NotContainsDeposits() {
        Transaction transferencia = new Transaction(user1, new BigDecimal("200.00"), LocalDateTime.now(), account1, account2);
        List<Transaction> transactions = List.of(transferencia);
        BigDecimal total = transactionService.totalDeposits(transactions);
        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void Should_ReturnTotalTransfers_When_ContainsTransfers() {
        // Transferencia: destinationAccount.owner != user
        Transaction transferencia1 = new Transaction(user1, new BigDecimal("200.00"), LocalDateTime.now(), account1, account2);
        Transaction transferencia2 = new Transaction(user1, new BigDecimal("50.00"), LocalDateTime.now(), account1, account2);
        // Deposito: destinationAccount.owner == user
        Transaction deposito = new Transaction(user1, new BigDecimal("100.00"), LocalDateTime.now(), account2, account1);
        List<Transaction> transactions = List.of(transferencia1, transferencia2, deposito);
        BigDecimal total = transactionService.totalTransfers(transactions);
        assertEquals(new BigDecimal("250.00"), total);
    }

    @Test
    void Should_ReturnZeroTransfers_When_EmptyTransfers() {
        BigDecimal actualTotal = transactionService.totalTransfers(new ArrayList<>());
        assertEquals(BigDecimal.ZERO, actualTotal);
    }

    @Test
    void Should_ReturnZeroTransfers_When_NotContainsTransfers() {
        Transaction deposito = new Transaction(user1, new BigDecimal("100.00"), LocalDateTime.now(), account2, account1);
        List<Transaction> transactions = List.of(deposito);
        BigDecimal actualTotal = transactionService.totalTransfers(transactions);
        assertEquals(BigDecimal.ZERO, actualTotal);
    }
}
