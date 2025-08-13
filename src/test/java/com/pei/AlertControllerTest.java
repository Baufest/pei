package com.pei;

import com.fasterxml.jackson.databind.*;
import com.pei.controller.AlertController;
import com.pei.domain.Transaction;
import com.pei.dto.Alert;
import com.pei.repository.TransactionRepository;
import com.pei.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AlertControllerTest {

    @Mock
    private AlertService alertService;

    @InjectMocks
    private AlertController alertController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(alertController).build();
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
        var result = mockMvc.perform(post("/alerta-aprobaciones")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest));

        // then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(123))
            .andExpect(jsonPath("$.description").value("Transacción con ID = 123 tiene más de 2 aprobaciones"));

        verify(alertService, times(1)).approvalAlert(transactionId);
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
        when(alertService.timeRangeAlert(anyList(), any(Transaction.class))).thenReturn(mockAlert);

        // when:
        var result = mockMvc.perform(post("/alerta-horario")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequest));

        // then: verificamos que el status y el body sean correctos
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(3))
            .andExpect(jsonPath("$.description").value("Transacción con ID = 3, realizada fuera del rango de horas promedio: 9 - 14"));

        // verificamos que se llamó al servicio exactamente una vez
        verify(alertService, times(1)).timeRangeAlert(anyList(), any(Transaction.class));
    }


}
