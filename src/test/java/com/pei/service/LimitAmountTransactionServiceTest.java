package com.pei.service;

import com.pei.domain.AmountLimit;
import com.pei.repository.AmountLimitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LimitAmountTransactionServiceTest {

    @Mock
    private AmountLimitRepository amountLimitRepository;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private LimitAmountTransactionService limitAmountTransactionService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
        void getAvailableAmount_returnsAmountFromRepository() {
        // given
        Long userId = 1L;
        String clientType = "individual";
        LocalDateTime now = LocalDateTime.now();

        AmountLimit amountLimit = new AmountLimit(clientType, BigDecimal.valueOf(1000),
                now.minusDays(1), now.plusDays(1));

        when(clienteService.getClientType(userId)).thenReturn(Optional.of(clientType));
        when(amountLimitRepository.findByClientTypeAndStartingDateBeforeAndExpirationDateAfter(eq(clientType), any(), any()))
                .thenReturn(amountLimit);

        // when
        BigDecimal result = limitAmountTransactionService.getAvailableAmount(userId);

        // then
        assertEquals(BigDecimal.valueOf(1000), result);
        verify(clienteService).getClientType(userId);
        verify(amountLimitRepository).findByClientTypeAndStartingDateBeforeAndExpirationDateAfter(eq(clientType), any(), any());
        }

        @Test
        void getAvailableAmount_throwsExceptionWhenNoLimitConfigured() {
        // given
        Long userId = 2L;
        String clientType = "empresa";

        when(clienteService.getClientType(userId)).thenReturn(Optional.of(clientType));
        when(amountLimitRepository.findByClientTypeAndStartingDateBeforeAndExpirationDateAfter(eq(clientType), any(), any()))
                .thenReturn(null);

        // when + then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> limitAmountTransactionService.getAvailableAmount(userId));

        assertTrue(ex.getMessage().contains("No existe un límite de monto configurado"));
        verify(clienteService).getClientType(userId);
        verify(amountLimitRepository).findByClientTypeAndStartingDateBeforeAndExpirationDateAfter(eq(clientType), any(), any());
        }

        @Test
        void getAvailableAmount_throwsExceptionWhenClientTypeMissing() {
        // given
        Long userId = 3L;

        when(clienteService.getClientType(userId)).thenReturn(Optional.empty());

        // when + then
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> limitAmountTransactionService.getAvailableAmount(userId));

        assertTrue(ex.getMessage().contains("No existe clientType configurado"));
        verify(clienteService).getClientType(userId);
        verifyNoInteractions(amountLimitRepository);
        }

    @Test
    void createAmountLimit_savesValidLimit() {
        // given
        String clientType = "empresa";
        BigDecimal amount = BigDecimal.valueOf(2000);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(10);

        when(amountLimitRepository.existsByClientTypeAndStartingDateLessThanEqualAndExpirationDateGreaterThanEqual(clientType, end, start))
                .thenReturn(false);

        // when
        limitAmountTransactionService.createAmountLimit(clientType, amount, start, end);

        // then
        verify(amountLimitRepository).save(any(AmountLimit.class));
    }

    @Test
    void createAmountLimit_throwsExceptionWhenAmountIsZero() {
        String clientType = "individuo";
        BigDecimal amount = BigDecimal.ZERO;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> limitAmountTransactionService.createAmountLimit(clientType, amount, start, end));

        assertEquals("El monto debe ser mayor que cero.", ex.getMessage());
        verifyNoInteractions(amountLimitRepository);
    }

    @Test
    void createAmountLimit_throwsExceptionWhenDatesInvalid() {
        String clientType = "individuo";
        BigDecimal amount = BigDecimal.TEN;
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> limitAmountTransactionService.createAmountLimit(clientType, amount, start, end));

        assertTrue(ex.getMessage().contains("La fecha de inicio"));
    }

    @Test
    void createAmountLimit_throwsExceptionWhenOverlapExists() {
        String clientType = "individuo";
        BigDecimal amount = BigDecimal.TEN;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusDays(5);

        when(amountLimitRepository.existsByClientTypeAndStartingDateLessThanEqualAndExpirationDateGreaterThanEqual(clientType, end, start))
                .thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> limitAmountTransactionService.createAmountLimit(clientType, amount, start, end));

        assertTrue(ex.getMessage().contains("se solapa"));
    }
}
