package com.pei.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import com.pei.dto.Alert;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para AccountService")
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private Account cuentaDestino;

    @Mock
    private AccountParamsService accountParamsService;

    @Mock
    private Transaction transaccionActual;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ObjectMapper realMapper = new ObjectMapper();
        accountService = new AccountService(accountParamsService, realMapper);
    }

    @Nested
    @DisplayName("Tests for validateHighRiskClient")
    class ValidateHighRiskClientTests {

        @Test
        @DisplayName("Debe retornar alerta de tipo cliente no encontrado si clientType es null")
        void testClientTypeNull() throws Exception {
            Long userId = 1L;
            String json = "{\"clientType\":null,\"chargebacks\":[]}";
            try (MockedStatic<ClienteService> mock = mockStatic(ClienteService.class)) {
                mock.when(() -> ClienteService.obtenerClienteJson(userId.intValue())).thenReturn(json);

                Alert alert = accountService.validateHighRiskClient(userId);

                assertNotNull(alert);
                assertEquals("Alerta: Tipo de cliente no encontrado. (NULL)", alert.description());
            }
        }

        @Test
        @DisplayName("Debe retornar alerta si chargebacks no existe o no es array")
        void testChargebacksMissing() throws Exception {
            Long userId = 2L;
            String json = "{\"clientType\":\"empresa\"}"; // no tiene chargebacks
            try (MockedStatic<ClienteService> mock = mockStatic(ClienteService.class)) {
                mock.when(() -> ClienteService.obtenerClienteJson(userId.intValue())).thenReturn(json);

                Alert alert = accountService.validateHighRiskClient(userId);

                assertNotNull(alert);
                assertEquals("Alerta: Lista de chargebacks no encontrada. (NULL)", alert.description());
            }
        }

        @Test
        @DisplayName("Debe retornar alerta de alto riesgo para cliente empresa")
        void testEmpresaHighRisk() throws Exception {
            Long userId = 3L;
            String json = "{\"clientType\":\"empresa\",\"chargebacks\":[{\"fechaCreacion\":\"2025-08-01\",\"monto\":100,\"aceptado\":false}]}";

            when(accountParamsService.getLimiteAlertaAltoRiesgoEmpresa()).thenReturn(1);

            try (MockedStatic<ClienteService> mock = mockStatic(ClienteService.class)) {
                mock.when(() -> ClienteService.obtenerClienteJson(userId.intValue())).thenReturn(json);

                Alert alert = accountService.validateHighRiskClient(userId);

                assertNotNull(alert);
                assertTrue(alert.description().contains("Alerta: Cliente empresarial de alto riesgo"));
            }
        }

        @Test
        @DisplayName("Debe retornar alerta de alto riesgo para cliente individuo")
        void testIndividuoHighRisk() throws Exception {
            Long userId = 4L;
            String json = "{\"clientType\":\"individuo\",\"chargebacks\":[{\"fechaCreacion\":\"2025-08-01\",\"monto\":100,\"aceptado\":false}]}";

            when(accountParamsService.getLimiteAlertaAltoRiesgoIndividuo()).thenReturn(1);

            try (MockedStatic<ClienteService> mock = mockStatic(ClienteService.class)) {
                mock.when(() -> ClienteService.obtenerClienteJson(userId.intValue())).thenReturn(json);

                Alert alert = accountService.validateHighRiskClient(userId);

                assertNotNull(alert);
                assertTrue(alert.description().contains("Alerta: Cliente individual de alto riesgo"));
            }
        }

        @Test
        @DisplayName("Debe retornar cliente validado sin alertas si no hay riesgo")
        void testClienteValidadoSinAlertas() throws Exception {
            Long userId = 5L;
            String json = "{\"clientType\":\"individuo\",\"chargebacks\":[]}";

            when(accountParamsService.getLimiteAlertaAltoRiesgoIndividuo()).thenReturn(1);

            try (MockedStatic<ClienteService> mock = mockStatic(ClienteService.class)) {
                mock.when(() -> ClienteService.obtenerClienteJson(userId.intValue())).thenReturn(json);

                Alert alert = accountService.validateHighRiskClient(userId);

                assertNotNull(alert);
                assertEquals("Alerta: Cliente validado sin alertas de riesgo.", alert.description());
            }
        }

        @Test
        @DisplayName("Debe retornar alerta de error si hay JsonProcessingException")
        void testJsonProcessingException() throws Exception {
            Long userId = 6L;

            try (MockedStatic<ClienteService> mock = mockStatic(ClienteService.class)) {
                // Devolvemos un JSON mal formado
                mock.when(() -> ClienteService.obtenerClienteJson(userId.intValue())).thenReturn("BAD_JSON");

                Alert alert = accountService.validateHighRiskClient(userId);

                assertNotNull(alert);
                assertEquals("Alerta: Error al procesar los datos del cliente.", alert.description());
            }
        }
    }

    @Nested
    @DisplayName("validateNewAccountTransfers")
    class ValidateNewAccountTransfersTests {

        @Test
        @DisplayName("Debe alertar si la cuenta fue creada hace menos de 48 horas")
        void testCuentaCreadaHaceMenosDe48Horas() {
            LocalDateTime now = LocalDateTime.now();
            when(transaccionActual.getDate()).thenReturn(now);
            when(cuentaDestino.getCreationDate()).thenReturn(now.minusHours(24));

            Alert alert = accountService.validateNewAccountTransfers(cuentaDestino, transaccionActual);

            assertEquals("Alerta: Se transfiere dinero a una cuenta creada hace menos de 48 horas.",
                    alert.description());
        }

        @Test
        @DisplayName("Debe permitir transferencia si la cuenta fue creada hace más de 48 horas")
        void testCuentaCreadaHaceMasDe48Horas() {
            LocalDateTime now = LocalDateTime.now();
            when(transaccionActual.getDate()).thenReturn(now);
            when(cuentaDestino.getCreationDate()).thenReturn(now.minusHours(72));

            Alert alert = accountService.validateNewAccountTransfers(cuentaDestino, transaccionActual);

            assertEquals("Transferencia permitida.", alert.description());
        }

        @Test
        @DisplayName("Debe permitir transferencia si la cuenta fue creada exactamente hace 48 horas")
        void testCuentaCreadaExactamente48Horas() {
            LocalDateTime now = LocalDateTime.now();
            when(transaccionActual.getDate()).thenReturn(now);
            when(cuentaDestino.getCreationDate()).thenReturn(now.minusHours(48));

            Alert alert = accountService.validateNewAccountTransfers(cuentaDestino, transaccionActual);

            assertEquals("Transferencia permitida.", alert.description());
        }

        @Test
        @DisplayName("Debe permitir transferencia si la cuenta fue creada después de la transacción")
        void testCuentaCreadaDespuesDeTransaccion() {
            LocalDateTime now = LocalDateTime.now();
            when(transaccionActual.getDate()).thenReturn(now);
            when(cuentaDestino.getCreationDate()).thenReturn(now.plusHours(1));

            Alert alert = accountService.validateNewAccountTransfers(cuentaDestino, transaccionActual);

            assertEquals("Transferencia permitida.", alert.description());
        }
    }

    @Nested
    @DisplayName("Tests para validateUserProfileTransaction")
    class ValidateUserProfileTransactionTests {

        @Test
        @DisplayName("Debe retornar alerta si el usuario es null")
        void testUserNull() {
            Alert alert = accountService.validateUserProfileTransaction(null,
                    new Transaction(BigDecimal.valueOf(100.0)));
            assertNotNull(alert);
            assertEquals("Alerta: Datos de usuario inválidos.", alert.description());
        }

        @Test
        @DisplayName("Debe retornar alerta si el perfil del usuario es null")
        void testUserProfileNull() {
            User user = new User();
            user.setProfile(null);
            Alert alert = accountService.validateUserProfileTransaction(user,
                    new Transaction(BigDecimal.valueOf(100.0)));
            assertNotNull(alert);
            assertEquals("Alerta: Datos de usuario inválidos.", alert.description());
        }

        @Test
        @DisplayName("Debe retornar alerta si la transacción es null")
        void testTransactionNull() {
            User user = new User();
            user.setProfile("normal");
            user.setAverageMonthlySpending(BigDecimal.valueOf(1000.0));
            Alert alert = accountService.validateUserProfileTransaction(user, null);
            assertNotNull(alert);
            assertEquals("Alerta: Datos de transacción inválidos.", alert.description());
        }

        @Test
        @DisplayName("Debe retornar alerta si el monto de la transacción es null")
        void testTransactionAmountNull() {
            User user = new User();
            user.setProfile("normal");
            user.setAverageMonthlySpending(BigDecimal.valueOf(1000.0));
            Transaction transaction = new Transaction(null);
            Alert alert = accountService.validateUserProfileTransaction(user, transaction);
            assertNotNull(alert);
            assertEquals("Alerta: Datos de transacción inválidos.", alert.description());
        }

        @Test
        @DisplayName("Debe retornar alerta si el monto es mayor a 3 veces el promedio y perfil es 'ahorrista'")
        void testAmountExceedsThresholdAndProfileAhorrista() {
            User user = new User();
            user.setProfile("ahorrista");
            user.setAverageMonthlySpending(BigDecimal.valueOf(1000.0));
            Transaction transaction = new Transaction(BigDecimal.valueOf(3500.0)); // > 3 * 1000 = 3000

            Alert alert = accountService.validateUserProfileTransaction(user, transaction);

            assertNotNull(alert);
            assertEquals("Alerta: Monto inusual para perfil.", alert.description());
        }

        @Test
        @DisplayName("Debe permitir validación correcta para monto dentro del rango y perfil 'ahorrista'")
        void testValidAmountAndProfileAhorrista() {
            User user = new User();
            user.setProfile("ahorrista");
            user.setAverageMonthlySpending(BigDecimal.valueOf(1000.0));
            Transaction transaction = new Transaction(BigDecimal.valueOf(2500.0)); // <= 3 * 1000

            Alert alert = accountService.validateUserProfileTransaction(user, transaction);

            assertNotNull(alert);
            assertEquals("Perfil de usuario validado para la transacción.", alert.description());
        }

        @Test
        @DisplayName("Debe permitir validación correcta para cualquier perfil distinto de 'ahorrista'")
        void testValidAmountAndProfileOther() {
            User user = new User();
            user.setProfile("normal");
            user.setAverageMonthlySpending(BigDecimal.valueOf(1000.0));
            Transaction transaction = new Transaction(BigDecimal.valueOf(5000.0)); // monto alto, pero perfil distinto

            Alert alert = accountService.validateUserProfileTransaction(user, transaction);

            assertNotNull(alert);
            assertEquals("Perfil de usuario validado para la transacción.", alert.description());
        }
    }
}
