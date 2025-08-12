package com.pei;

import com.fasterxml.jackson.databind.*;
import com.pei.controller.AlertController;
import com.pei.service.AlertService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
    void shouldReturnOkWhenTransactionIsValid() throws Exception{
        //given
        String jsonRequest = """
{
    "transactionId": 123,
    "approvals": [
        {"approverId": 1, "approverName": "User1"},
        {"approverId": 2, "approverName": "User2"},
        {"approverId": 3, "approverName": "User3"}
    ]
}
""";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonRequest);
        Long transactionId = jsonNode.get("transactionId").asLong();

        doNothing().when(alertService).approvalAlert(transactionId);

        //when
        var result = mockMvc.perform(post("/alerta-aprobaciones")
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest));

        //then
        result.andExpect(status().isOk()).andExpect(content().string("Se evaluo correctamente la transacción con ID = " + transactionId));
        verify(alertService, times(1)).approvalAlert(transactionId);
    }




}
