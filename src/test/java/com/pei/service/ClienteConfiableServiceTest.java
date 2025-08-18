package com.pei.service;

import com.pei.config.ClienteConfiableProperties;
import com.pei.domain.User;
import com.pei.dto.Chargeback;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


class ClienteConfiableServiceTest {

    private ClienteConfiableProperties config;
    private ClienteConfiableService service;

    @Nested
    @DisplayName("Tests para validar cliente confiable con configuración manual")
    class ValidateClienteConfiableServiceConConfigManual {
        @BeforeEach
        void setUp() {
            config = new ClienteConfiableProperties();

            // Configuración simulada
            ClienteConfiableProperties.Antiguedad antiguedad = new ClienteConfiableProperties.Antiguedad();
            antiguedad.setMedicion("MES");
            antiguedad.setMinimoMedicion(24);
            config.setAntiguedad(antiguedad);

            config.setPerfilesNoConfiables(List.of("IRRECUPERABLE", "ALTO RIESGO"));
            config.setLimiteChargeback(0);

            service = new ClienteConfiableService(config);
        }

        @Test
        void clienteCumpleTodosLosFiltros() {
            User cliente = new User();
            cliente.setCreationDate(LocalDate.now().minusMonths(36)); // 3 años
            cliente.setProfile("ahorrista");
            cliente.setRisk("normal");
            cliente.setAverageMonthlySpending(BigDecimal.valueOf(5000));
            // sin chargebacks

            assertTrue(service.esClienteConfiable(cliente));
        }

        @Test
        void clienteFallaPorAntiguedad() {
            User cliente = new User();
            cliente.setCreationDate(LocalDate.now().minusMonths(12)); // solo 1 año
            cliente.setProfile("ahorrista");
            cliente.setRisk("normal");

            assertFalse(service.esClienteConfiable(cliente));
        }

        @Test
        void clienteFallaPorChargebacks() {
            User cliente = new User();
            cliente.setCreationDate(LocalDate.now().minusMonths(36));
            cliente.setProfile("ahorrista");
            cliente.setRisk("normal");
            cliente.addChargebacks(List.of(new Chargeback(), new Chargeback())); // 2 chargebacks

            assertFalse(service.esClienteConfiable(cliente));
        }

        @Test
        void clienteFallaPorPerfil() {
            User cliente = new User();
            cliente.setCreationDate(LocalDate.now().minusMonths(36));
            cliente.setProfile("IRRECUPERABLE");
            cliente.setRisk("alto");
            cliente.addChargebacks(List.of(new Chargeback(), new Chargeback()));

            assertFalse(service.esClienteConfiable(cliente));
        }
    }
}
