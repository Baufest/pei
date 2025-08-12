package com.pei.service;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.dto.Alert;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para AccountService")
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private Account cuentaDestino;

    @Mock
    private Transaction transaccionActual;

    @Nested
    @DisplayName("validarTransferenciasCuentasRecienCreadas")
    class ValidarTransferenciasCuentasRecienCreadasTests {

        @Test
        @DisplayName("Debe alertar si la cuenta fue creada hace menos de 48 horas")
        void testCuentaCreadaHaceMenosDe48Horas() {
            LocalDateTime now = LocalDateTime.now();
            when(transaccionActual.getDate()).thenReturn(now);
            when(cuentaDestino.getCreationDate()).thenReturn(now.minusHours(24));

            Alert alert = accountService.validarTransferenciasCuentasRecienCreadas(cuentaDestino, transaccionActual);

            assertEquals("Alerta: Se transfiere dinero a una cuenta creada hace menos de 48 horas.", alert.getMessage());
        }

        @Test
        @DisplayName("Debe permitir transferencia si la cuenta fue creada hace más de 48 horas")
        void testCuentaCreadaHaceMasDe48Horas() {
            LocalDateTime now = LocalDateTime.now();
            when(transaccionActual.getDate()).thenReturn(now);
            when(cuentaDestino.getCreationDate()).thenReturn(now.minusHours(72));

            Alert alert = accountService.validarTransferenciasCuentasRecienCreadas(cuentaDestino, transaccionActual);

            assertEquals("Transferencia permitida.", alert.getMessage());
        }

        @Test
        @DisplayName("Debe permitir transferencia si la cuenta fue creada exactamente hace 48 horas")
        void testCuentaCreadaExactamente48Horas() {
            LocalDateTime now = LocalDateTime.now();
            when(transaccionActual.getDate()).thenReturn(now);
            when(cuentaDestino.getCreationDate()).thenReturn(now.minusHours(48));

            Alert alert = accountService.validarTransferenciasCuentasRecienCreadas(cuentaDestino, transaccionActual);

            assertEquals("Transferencia permitida.", alert.getMessage());
        }

        @Test
        @DisplayName("Debe permitir transferencia si la cuenta fue creada después de la transacción")
        void testCuentaCreadaDespuesDeTransaccion() {
            LocalDateTime now = LocalDateTime.now();
            when(transaccionActual.getDate()).thenReturn(now);
            when(cuentaDestino.getCreationDate()).thenReturn(now.plusHours(1));

            Alert alert = accountService.validarTransferenciasCuentasRecienCreadas(cuentaDestino, transaccionActual);

            assertEquals("Transferencia permitida.", alert.getMessage());
        }
    }
}
