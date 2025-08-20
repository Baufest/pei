package com.pei.service;

import com.pei.dto.Alert;
import com.pei.dto.Chargeback;
import com.pei.dto.Purchase;
import com.pei.repository.ChargebackRepository;
import com.pei.repository.PurchaseRepository;
import com.pei.repository.TransactionRepository;
import com.pei.service.bbva.ScoringService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

        User user1, user2;
        Account account1, account2;
        @Mock
        private ChargebackRepository chargebackRepository;
        @Mock
        private TransactionRepository transactionRepository;
        @Mock
        private PurchaseRepository purchaseRepository;
        @Mock
        private TransactionVelocityDetectorService transactionVelocityDetectorService;
        @Mock
        private ScoringServiceInterno scoringServiceInterno;
        @Mock
        private Gson gson;
        @InjectMocks
        private TransactionService transactionService;

        @BeforeEach
        void setUp() {
                user1 = new User(1L);
                user2 = new User(2L);
                account1 = new Account(1L, user1);
                account2 = new Account(2L, user2);
        }

        @Test
        void Should_GetLast24HoursTransactions_Correct() {
                // Given
                LocalDateTime now = LocalDateTime.now();
                Transaction t1 = new Transaction(user1, new BigDecimal("100.00"), now.minusHours(2), account1,
                                account1); // dentro
                                           // de
                                           // 24h
                Transaction t2 = new Transaction(user1, new BigDecimal("200.00"), now.minusDays(2), account1, account1); // fuera
                                                                                                                         // de
                                                                                                                         // 24h
                Transaction t3 = new Transaction(user2, new BigDecimal("300.00"), now.minusHours(10), account2,
                                account2); // dentro
                                           // de
                                           // 24h
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
                Transaction deposito1 = new Transaction(user1, new BigDecimal("100.00"), LocalDateTime.now(), account2,
                                account1);
                Transaction deposito2 = new Transaction(user1, new BigDecimal("50.00"), LocalDateTime.now(), account2,
                                account1);
                // Transferencia: destinationAccount.owner != user
                Transaction transferencia = new Transaction(user1, new BigDecimal("200.00"), LocalDateTime.now(),
                                account1,
                                account2);
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
                Transaction transferencia = new Transaction(user1, new BigDecimal("200.00"), LocalDateTime.now(),
                                account1,
                                account2);
                List<Transaction> transactions = List.of(transferencia);
                BigDecimal total = transactionService.totalDeposits(transactions);
                assertEquals(BigDecimal.ZERO, total);
        }

        @Test
        void Should_ReturnTotalTransfers_When_ContainsTransfers() {
                // Transferencia: destinationAccount.owner != user
                Transaction transferencia1 = new Transaction(user1, new BigDecimal("200.00"), LocalDateTime.now(),
                                account1,
                                account2);
                Transaction transferencia2 = new Transaction(user1, new BigDecimal("50.00"), LocalDateTime.now(),
                                account1,
                                account2);
                // Deposito: destinationAccount.owner == user
                Transaction deposito = new Transaction(user1, new BigDecimal("100.00"), LocalDateTime.now(), account2,
                                account1);
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
                Transaction deposito = new Transaction(user1, new BigDecimal("100.00"), LocalDateTime.now(), account2,
                                account1);
                List<Transaction> transactions = List.of(deposito);
                BigDecimal actualTotal = transactionService.totalTransfers(transactions);
                assertEquals(BigDecimal.ZERO, actualTotal);
        }

        @Test
        void givenChargebackFraud_thenReturnsAlert() {
                when(chargebackRepository.findByUserId(1L))
                                .thenReturn(List.of(
                                                new Chargeback(1L, user1),
                                                new Chargeback(2L, user1)));
                when(purchaseRepository.findByUserId(1L))
                                .thenReturn(List.of(
                                                new Purchase(1L, user1),
                                                new Purchase(2L, user1),
                                                new Purchase(3L, user1),
                                                new Purchase(4L, user1),
                                                new Purchase(5L, user1),
                                                new Purchase(6L, user1),
                                                new Purchase(7L, user1),
                                                new Purchase(8L, user1),
                                                new Purchase(9L, user1),
                                                new Purchase(10L, user1),
                                                new Purchase(11L, user1),
                                                new Purchase(12L, user1),
                                                new Purchase(13L, user1),
                                                new Purchase(14L, user1),
                                                new Purchase(15L, user1)));

                Alert alert = transactionService.getChargebackFraudAlert(1L);

                assertNotNull(alert);
                assertEquals(1L, alert.userId());
                assertEquals("Chargeback fraud detected for user 1", alert.description());
                verify(chargebackRepository, times(1)).findByUserId(1L);
                verify(purchaseRepository, times(1)).findByUserId(1L);
        }

        @Test
        void givenNoChargebackFraud_thenReturnsNull() {

                when(chargebackRepository.findByUserId(1L))
                                .thenReturn(List.of(new Chargeback(1L, user1)));
                when(purchaseRepository.findByUserId(1L))
                                .thenReturn(List.of(
                                                new Purchase(1L, user1),
                                                new Purchase(2L, user1),
                                                new Purchase(3L, user1),
                                                new Purchase(4L, user1),
                                                new Purchase(5L, user1),
                                                new Purchase(6L, user1),
                                                new Purchase(7L, user1),
                                                new Purchase(8L, user1),
                                                new Purchase(9L, user1),
                                                new Purchase(10L, user1)));

                Alert alert = transactionService.getChargebackFraudAlert(1L);

                assertNull(alert);
                verify(chargebackRepository, times(1)).findByUserId(1L);
                verify(purchaseRepository, times(1)).findByUserId(1L);
        }

        @Test
        void givenNoPurchases_thenReturnsNull() {

                when(chargebackRepository.findByUserId(1L))
                                .thenReturn(List.of(
                                                new Chargeback(1L, user1),
                                                new Chargeback(2L, user1)));
                when(purchaseRepository.findByUserId(1L))
                                .thenReturn(Collections.emptyList());

                Alert alert = transactionService.getChargebackFraudAlert(1L);

                assertNotNull(alert);
                verify(chargebackRepository, times(1)).findByUserId(1L);
                verify(purchaseRepository, times(1)).findByUserId(1L);
        }

        @Test
        void getFastMultipleTransactionAlert_Individuo_ExceedsMax_ReturnsAlert() {
                Long userId = 1L;
                String clientType = "individuo";
                int minutesRange = 10;
                int maxTransactions = 5;
                int numTransactions = 6;
                BigDecimal minMonto = new BigDecimal(10000);
                BigDecimal maxMonto = new BigDecimal(50000);

                Map<String, BigDecimal> umbralMonto = Map.of(
                                "minMonto", minMonto,
                                "maxMonto", maxMonto);

                when(transactionVelocityDetectorService.getIndividuoMinutesRange()).thenReturn(minutesRange);
                when(transactionVelocityDetectorService.getIndividuoMaxTransactions()).thenReturn(maxTransactions);
                when(transactionVelocityDetectorService.getIndividuoUmbralMonto()).thenReturn(umbralMonto);
                when(transactionRepository.countTransactionsByUserAfterDateBetweenMontos(eq(1L),
                                any(LocalDateTime.class),
                                eq(new BigDecimal("10000")),
                                eq(new BigDecimal("50000"))))
                                .thenReturn(numTransactions);

                Alert alert = transactionService.getFastMultipleTransactionAlert(userId, clientType);

                assertNotNull(alert);
                assertEquals(userId, alert.userId());
                assertTrue(alert.description().contains("Fast multiple transactions detected"));
                verify(transactionVelocityDetectorService, times(1)).getIndividuoMinutesRange();
                verify(transactionVelocityDetectorService, times(1)).getIndividuoMaxTransactions();
                verify(transactionVelocityDetectorService, times(2)).getIndividuoUmbralMonto();
                verify(transactionRepository).countTransactionsByUserAfterDateBetweenMontos(eq(userId),
                                any(LocalDateTime.class), eq(minMonto), eq(maxMonto));
        }

        @Test
        void getFastMultipleTransactionAlert_Individuo_NotExceedsMax_ReturnsNull() {
                Long userId = 1L;
                String clientType = "individuo";
                int minutesRange = 10;
                int maxTransactions = 5;
                int numTransactions = 5;
                BigDecimal minMonto = new BigDecimal(10000);
                BigDecimal maxMonto = new BigDecimal(50000);

                Map<String, BigDecimal> umbralMonto = Map.of(
                                "minMonto", minMonto,
                                "maxMonto", maxMonto);

                when(transactionVelocityDetectorService.getIndividuoMinutesRange()).thenReturn(minutesRange);
                when(transactionVelocityDetectorService.getIndividuoMaxTransactions()).thenReturn(maxTransactions);
                when(transactionVelocityDetectorService.getIndividuoUmbralMonto()).thenReturn(umbralMonto);
                when(transactionRepository.countTransactionsByUserAfterDateBetweenMontos(eq(1L),
                                any(LocalDateTime.class),
                                eq(new BigDecimal("10000")),
                                eq(new BigDecimal("50000"))))
                                .thenReturn(numTransactions);

                Alert alert = transactionService.getFastMultipleTransactionAlert(userId, clientType);

                assertNull(alert);
                verify(transactionVelocityDetectorService, times(1)).getIndividuoMinutesRange();
                verify(transactionVelocityDetectorService, times(1)).getIndividuoMaxTransactions();
                verify(transactionVelocityDetectorService, times(2)).getIndividuoUmbralMonto();
                verify(transactionRepository).countTransactionsByUserAfterDateBetweenMontos(eq(userId),
                                any(LocalDateTime.class), eq(minMonto), eq(maxMonto));
        }

        @Test
        void getFastMultipleTransactionAlert_Empresa_ExceedsMax_ReturnsAlert() {
                Long userId = 2L;
                String clientType = "empresa";
                int minutesRange = 20;
                int maxTransactions = 10;
                int numTransactions = 11;
                BigDecimal minMonto = new BigDecimal(100000);
                BigDecimal maxMonto = new BigDecimal(300000);
                Map<String, BigDecimal> umbralMonto = Map.of(
                                "minMonto", minMonto,
                                "maxMonto", maxMonto);

                when(transactionVelocityDetectorService.getEmpresaMinutesRange()).thenReturn(minutesRange);
                when(transactionVelocityDetectorService.getEmpresaMaxTransactions()).thenReturn(maxTransactions);
                when(transactionVelocityDetectorService.getEmpresaUmbralMonto()).thenReturn(umbralMonto);
                when(transactionRepository.countTransactionsByUserAfterDateBetweenMontos(eq(2L),
                                any(LocalDateTime.class),
                                eq(new BigDecimal("100000")),
                                eq(new BigDecimal("300000"))))
                                .thenReturn(numTransactions);

                Alert alert = transactionService.getFastMultipleTransactionAlert(userId, clientType);

                assertNotNull(alert);
                assertEquals(userId, alert.userId());
                assertTrue(alert.description().contains("Fast multiple transactions detected"));
                verify(transactionVelocityDetectorService, times(1)).getEmpresaMinutesRange();
                verify(transactionVelocityDetectorService, times(1)).getEmpresaMaxTransactions();
                verify(transactionVelocityDetectorService, times(2)).getEmpresaUmbralMonto();
                verify(transactionRepository).countTransactionsByUserAfterDateBetweenMontos(eq(userId),
                                any(LocalDateTime.class), eq(minMonto), eq(maxMonto));
        }

        @Test
        void getFastMultipleTransactionAlert_Empresa_NotExceedsMax_ReturnsNull() {
                Long userId = 2L;
                String clientType = "empresa";
                int minutesRange = 20;
                int maxTransactions = 10;
                int numTransactions = 10;
                BigDecimal minMonto = new BigDecimal(100000);
                BigDecimal maxMonto = new BigDecimal(300000);
                Map<String, BigDecimal> umbralMonto = Map.of(
                                "minMonto", minMonto,
                                "maxMonto", maxMonto);

                when(transactionVelocityDetectorService.getEmpresaMinutesRange()).thenReturn(minutesRange);
                when(transactionVelocityDetectorService.getEmpresaMaxTransactions()).thenReturn(maxTransactions);
                when(transactionVelocityDetectorService.getEmpresaUmbralMonto()).thenReturn(umbralMonto);
                when(transactionRepository.countTransactionsByUserAfterDateBetweenMontos(eq(2L),
                                any(LocalDateTime.class),
                                eq(new BigDecimal("100000")),
                                eq(new BigDecimal("300000"))))
                                .thenReturn(numTransactions);

                Alert alert = transactionService.getFastMultipleTransactionAlert(userId, clientType);

                assertNull(alert);
                verify(transactionVelocityDetectorService, times(1)).getEmpresaMinutesRange();
                verify(transactionVelocityDetectorService, times(1)).getEmpresaMaxTransactions();
                verify(transactionVelocityDetectorService, times(2)).getEmpresaUmbralMonto();

                verify(transactionRepository).countTransactionsByUserAfterDateBetweenMontos(eq(userId),
                                any(LocalDateTime.class), eq(minMonto), eq(maxMonto));
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