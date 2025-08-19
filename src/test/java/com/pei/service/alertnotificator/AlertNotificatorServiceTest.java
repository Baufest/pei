package com.pei.service.alertnotificator;

import com.pei.dto.TransactionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AlertNotificatorServiceTest {

    @Mock
    private AlertNotificatorStrategy alertNotificatorStrategy;

    @InjectMocks
    private AlertNotificatorService alertNotificatorService;

    private final Long userId = 1L;
    private final TransactionDTO transactionDTO = new TransactionDTO(
        100L, "1234567890ACBDEFGHIJKL", BigDecimal.valueOf(5000), "ARS", 1L, LocalDateTime.now());

    @BeforeEach
    void setUp() {
        alertNotificatorService.setAlertNotificatorStrategy(alertNotificatorStrategy);
    }

    @Test
    void executeNotificator_CuandoEjecucionExitosa_VerificaEnvio() throws Exception {
        // Arrange
        doNothing().when(alertNotificatorStrategy).sendCriticalAlert(userId, transactionDTO);

        // Act
        alertNotificatorService.executeNotificator(userId, transactionDTO);

        // Assert
        verify(alertNotificatorStrategy, times(1)).sendCriticalAlert(userId, transactionDTO);
    }

    @Test
    void executeNotificator_CuandoEstrategiaLanzaExcepcion_LanzaAlertNotificatorException() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Error de envío")).when(alertNotificatorStrategy).sendCriticalAlert(userId, transactionDTO);

        // Act & Assert
        AlertNotificatorException exception = assertThrows(AlertNotificatorException.class, () ->
            alertNotificatorService.executeNotificator(userId, transactionDTO)
        );
        assertTrue(exception.getMessage().contains("Error de envío"));
        verify(alertNotificatorStrategy, times(1)).sendCriticalAlert(userId, transactionDTO);
    }
}
