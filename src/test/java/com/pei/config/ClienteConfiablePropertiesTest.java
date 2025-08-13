package com.pei.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ClienteConfiablePropertiesTest {

    @Autowired
    private ClienteConfiableProperties config;

    @Test
    void testCargaDesdeYaml() {
        assertAll(
            () -> assertEquals("MES", config.getAntiguedad().getMedicion()),
            () -> assertEquals(24.0, config.getAntiguedad().getMinima()),
            () -> assertEquals(List.of("IRRECUPERABLE"), config.getPerfilesNoConfiables()),
            () -> assertEquals(0, config.getLimiteChargeback())
        );
    }
}
