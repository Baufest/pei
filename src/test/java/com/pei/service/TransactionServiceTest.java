package com.pei.service;

import com.pei.dto.Alert;
import com.pei.repository.TransactionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {


    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionVelocityDetectorService transactionVelocityDetectorService;
    @InjectMocks
    private TransactionService transactionService;

    @Test
    void getFastMultipleTransactionAlert_Individuo_ExceedsMax_ReturnsAlert() {
        Long userId = 1L;
        String clientType = "individuo";
        int minutesRange = 10;
        int maxTransactions = 5;
        int numTransactions = 6;

        when(transactionVelocityDetectorService.getIndividuoMinutesRange()).thenReturn(minutesRange);
        when(transactionVelocityDetectorService.getIndividuoMaxTransactions()).thenReturn(maxTransactions);
        when(transactionRepository.countTransactionsFromDate(eq(userId), any(LocalDateTime.class))).thenReturn(numTransactions);

        Alert alert = transactionService.getFastMultipleTransactionAlert(userId, clientType);

        assertNotNull(alert);
        assertEquals(userId, alert.userId());
        assertTrue(alert.description().contains("Fast multiple transactions detected"));
        verify(transactionVelocityDetectorService).getIndividuoMinutesRange();
        verify(transactionVelocityDetectorService).getIndividuoMaxTransactions();
        verify(transactionRepository).countTransactionsFromDate(eq(userId), any(LocalDateTime.class));
    }

    @Test
    void getFastMultipleTransactionAlert_Individuo_NotExceedsMax_ReturnsNull() {
        Long userId = 1L;
        String clientType = "individuo";
        int minutesRange = 10;
        int maxTransactions = 5;
        int numTransactions = 5;

        when(transactionVelocityDetectorService.getIndividuoMinutesRange()).thenReturn(minutesRange);
        when(transactionVelocityDetectorService.getIndividuoMaxTransactions()).thenReturn(maxTransactions);
        when(transactionRepository.countTransactionsFromDate(eq(userId), any(LocalDateTime.class))).thenReturn(numTransactions);

        Alert alert = transactionService.getFastMultipleTransactionAlert(userId, clientType);

        assertNull(alert);
        verify(transactionVelocityDetectorService).getIndividuoMinutesRange();
        verify(transactionVelocityDetectorService).getIndividuoMaxTransactions();
        verify(transactionRepository).countTransactionsFromDate(eq(userId), any(LocalDateTime.class));
    }

    @Test
    void getFastMultipleTransactionAlert_Empresa_ExceedsMax_ReturnsAlert() {
        Long userId = 2L;
        String clientType = "empresa";
        int minutesRange = 20;
        int maxTransactions = 10;
        int numTransactions = 11;

        when(transactionVelocityDetectorService.getEmpresaMinutesRange()).thenReturn(minutesRange);
        when(transactionVelocityDetectorService.getEmpresaMaxTransactions()).thenReturn(maxTransactions);
        when(transactionRepository.countTransactionsFromDate(eq(userId), any(LocalDateTime.class))).thenReturn(numTransactions);
        
        
        Alert alert = transactionService.getFastMultipleTransactionAlert(userId, clientType);

        assertNotNull(alert);
        assertEquals(userId, alert.userId());
        assertTrue(alert.description().contains("Fast multiple transactions detected"));
        verify(transactionVelocityDetectorService).getEmpresaMinutesRange();
        verify(transactionVelocityDetectorService).getEmpresaMaxTransactions();
        verify(transactionRepository).countTransactionsFromDate(eq(userId), any(LocalDateTime.class));
    }

    @Test
    void getFastMultipleTransactionAlert_Empresa_NotExceedsMax_ReturnsNull() {
        Long userId = 2L;
        String clientType = "empresa";
        int minutesRange = 20;
        int maxTransactions = 10;
        int numTransactions = 10;

        when(transactionVelocityDetectorService.getEmpresaMinutesRange()).thenReturn(minutesRange);
        when(transactionVelocityDetectorService.getEmpresaMaxTransactions()).thenReturn(maxTransactions);
        when(transactionRepository.countTransactionsFromDate(eq(userId), any(LocalDateTime.class))).thenReturn(numTransactions);

        Alert alert = transactionService.getFastMultipleTransactionAlert(userId, clientType);

        assertNull(alert);
        verify(transactionVelocityDetectorService).getEmpresaMinutesRange();
        verify(transactionVelocityDetectorService).getEmpresaMaxTransactions();
        verify(transactionRepository).countTransactionsFromDate(eq(userId), any(LocalDateTime.class));
    }
}