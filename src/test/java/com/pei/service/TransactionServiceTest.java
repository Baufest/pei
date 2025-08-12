package com.pei.service;

import com.pei.dto.Alert;
import com.pei.dto.Chargeback;
import com.pei.dto.Purchase;
import com.pei.repository.ChargebackRepository;
import com.pei.repository.PurchaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private ChargebackRepository chargebackRepository;
    private PurchaseRepository purchaseRepository;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        chargebackRepository = mock(ChargebackRepository.class);
        purchaseRepository = mock(PurchaseRepository.class);
        transactionService = new TransactionService(chargebackRepository, purchaseRepository);
    }

    @Test
    void givenChargebackFraud_thenReturnsAlert() {
        when(chargebackRepository.findByUserId(1L))
                .thenReturn(List.of(
                        new Chargeback(1L, 1L),
                        new Chargeback(2L, 1L)
                ));
        when(purchaseRepository.findByUserId(1L))
                .thenReturn(List.of(
                        new Purchase(1L, 1L),
                        new Purchase(2L, 1L),
                        new Purchase(3L, 1L),
                        new Purchase(4L, 1L),
                        new Purchase(5L, 1L),
                        new Purchase(6L, 1L),
                        new Purchase(7L, 1L),
                        new Purchase(8L, 1L),
                        new Purchase(9L, 1L),
                        new Purchase(10L, 1L),
                        new Purchase(11L, 1L),
                        new Purchase(12L, 1L),
                        new Purchase(13L, 1L),
                        new Purchase(14L, 1L),
                        new Purchase(15L, 1L)
                ));

        Alert alert = transactionService.getChargebackFraudAlert(1L);

        assertNotNull(alert);
        assertEquals(1L, alert.getUserId());
        assertEquals("Chargeback fraud detected for user 1", alert.getDescription());
        verify(chargebackRepository, times(1)).findByUserId(1L);
        verify(purchaseRepository, times(1)).findByUserId(1L);
    }

    @Test
    void givenNoChargebackFraud_thenReturnsNull() {
        
        when(chargebackRepository.findByUserId(1L))
                .thenReturn(List.of(new Chargeback(1L, 1L)));
        when(purchaseRepository.findByUserId(1L))
                .thenReturn(List.of(
                        new Purchase(1L, 1L),
                        new Purchase(2L, 1L),
                        new Purchase(3L, 1L),
                        new Purchase(4L, 1L),
                        new Purchase(5L, 1L),
                        new Purchase(6L, 1L),
                        new Purchase(7L, 1L),
                        new Purchase(8L, 1L),
                        new Purchase(9L, 1L),
                        new Purchase(10L, 1L)
                ));

        Alert alert = transactionService.getChargebackFraudAlert(1L);

        assertNull(alert);
        verify(chargebackRepository, times(1)).findByUserId(1L);
        verify(purchaseRepository, times(1)).findByUserId(1L);
    }

    @Test
    void givenNoPurchases_thenReturnsNull() {
        
        when(chargebackRepository.findByUserId(1L))
                .thenReturn(List.of(
                        new Chargeback(1L, 1L),
                        new Chargeback(2L, 1L)
                ));
        when(purchaseRepository.findByUserId(1L))
                .thenReturn(Collections.emptyList());

    
        Alert alert = transactionService.getChargebackFraudAlert(1L);

        assertNotNull(alert);
        verify(chargebackRepository, times(1)).findByUserId(1L);
        verify(purchaseRepository, times(1)).findByUserId(1L);
    }
}
