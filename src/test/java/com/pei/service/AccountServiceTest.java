package com.pei.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pei.dto.Alert;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para AccountService")
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Nested
    @DisplayName("Tests for validateHighRiskClient")
    class ValidateHighRiskClientTests {

        @Test
        @DisplayName("Debe retornar alerta de alto riesgo si el usuario es de alto riesgo")
        void testHighRiskUser() {
            Long userId = 1L;
            // Simula un usuario de alto riesgo
            String json = "{\"id\":1,\"name\":\"Juan\",\"risk\":\"alto\",\"accounts\":[]}";
            try (MockedStatic<ClienteService> mock = mockStatic(ClienteService.class)) {
                mock.when(() -> ClienteService.obtenerClienteJson(userId.intValue())).thenReturn(json);
                Alert alert = accountService.validateHighRiskClient(userId);
                assertNotNull(alert);
                assertEquals("Alerta: El cliente es de alto riesgo.", alert.description());
            }
        }

        @Test
        @DisplayName("Debe retornar alerta de bajo riesgo si el usuario es de bajo riesgo")
        void testLowRiskUser() {
            Long userId = 2L;
            // Simula un usuario de bajo riesgo
            String json = "{\"id\":2,\"name\":\"Ana\",\"risk\":\"bajo\",\"accounts\":[]}";
            try (MockedStatic<ClienteService> mock = mockStatic(ClienteService.class)) {
                mock.when(() -> ClienteService.obtenerClienteJson(userId.intValue())).thenReturn(json);
                Alert alert = accountService.validateHighRiskClient(userId);
                assertNotNull(alert);
                assertEquals("Cliente verificado como de bajo riesgo.", alert.description());
            }
        }

        @Test
        @DisplayName("Debe retornar alerta de usuario no encontrado si el usuario no es encontrado")
        void testNotFoundUser() {
            Long userId = 3L;
            // Simula un JSON vacío o inválido
            String json = "{}";
            try (MockedStatic<ClienteService> mock = mockStatic(ClienteService.class)) {
                mock.when(() -> ClienteService.obtenerClienteJson(userId.intValue())).thenReturn(json);
                Alert alert = accountService.validateHighRiskClient(userId);
                assertNotNull(alert);
                assertEquals("Alerta: Usuario no encontrado.", alert.description());
            }
        }
    }
}