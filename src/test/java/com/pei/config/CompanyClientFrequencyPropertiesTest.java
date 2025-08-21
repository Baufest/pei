package com.pei.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CompanyClientFrequencyPropertiesTest {
    @Autowired
    private CompanyClientFrequencyProperties config;

    @Test
    void InitializeWithCorrectDefaultProperties() {
        List<String> accountHolderTypesExpected = List.of(
            "CUENTA_CORRIENTE", "CUENTA_AHORRO", "CUENTA_PLAZO_FIJO", "CUENTA_INVERSION");

        assertAll(
            () -> assertEquals(24, config.getCheckWindowHs()),
            () -> assertEquals(10, config.getMaxDepositsAccountHolder()),
            () -> assertEquals(5, config.getMaxTransfersSameBeneficiary()),
            () -> assertArrayEquals(accountHolderTypesExpected.toArray(), config.getTypesAccountHolder().toArray())
        );
    }
}
