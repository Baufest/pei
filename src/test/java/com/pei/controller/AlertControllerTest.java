package com.pei.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import com.pei.domain.*;
import com.pei.dto.*;
import com.pei.repository.LoginRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pei.domain.Transaction;
import com.pei.domain.Account.Account;
import com.pei.domain.User.User;
import com.pei.dto.Alert;
import com.pei.domain.Login;
import com.pei.dto.UserTransaction;
import com.pei.repository.LoginRepository;
import com.pei.service.AccountService;
import com.pei.service.AlertService;
import com.pei.service.ClienteService;
import com.pei.service.GeoSimService;
import com.pei.service.GeolocalizationService;
import com.pei.service.LimitAmountTransactionService;
import com.pei.service.TransactionService;

@WebMvcTest(AlertController.class)
class AlertControllerTest {

        @MockitoBean
        private GeolocalizationService geolocalizationService;

        @MockitoBean
        private GeoSimService geoSimService;

        @MockitoBean
        private LoginRepository loginRepository;

        @MockitoBean
        private AlertService alertService;

        @MockitoBean
        private AccountService accountService;

        @MockitoBean
        private ClienteService clienteService;

        @MockitoBean
        private TransactionService transactionService;

        @MockitoBean
        private LimitAmountTransactionService limitAmountTransactionService;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        MockMvc mockMvc;

        @Nested
        @DisplayName("Tests para validar fraude geolocalizacion y dispositivo")
        class ValidarFraudeGeoDisp {

        @Test
        void shouldReturnAlertWhenNoPreviousLoginFound() throws Exception {
            User user = new User();
            ReflectionTestUtils.setField(user, "id", 1L);

            Device device = new Device();
            device.setDeviceId(1L);
            device.setUser(user);

            Login login = new Login(1L, user, device, "Canada", LocalDateTime.now(), true);

            when(geolocalizationService.verifyFraudOfDeviceAndGeolocation(any(Login.class)))
                .thenReturn(new Alert(user.getId(),
                    "Device and geolocalization problem detected for " + user.getId()));

            mockMvc.perform(post("/api/alerta-dispositivo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.description")
                    .value("Device and geolocalization problem detected for 1"));
        }

        @Test
        void shouldReturnAlertOkWhenPreviousLoginFound() throws Exception {
            // Creamos el User y Device
            User user = new User();
            ReflectionTestUtils.setField(user, "id", 1L);

            Device device = new Device();
            device.setDeviceId(1L);
            device.setUser(user);

            Login login = new Login(1L, user, device, "Canada", LocalDateTime.now(), true);

            // Mockeamos el servicio para que devuelva otra alerta
            when(geolocalizationService.verifyFraudOfDeviceAndGeolocation(any(Login.class)))
                .thenReturn(new Alert(user.getId(), "Something Else"));

            mockMvc.perform(post("/api/alerta-dispositivo")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.description").value("Something Else"));
        }







                @Test
                void shouldReturn404WhenLoginNull() throws Exception {
                        Login login = null;

                        mockMvc.perform(post("/api/alerta-dispositivo")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(login)))
                                        .andExpect(status().isBadRequest());
                }
        }

        @Test
        void Should_ReturnOkAlert_When_MoneyMuleDetected() throws Exception {
                // Aquí puedes simular el comportamiento del servicio y probar la lógica del
                // controlador
                // Simular el comportamiento del servicio
                User user = new User(1L);
                Account account = new Account(1L, user, "Argentina");
                LocalDateTime now = LocalDateTime.now();
                List<Transaction> inputTransactions = List
                                .of(new Transaction(user, new BigDecimal("100.00"), now.minusHours(2), account,
                                                account));

                when(alertService.verifyMoneyMule(anyList())).thenReturn(true);

                mockMvc.perform(post("/api/alerta-money-mule")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(inputTransactions))) // Simular una solicitud
                                                                                              // POST
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$.userId").value(1))
                                .andExpect(jsonPath("$.description")
                                                .value("Alerta: Posible Money Mule detectado del usuario 1"))
                                .andDo(print());
        }

        @Test
        void Should_ReturnNotContent_When_MoneyMuleNotDetected() throws Exception {
                // Aquí puedes simular el comportamiento del servicio y probar la lógica del
                // controlador
                // Simular el comportamiento del servicio
                when(alertService.verifyMoneyMule(anyList())).thenReturn(false);

                mockMvc.perform(post("/api/alerta-money-mule")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("[]"))// Simular una solicitud POST con un cuerpo vacío, ya que esta Mockeado
                                               // el
                                               // Service
                                .andExpect(status().isNotFound())
                                .andDo(print());
        }

        @Nested
        @DisplayName("Test para validar Fraude en Red de Transacciones")
        class ValidarFraudeRedTransferencias {
                private User user1, user2, user3, user4;
                private Account acc1, acc2, acc3, acc4;

                @BeforeEach
                void setUp() {
                        user1 = new User(1L);
                        user2 = new User(2L);
                        user3 = new User(3L);
                        user4 = new User(4L);

                        acc1 = new Account(1L, user1, "Argentina");
                        acc2 = new Account(2L, user2, "Brasil");
                        acc3 = new Account(3L, user3, "Chile");
                        acc4 = new Account(4L, user4, "Colombia");
                }

                @Test
                void shouldReturnAlertWhenAccountFound() throws Exception {
                        Account destino = new Account(1L, user1);

                        List<Transaction> allTransactions = List.of(
                                        new Transaction(user2, new BigDecimal("100"), LocalDateTime.now(), acc2, acc1),
                                        new Transaction(user3, new BigDecimal("200"), LocalDateTime.now(), acc3, acc1),
                                        new Transaction(user4, new BigDecimal("300"), LocalDateTime.now(), acc4, acc1));

                        when(alertService.verifyMultipleAccountsCashNotRelated(allTransactions))
                                        .thenReturn(List.of(destino));

                        mockMvc.perform(post("/api/alerta-red-transacciones")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(allTransactions)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.userId").value(1))
                                        .andExpect(jsonPath("$.description")
                                                        .value("Alert: Multiples transactions not related to the account of 1 detected"));

                        verify(alertService, times(1)).verifyMultipleAccountsCashNotRelated(anyList());
                }

                @Test
                void shouldReturnBadRequestWhenNotListParameter() throws Exception {
                        when(alertService.verifyMultipleAccountsCashNotRelated(List.of()))
                                        .thenReturn(List.of());

                        mockMvc.perform(post("/api/alerta-red-transacciones")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(List.of())))
                                        .andExpect(status().isBadRequest());
                }

                @Test
                void shouldReturnNotFoundWhenAccountNotFound() throws Exception {

                        List<Transaction> allTransactions = List.of(
                                        new Transaction(user2, new BigDecimal("100"), LocalDateTime.now(), acc2, acc1),
                                        new Transaction(user3, new BigDecimal("200"), LocalDateTime.now(), acc3, acc4),
                                        new Transaction(user4, new BigDecimal("300"), LocalDateTime.now(), acc4, acc2));

                        when(alertService.verifyMultipleAccountsCashNotRelated(allTransactions))
                                        .thenReturn(List.of());

                        mockMvc.perform(post("/api/alerta-red-transacciones")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(allTransactions)))
                                        .andExpect(status().isNotFound());
                }
        }

        @Nested
        @DisplayName("Tests para validarTransferenciasCuentasRecienCreadas")
        class ValidarTransferenciasCuentasRecienCreadasTests {

                @BeforeEach
                void setUp() {
                        objectMapper = new ObjectMapper();
                }

                @Test
                @DisplayName("POST /api/alerta-cuenta-nueva - éxito")
                void validarTransferenciasCuentasRecienCreadas_CuandoOk_RetornaAlerta() throws Exception {
                        Account cuenta = new Account();
                        Transaction transaccion = new Transaction();
                        Alert alertaEsperada = new Alert(null, "Alerta de prueba");

                        when(accountService.validateNewAccountTransfers(any(Account.class), any(Transaction.class)))
                                        .thenReturn(alertaEsperada);

                        String cuentaJson = objectMapper.writeValueAsString(cuenta);
                        String transaccionJson = objectMapper.writeValueAsString(transaccion);
                        String requestBody = "{" +
                                        "\"destinationAccount\":" + cuentaJson + "," +
                                        "\"currentTransaction\":" + transaccionJson +
                                        "}";

                        mockMvc.perform(post("/api/alerta-cuenta-nueva")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                                        .andExpect(status().isOk());
                }

                @Test
                @DisplayName("GET /api/alerta-cliente-alto-riesgo/{userId} - éxito")
                void validarClienteAltoRiesgo_CuandoOk_RetornaAlerta() throws Exception {
                        Long userId = 1L;
                        Alert alertaEsperada = new Alert(userId, "Alerta: El cliente es de alto riesgo.");

                        when(accountService.validateHighRiskClient(userId)).thenReturn(alertaEsperada);

                        mockMvc.perform(get("/api/alerta-cliente-alto-riesgo/{userId}", userId)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk());
                }

                @Test
                @DisplayName("POST /api/alerta-perfil - éxito")
                void validateUserProfileTransaction() throws Exception {
                        UserTransaction userTransaction = new UserTransaction();
                        userTransaction.setTransaction(new Transaction());
                        userTransaction.setUser(new User());

                        Alert alertaEsperada = new Alert(null, "Perfil de usuario validado para la transacción.");

                        when(accountService.validateUserProfileTransaction(any(User.class), any(Transaction.class)))
                                        .thenReturn(alertaEsperada);

                        mockMvc.perform(post("/api/alerta-perfil")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(userTransaction)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.description")
                                                        .value("Perfil de usuario validado para la transacción."));

                }

                @Test
                void shouldReturnAlertWhenTransactionHasMoreThanTwoApprovals() throws Exception {
                        // given
                        Long transactionId = 123L;
                        Alert mockAlert = new Alert(transactionId,
                                        "Transacción con ID = " + transactionId + " tiene más de 2 aprobaciones");

                        when(alertService.approvalAlert(transactionId)).thenReturn(mockAlert);

                        String jsonRequest = "123";

                        // when
                        var result = mockMvc.perform(post("/api/alerta-aprobaciones")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonRequest));

                        // then
                        result.andExpect(status().isOk())
                                        .andExpect(jsonPath("$.userId").value(123))
                                        .andExpect(jsonPath("$.description")
                                                        .value("Transacción con ID = 123 tiene más de 2 aprobaciones"));

                        verify(alertService, times(1)).approvalAlert(transactionId);
                }

                @Test
                void shouldReturnAlertWhenTransactionIsOutOfTimeRange() throws Exception {
                        // given: historial de transacciones y nueva transacción a testear(esta fuera de
                        // rango)
                        String jsonRequest = """
                                            {
                                              "transactions": [
                                                { "id": 1, "dateHour": "2025-08-13T09:30:00" },
                                                { "id": 2, "dateHour": "2025-08-13T14:20:00" }
                                              ],
                                              "newTransaction": { "id": 3, "dateHour": "2025-08-13T23:10:00" }
                                            }
                                        """;

                        // creamos la alerta esperada
                        Alert mockAlert = new Alert(3L,
                                        "Transacción con ID = 3, realizada fuera del rango de horas promedio: 9 - 14");

                        // devuelvo la alerta
                        when(alertService.timeRangeAlert(anyList(), any(Transaction.class))).thenReturn(mockAlert);

                        // when:
                        var result = mockMvc.perform(post("/api/alerta-horario")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(jsonRequest));

                        // then: verificamos que el status y el body sean correctos
                        result.andExpect(status().isOk())
                                        .andExpect(jsonPath("$.userId").value(3))
                                        .andExpect(jsonPath("$.description")
                                                        .value("Transacción con ID = 3, realizada fuera del rango de horas promedio: 9 - 14"));

                        // verificamos que se llamó al servicio exactamente una vez
                        verify(alertService, times(1)).timeRangeAlert(anyList(), any(Transaction.class));
                }

                @Test
                void Should_ReturnEmailAlert_When_CriticalityIsHigh() throws Exception {
                        // Given
                        User originUser = new User();
                        originUser.setName("Juan");
                        originUser.setEmail("juan@mail.com");

                        User destinationUser = new User();
                        destinationUser.setName("Pepo");
                        destinationUser.setEmail("pepo@mail.com");

                        Transaction transaction = new Transaction();
                        transaction.setAmount(BigDecimal.valueOf(2_000_000));
                        transaction.setUser(originUser);
                        transaction.setDestinationAccount(new Account(destinationUser));

                        // When
                        when(alertService.alertCriticality(any(Transaction.class)))
                                        .thenReturn(new Alert(10L,
                                                        "Transacción de alta criticidad. Se notificará por Mail."));

                        // Then
                        mockMvc.perform(post("/api/alerta-canales")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(transaction)))
                                        .andExpect(status().isOk())
                                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(jsonPath("$.userId").value(10))
                                        .andExpect(
                                                        jsonPath("$.description").value(
                                                                        "Transacción de alta criticidad. Se notificará por Mail."))
                                        .andDo(print());

                        verify(alertService).alertCriticality(any(Transaction.class));
                }

                @Test
                void Should_ReturnNotFound_When_CriticalityIsLow() throws Exception {
                        when(alertService.alertCriticality(any(Transaction.class))).thenReturn(null);

                        Transaction transaction = new Transaction();
                        transaction.setAmount(BigDecimal.valueOf(1000)); // monto bajo

                        mockMvc.perform(post("/api/alerta-canales")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(transaction)))
                                        .andExpect(status().isNotFound())
                                        .andDo(print());

                        verify(alertService).alertCriticality(any(Transaction.class));
                }

        }
    @Nested
    @DisplayName("Test para checkear ProcessTransaction con Scoring Service")
    class ScoringIntegration {

        @Test
        void checkProccesTransaction_CuandoTransaccionExitosa_RetornaResponseOk() throws Exception {
            // Arrange
            Long idCliente = 1L;
            Alert alertaMock = new Alert(idCliente,
                "Alerta: Transaccion aprobada para cliente " + idCliente
                    + " con scoring de: 90");

            when(transactionService.processTransaction(idCliente)).thenReturn(alertaMock);

            mockMvc.perform(post("/api/alerta-scoring")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(idCliente))
                .andExpect(jsonPath("$.description").value(
                    "Alerta: Transaccion aprobada para cliente " + idCliente
                        + " con scoring de: 90"));
        }

        @Test
        void checkProccesTransaction_CuandoAlertNull_RetornaNotFound() throws Exception {
            // Arrange
            Long idCliente = 2L;
            when(transactionService.processTransaction(anyLong()))
                .thenReturn(null);

            // Act & Assert
            mockMvc.perform(post("/api/alerta-scoring")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("2"))
                .andExpect(status().isNotFound());

            verify(transactionService).processTransaction(idCliente);
        }
    }


    @Nested
        @DisplayName("Tests para Evaluar el Account Takeover")
        class EvaluateAccountTakeoverTests {

                @Test
                @DisplayName("POST /api/alerta-account-takeover - éxito")
                void evaluateAccountTakeover_CuandoOk_RetornaAlerta() throws Exception {
                        when(transactionService.getMostRecentTransferByUserId(anyLong()))
                                        .thenReturn(Optional.of(new Transaction(new User(2L),
                                                        new BigDecimal("100.00"),
                                                        LocalDateTime.now(), new Account(), new Account())));

                        when(transactionService.isLastTransferInLastHour(any(Transaction.class),
                                        any(LocalDateTime.class)))
                                        .thenReturn(true);

                        String userEventJson = """
                                                [
                                                {
                                                "id": 1,
                                                "user": { "id": 1 },
                                                "type": "CHANGE_EMAIL",
                                                "eventDateHour": "2025-08-13T10:00:00"
                                                },
                                                {
                                                "id": 2,
                                                "user": { "id": 2 },
                                                "type": "CHANGE_PASSWORD",
                                                "eventDateHour": "2025-08-13T10:30:00"
                                                }
                                                ]
                                        """;

                        mockMvc.perform(
                                        post("/api/alerta-account-takeover")
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .content(userEventJson))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.userId").value(2))
                                        .andExpect(jsonPath("$.description")
                                                        .value("Alerta: Posible Account Takeover detectado para el usuario 2"));
                }

                @Test
                @DisplayName("POST /api/alerta-account-takeover - sin eventos de usuario")
                void evaluateAccountTakeover_CuandoNoHayEventos_RetornaBadRequest() throws Exception {
                        String userEventJson = "[]";

                        mockMvc.perform(post("/api/alerta-account-takeover")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(userEventJson))
                                        .andExpect(status().isBadRequest())
                                        .andExpect(jsonPath("$.description")
                                                        .value("Error: No se han proporcionado eventos de usuario."));
                }

        }

        @Nested
        @DisplayName("POST /api/alerta-transaccion-internacional")
        class AlertaTransaccionInternacionalControllerTest {

                @Test
                @DisplayName("Retorna 200 OK y alerta cuando la transacción internacional genera alerta")
                void postAlertaTransaccionInternacional_CuandoAlerta_RetornaOk() throws Exception {
                        Transaction transaction = new Transaction();
                        Alert expectedAlert = new Alert(1L, "Alerta: Transacción internacional aprobada");

                        when(transactionService.processTransactionCountryInternational(any(Transaction.class)))
                                        .thenReturn(expectedAlert);

                        mockMvc.perform(post("/api/alerta-transaccion-internacional")
                                        .contentType("application/json")
                                        .content(objectMapper.writeValueAsString(transaction)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.userId").value(1L))
                                        .andExpect(jsonPath("$.description")
                                                        .value("Alerta: Transacción internacional aprobada"));

                        verify(transactionService).processTransactionCountryInternational(any(Transaction.class));
                }

                @Test
                @DisplayName("Retorna 404 Not Found cuando no hay alerta")
                void postAlertaTransaccionInternacional_CuandoNoAlerta_RetornaNotFound() throws Exception {
                        Transaction transaction = new Transaction();

                        when(transactionService.processTransactionCountryInternational(any(Transaction.class)))
                                        .thenReturn(null);

                        mockMvc.perform(post("/api/alerta-transaccion-internacional")
                                        .contentType("application/json")
                                        .content(objectMapper.writeValueAsString(transaction)))
                                        .andExpect(status().isNotFound());

                        verify(transactionService).processTransactionCountryInternational(any(Transaction.class));
                }

        }
        @DisplayName("tests para velocity transaction fraud umbral")
        public class TestsUmbralDeVelocidades {
        Long userId;

        @BeforeEach
        public void setUp() {
                userId = 1L;
        }

        @Test
        void whenClientTypeIsNull_ShouldReturnNotFound() throws Exception {
                when(clienteService.getClientType(userId)).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/alerta-fast-multiple-transaction/{userId}", userId))
                        .andExpect(status().isNotFound());
        }

        @Test
        void whenClientTypeIsInvalid_ShouldReturnNotFound() throws Exception {
                when(clienteService.getClientType(userId)).thenReturn(Optional.of("otroTipo"));

                mockMvc.perform(get("/api/alerta-fast-multiple-transaction/{userId}", userId))
                        .andExpect(status().isNotFound());
        }

        @Test
        void whenAllOk_ShouldReturnAlerta() throws Exception {
                Alert alert = new Alert(userId, "Fast multiple transactions detected for user " + userId);

                when(clienteService.getClientType(userId)).thenReturn(Optional.of("individuo"));
                when(transactionService.getFastMultipleTransactionAlert(userId, "individuo")).thenReturn(alert);

                mockMvc.perform(get("/api/alerta-fast-multiple-transaction/{userId}", userId))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.userId").value(userId))
                        .andExpect(jsonPath("$.description").value(
                                "Fast multiple transactions detected for user " + userId));
        }

        @Test
        void whenAlertaIsNull_ShouldReturnNotFound() throws Exception {
                when(clienteService.getClientType(userId)).thenReturn(Optional.of("empresa"));
                when(transactionService.getFastMultipleTransactionAlert(userId, "empresa")).thenReturn(null);

                mockMvc.perform(get("/api/alerta-fast-multiple-transaction/{userId}", userId))
                        .andExpect(status().isNotFound());
        }
        }

    @Nested @DisplayName("Tests de Autenticación por Comportamiento Inusual")
    class CheckUnusualBehaviorTests {

        @Test
        void shouldReturnAlertWhenBehaviorIsUnusual() throws Exception {
            // Usuario
            User user = new User();
            ReflectionTestUtils.setField(user, "id", 1L);
            user.setDevices(new HashSet<>()); // Para isNewDevice

            // Transaction
            Transaction transaction = new Transaction();
            ReflectionTestUtils.setField(transaction, "id", 12L);
            ReflectionTestUtils.setField(transaction, "user", user);

            // Login
            Device device = new Device();
            ReflectionTestUtils.setField(device, "deviceId", 123L); // CORREGIDO

            Login login = new Login();
            ReflectionTestUtils.setField(login, "id", 5L);
            ReflectionTestUtils.setField(login, "user", user);
            ReflectionTestUtils.setField(login, "device", device);

            // TransactionLogin DTO usando IDs
            TransactionLogin transactionLogin = new TransactionLogin(transaction.getId(), login.getId());

            // Mock alerta
            Alert alert = new Alert(user.getId(), "Dispositivo nuevo y horario inusual");
            when(alertService.evaluateTransactionBehavior(anyLong(), anyLong()))
                .thenReturn(alert);

            // Test MVC
            mockMvc.perform(post("/api/alerta/comportamiento")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(transactionLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.description").value("Dispositivo nuevo y horario inusual"));
        }




        @Test
        void shouldReturnNotFoundWhenNoAlertGenerated() throws Exception {
            // Simulamos IDs
            Long transactionId = 12L;
            Long loginId = 5L;

            // Creamos el DTO con IDs
            TransactionLogin transactionLogin = new TransactionLogin(transactionId, loginId);

            // Mock: no se genera alerta
            when(alertService.evaluateTransactionBehavior(anyLong(), anyLong()))
                .thenReturn(null);

            // Test MVC
            mockMvc.perform(post("/api/alerta/comportamiento")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(transactionLogin)))
                .andExpect(status().isNotFound());
        }

        @Test
        void shouldReturnAlertWhenAmountExceedsThreshold() throws Exception {
            // IDs simulados
            Long transactionId = 12L;
            Long loginId = 5L;
            Long userId = 1L;

            // DTO con IDs
            TransactionLogin transactionLogin = new TransactionLogin(transactionId, loginId);

            // Mock: alerta por monto inusual
            Alert alert = new Alert(userId, "Monto de transacción inusual");
            when(alertService.evaluateTransactionBehavior(anyLong(), anyLong()))
                .thenReturn(alert);

            // Test MVC
            mockMvc.perform(post("/api/alerta/comportamiento")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(transactionLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.description").value("Monto de transacción inusual"));
        }


        @Test
        void shouldReturnInternalServerErrorOnException() throws Exception {
            // IDs simulados
            Long transactionId = 12L;
            Long loginId = 5L;

            // DTO con IDs
            TransactionLogin transactionLogin = new TransactionLogin(transactionId, loginId);

            // Simular excepción en el servicio
            when(alertService.evaluateTransactionBehavior(anyLong(), anyLong()))
                .thenThrow(new RuntimeException("Error inesperado"));

            // Test MVC
            mockMvc.perform(post("/api/alerta/comportamiento")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(transactionLogin)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.userId").doesNotExist())
                .andExpect(jsonPath("$.description")
                    .value("Error interno del servidor. No se pudo procesar la solicitud."));
        }

    }


}
