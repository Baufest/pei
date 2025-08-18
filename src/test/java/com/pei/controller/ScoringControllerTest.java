package com.pei.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pei.service.ScoringServiceInterno;

@WebMvcTest(ScoringController.class)
class ScoringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ScoringServiceInterno scoringService;

    @Test
    void createPeriod_CuandoDatosValidos_RetornaOk() throws Exception {
        String startDateStr = LocalDateTime.now().toString();
        String endDateStr = LocalDateTime.now().plusDays(30).toString();

        mockMvc.perform(post("/api/scorings")
                .param("startDateStr", startDateStr)
                .param("endDateStr", endDateStr)
                .param("rojoStart", "0")
                .param("rojoEnd", "49")
                .param("amarilloStart", "50")
                .param("amarilloEnd", "69")
                .param("verdeStart", "70")
                .param("verdeEnd", "100"))
            .andExpect(status().isOk());

        verify(scoringService).createPeriodScorings(
            LocalDateTime.parse(startDateStr),
            LocalDateTime.parse(endDateStr),
            0, 49, 50, 69, 70, 100
        );
    }
}