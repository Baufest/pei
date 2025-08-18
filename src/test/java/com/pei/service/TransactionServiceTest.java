package com.pei.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pei.dto.Alert;
import com.pei.repository.TransactionRepository;
import com.pei.service.bbva.ScoringService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
    @Mock
    private ScoringServiceInterno scoringServiceInterno;
    @Mock
    private Gson gson;
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
        when(transactionRepository.countTransactionsFromDate(eq(userId), any(LocalDateTime.class)))
                .thenReturn(numTransactions);

        Alert alert = transactionService.getFastMultipleTransactionAlert(userId, clientType);

        assertNull(alert);
        verify(transactionVelocityDetectorService).getEmpresaMinutesRange();
        verify(transactionVelocityDetectorService).getEmpresaMaxTransactions();
        verify(transactionRepository).countTransactionsFromDate(eq(userId), any(LocalDateTime.class));
    }

    @Test
    void processTransaction_CuandoStatusDistintoDe200_RetornaAlertRechazada() {
        Long idCliente = 1L;
        String scoringJson = "{\"status\":500,\"mensaje\":\"Error interno del servidor de scoring\",\"timestamp\":2025-08-18T10:00:00Z}";

        try (MockedStatic<ScoringService> mocked = Mockito.mockStatic(ScoringService.class)) {
            mocked.when(() -> ScoringService.consultarScoring(idCliente.intValue()))
                    .thenReturn(scoringJson);

            JsonObject fakeJson = new JsonObject();
            fakeJson.addProperty("status", 500);
            fakeJson.addProperty("mensaje", "Error interno del servidor de scoring");
            fakeJson.addProperty("timestamp", "2025-08-18T10:00:00Z");

            when(gson.fromJson(scoringJson, JsonObject.class)).thenReturn(fakeJson);

            Alert result = transactionService.processTransaction(idCliente);

            assertNotNull(result);
            assertEquals(idCliente, result.userId());
            assertTrue(result.description().contains("rechazada"));
        }
    }

    @Test
    void processTransaction_CuandoColorVerde_RetornaAlertAprobada() {
        Long idCliente = 2L;
        int scoringCliente = 70;

        String scoringJson = "{\"status\":200,\"mensaje\":\"Consulta exitosa\",\"idCliente\":2,\"scoring\":" +
                scoringCliente + ",\"timestamp\":\"2025-08-18T10:00:00Z\"}";

        try (MockedStatic<ScoringService> mocked = Mockito.mockStatic(ScoringService.class)) {
            mocked.when(() -> ScoringService.consultarScoring(idCliente.intValue()))
                    .thenReturn(scoringJson);

            JsonObject fakeJson = new JsonObject();
            fakeJson.addProperty("status", 200);
            fakeJson.addProperty("mensaje", "Consulta exitosa");
            fakeJson.addProperty("idCliente", idCliente.intValue());
            fakeJson.addProperty("scoring", scoringCliente);
            fakeJson.addProperty("timestamp", "2025-08-18T10:00:00Z");

            when(gson.fromJson(scoringJson, JsonObject.class)).thenReturn(fakeJson);

            when(scoringServiceInterno.getScoringColorBasedInUserScore(scoringCliente))
                    .thenReturn("Verde");

            Alert result = transactionService.processTransaction(idCliente);

            assertNotNull(result);
            assertEquals(idCliente, result.userId());
            assertTrue(result.description().contains("aprobada"));
            assertTrue(result.description().contains(String.valueOf(scoringCliente)));
        }
    }

    @Test
    void processTransaction_CuandoColorAmarillo_RetornaAlertRevision() {
        Long idCliente = 3L;
        int scoringCliente = 60;
        String scoringJson = "{\"status\":200,\"mensaje\":\"Consulta exitosa\",\"idCliente\":3,\"scoring\":" +
                scoringCliente + ",\"timestamp\":\"2025-08-18T10:00:00Z\"}";

        try (MockedStatic<ScoringService> mocked = Mockito.mockStatic(ScoringService.class)) {
            mocked.when(() -> ScoringService.consultarScoring(idCliente.intValue()))
                    .thenReturn(scoringJson);

            JsonObject fakeJson = new JsonObject();
            fakeJson.addProperty("status", 200);
            fakeJson.addProperty("mensaje", "Consulta exitosa");
            fakeJson.addProperty("idCliente", idCliente.intValue());
            fakeJson.addProperty("scoring", scoringCliente);
            fakeJson.addProperty("timestamp", "2025-08-18T10:00:00Z");

            when(gson.fromJson(scoringJson, JsonObject.class)).thenReturn(fakeJson);

            when(scoringServiceInterno.getScoringColorBasedInUserScore(scoringCliente))
                    .thenReturn("Amarillo");

            Alert result = transactionService.processTransaction(idCliente);

            assertNotNull(result);
            assertEquals(idCliente, result.userId());
            assertTrue(result.description().contains("revision"));
            assertTrue(result.description().contains(String.valueOf(scoringCliente)));
        }
    }

    @Test
    void processTransaction_CuandoColorRojo_RetornaAlertRechazada() {
        Long idCliente = 4L;
        int scoringCliente = 20;

        String scoringJson = "{\"status\":200,\"mensaje\":\"Consulta exitosa\",\"idCliente\":4,\"scoring\":" +
                scoringCliente + ",\"timestamp\":\"2025-08-18T10:00:00Z\"}";

        try (MockedStatic<ScoringService> mocked = Mockito.mockStatic(ScoringService.class)) {
            mocked.when(() -> ScoringService.consultarScoring(idCliente.intValue()))
                    .thenReturn(scoringJson);

            JsonObject fakeJson = new JsonObject();
            fakeJson.addProperty("status", 200);
            fakeJson.addProperty("mensaje", "Consulta exitosa");
            fakeJson.addProperty("idCliente", idCliente.intValue());
            fakeJson.addProperty("scoring", scoringCliente);
            fakeJson.addProperty("timestamp", "2025-08-18T10:00:00Z");

            when(gson.fromJson(scoringJson, JsonObject.class)).thenReturn(fakeJson);

            when(scoringServiceInterno.getScoringColorBasedInUserScore(scoringCliente))
                    .thenReturn("Rojo");

            Alert result = transactionService.processTransaction(idCliente);

            assertNotNull(result);
            assertEquals(idCliente, result.userId());
            assertTrue(result.description().contains("rechazada"));
            assertTrue(result.description().contains(String.valueOf(scoringCliente)));
        }
    }
}