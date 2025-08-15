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
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import com.pei.dto.Alert;
import com.pei.dto.Logins;
import com.pei.dto.UserTransaction;

import com.pei.repository.LoginsRepository;

import com.pei.service.AccountService;
import com.pei.service.AlertService;
import com.pei.service.GeoSimService;
import com.pei.service.GeolocalizationService;
import com.pei.service.TransactionService;

@WebMvcTest(AlertController.class)
@ExtendWith(MockitoExtension.class)
class AlertControllerTest {

        @MockitoBean
        private GeolocalizationService geolocalizationService;

        @MockitoBean
        private GeoSimService geoSimService;

        @MockitoBean
        private LoginsRepository loginsRepository;

        @MockitoBean
        private AlertService alertService;

        @MockitoBean
        private AccountService accountService;

        @MockitoBean
        private TransactionService transactionService;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        MockMvc mockMvc;

        @Nested
        @DisplayName("Tests para validar fraude geolocalizacion y dispositivo")
        class ValidarFraudeGeoDisp {


                /* solo probamos el controller que funcione corretamente */
                @Test
                void shouldReturnAlertWhenNoPreviousLoginsFound() throws Exception {
                        Logins login = new Logins(1L, 1L, "qwertasdfgh", "Canada", LocalDateTime.now(), true);

                        when(geolocalizationService.verifyFraudOfDeviceAndGeolocation(login))
                                        .thenReturn(new Alert(login.userId(),
                                                        "Device and geolocalization problem detected for "
                                                                        + login.userId()));

                        mockMvc.perform(post("/api/alerta-dispositivo")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(login)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.userId").value(1))
                                        .andExpect(jsonPath("$.description")
                                                        .value("Device and geolocalization problem detected for 1"));
                }

                /* probamos que devuelva something else, correctamente */
                @Test
                void shouldReturnAlertOkWhenPreviousLoginsFound() throws Exception {
                        Logins login = new Logins(1L, 1L, "qwertasdfgh", "Canada", LocalDateTime.now(), true);

                        when(geolocalizationService.verifyFraudOfDeviceAndGeolocation(login))
                                        .thenReturn(new Alert(1L, "Something Else"));

                        mockMvc.perform(post("/api/alerta-dispositivo")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(login)))
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.userId").value(1))
                                        .andExpect(jsonPath("$.description")
                                                        .value("Something Else"));
                }

                @Test
                void shouldReturn404WhenLoginNull() throws Exception {
                        Logins login = null;

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
                Account account = new Account(1L, user);
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

                        acc1 = new Account(1L, user1);
                        acc2 = new Account(2L, user2);
                        acc3 = new Account(3L, user3);
                        acc4 = new Account(4L, user4);
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
}

// TEST GONZA
@WebMvcTest(AlertController.class)
@ExtendWith(MockitoExtension.class)
@Nested
@DisplayName("Tests para validarTransferenciasCuentasRecienCreadas")
class ValidarTransferenciasCuentasRecienCreadasTests {

        @MockitoBean
        private AlertService alertService;

        @MockitoBean
        private AccountService accountService;

        @MockitoBean
        private TransactionService transactionService;

        @MockitoBean
        private GeolocalizationService geolocalizationService;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        MockMvc mockMvc;

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
                                .thenReturn(new Alert(10L, "Transacción de alta criticidad. Se notificará por Mail."));

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

        @DisplayName("Tests para Evaluar el Account Takeover")
        class EvaluateAccountTakeoverTests {

                @Test
                @DisplayName("POST /api/alerta-account-takeover - éxito")
                void evaluateAccountTakeover_CuandoOk_RetornaAlerta() throws Exception {
                        when(transactionService.getMostRecentTransferByUserId(anyLong()))
                                        .thenReturn(Optional.of(new Transaction(new User(2L), new BigDecimal("100.00"),
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
}