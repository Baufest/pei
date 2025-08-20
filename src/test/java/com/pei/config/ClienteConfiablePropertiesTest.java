package com.pei.config;

import com.pei.domain.User.ClientType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ClienteConfiablePropertiesTest {

    @Autowired
    private ClienteConfiableProperties config;

    @Test
    void testCargaDesdeYaml() {
        assertAll(
            () -> assertEquals("MES", config.getConfigFor(ClientType.INDIVIDUAL).getAntiguedad().getMedicion()),
            () -> assertEquals(24.0, config.getConfigFor(ClientType.INDIVIDUAL).getAntiguedad().getMinimoMedicion()),
            () -> assertEquals(List.of("IRRECUPERABLE"), config.getConfigFor(ClientType.INDIVIDUAL).getPerfilesNoConfiables()),
            () -> assertEquals(0, config.getConfigFor(ClientType.INDIVIDUAL).getLimiteChargeback())
        );
    }

    @Test
    void getConfigFor_CuandoTipoEsCompany_RetornaConfiguracionCorrecta() {
        ClienteConfiableProperties.ClienteConfiableConfig companyConfig = config.getConfigFor(ClientType.COMPANY);

        assertAll(
            () -> assertEquals("MES", companyConfig.getAntiguedad().getMedicion()),
            () -> assertEquals(12.0, companyConfig.getAntiguedad().getMinimoMedicion()),
            () -> assertEquals(List.of("CRITICO"), companyConfig.getPerfilesNoConfiables()),
            () -> assertEquals(0, companyConfig.getLimiteChargeback())
        );
    }

    @Test
    void getTipos_CuandoSeCargaYaml_RetornaMapConAmbosTipos() {
        var tipos = config.getTipos();
        assertTrue(tipos.containsKey(ClientType.INDIVIDUAL));
        assertTrue(tipos.containsKey(ClientType.COMPANY));
    }
}
