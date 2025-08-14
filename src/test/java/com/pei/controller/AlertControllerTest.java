package com.pei.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.pei.service.*;
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
import com.pei.dto.UserTransaction;


@WebMvcTest(AlertController.class)
@ExtendWith(MockitoExtension.class)
class AlertControllerTest {

    @MockitoBean
    private AlertService service;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private GeolocalizationService geolocalizationService;

    @MockitoBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    MockMvc mockMvc;

    @Test
    void Should_ReturnOkAlert_When_MoneyMuleDetected() throws Exception {
        // Aquí puedes simular el comportamiento del servicio y probar la lógica del controlador
        // Simular el comportamiento del servicio
        User user = new User(1L);
        Account account = new Account(1L, user);
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> inputTransactions = List.of(new Transaction(user, new BigDecimal("100.00"), now.minusHours(2), account, account));

        when(service.verifyMoneyMule(anyList())).thenReturn(true);

        mockMvc.perform(post("/api/alerta-money-mule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputTransactions))) // Simular una solicitud POST
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$.userId").value(1))
            .andExpect(jsonPath("$.description").value("Alerta: Posible Money Mule detectado del usuario 1"))
            .andDo(print());
    }

    @Test
    void Should_ReturnNotContent_When_MoneyMuleNotDetected() throws Exception {
        // Aquí puedes simular el comportamiento del servicio y probar la lógica del controlador
        // Simular el comportamiento del servicio
        when(service.verifyMoneyMule(anyList())).thenReturn(false);

        mockMvc.perform(post("/api/alerta-money-mule")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))// Simular una solicitud POST con un cuerpo vacío, ya que esta Mockeado el Service
            .andExpect(status().isNotFound())
            .andDo(print());
    }


    //TEST GONZA
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
                    .andExpect(jsonPath("$.description").value("Perfil de usuario validado para la transacción."));

        }
    }

    @Test
    void shouldReturnAlertWhenTransactionHasMoreThanTwoApprovals() throws Exception {
        // given
        Long transactionId = 123L;
        Alert mockAlert = new Alert(transactionId,
            "Transacción con ID = " + transactionId + " tiene más de 2 aprobaciones");

        when(service.approvalAlert(transactionId)).thenReturn(mockAlert);

        String jsonRequest = "123";

        // when
        var result = mockMvc.perform(post("/api/alerta-aprobaciones")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest));

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(123))
            .andExpect(jsonPath("$.description").value("Transacción con ID = 123 tiene más de 2 aprobaciones"));

        verify(service, times(1)).approvalAlert(transactionId);
    }

    @Test
    void shouldReturnAlertWhenTransactionIsOutOfTimeRange() throws Exception {
        // given: historial de transacciones y nueva transacción a testear(esta fuera de rango)
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
        Alert mockAlert = new Alert(3L, "Transacción con ID = 3, realizada fuera del rango de horas promedio: 9 - 14");

        // devuelvo la alerta
        when(service.timeRangeAlert(anyList(), any(Transaction.class))).thenReturn(mockAlert);

        // when:
        var result = mockMvc.perform(post("/api/alerta-horario")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest));

        // then: verificamos que el status y el body sean correctos
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(3))
            .andExpect(jsonPath("$.description").value("Transacción con ID = 3, realizada fuera del rango de horas promedio: 9 - 14"));

        // verificamos que se llamó al servicio exactamente una vez
        verify(service, times(1)).timeRangeAlert(anyList(), any(Transaction.class));
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

        //When
        when(service.alertCriticality(any(Transaction.class)))
            .thenReturn(new Alert(10L, "Transacción de alta criticidad. Se notificará por Mail."));

        // Then
        mockMvc.perform(post("/api/alerta-canales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.userId").value(10))
            .andExpect(jsonPath("$.description").value("Transacción de alta criticidad. Se notificará por Mail."))
            .andDo(print());

        verify(service).alertCriticality(any(Transaction.class));
    }

    @Test
    void Should_ReturnNotFound_When_CriticalityIsLow() throws Exception {
        when(service.alertCriticality(any(Transaction.class))).thenReturn(null);

        Transaction transaction = new Transaction();
        transaction.setAmount(BigDecimal.valueOf(1000)); // monto bajo

        mockMvc.perform(post("/api/alerta-canales")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transaction)))
            .andExpect(status().isNotFound())
            .andDo(print());

        verify(service).alertCriticality(any(Transaction.class));
    }

}
