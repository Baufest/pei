package com.pei.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import com.pei.dto.Alert;
import com.pei.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para AccountService")
class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private Account cuentaDestino;

    @Mock
    private Transaction transaccionActual;

    @Mock
    private UserRepository userRepository;

    @Nested
    @DisplayName("Tests for validateHighRiskClient")
    class ValidateHighRiskClientTests {

        @Test
        @DisplayName("Debe retornar alerta de alto riesgo si el usuario es de alto riesgo")
        void testUsuarioAltoRiesgo() {
            Long userId = 1L;
            User user = new User();
            user.setHighRisk(true);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            Alert alert = accountService.validateHighRiskClient(userId);

            assertEquals("Alerta: El cliente es de alto riesgo.", alert.description());
        }

        @Test
        @DisplayName("Debe retornar alerta de bajo riesgo si el usuario es de bajo riesgo")
        void testUsuarioBajoRiesgo(){
            Long userId = 2L;
            User user = new User();
            user.setHighRisk(false);

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            Alert alert = accountService.validateHighRiskClient(userId);

            assertEquals("Cliente verificado como de bajo riesgo.", alert.description());
        }

        @Test
        @DisplayName("Debe retornar alerta de usuario no encontrado si el usuario no es encontrado")
        void testUsuarioNoEncontrado(){
            Long userId = 3L;

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            Alert alert = accountService.validateHighRiskClient(userId);

            assertEquals("Alerta: Usuario no encontrado.", alert.description());
        }

    }

    @Nested
    @DisplayName("validateNewAccountTransfers")
    class ValidateNewAccountTransfersTests {

        @Test
        @DisplayName("Debe alertar si la cuenta fue creada hace menos de 48 horas")
        void testCuentaCreadaHaceMenosDe48Horas() {
            LocalDateTime now = LocalDateTime.now();
            when(transaccionActual.getDate()).thenReturn(now);
            when(cuentaDestino.getCreationDate()).thenReturn(now.minusHours(24));

            Alert alert = accountService.validateNewAccountTransfers(cuentaDestino, transaccionActual);

            assertEquals("Alerta: Se transfiere dinero a una cuenta creada hace menos de 48 horas.",
                    alert.description());
        }

        @Test
        @DisplayName("Debe permitir transferencia si la cuenta fue creada hace más de 48 horas")
        void testCuentaCreadaHaceMasDe48Horas() {
            LocalDateTime now = LocalDateTime.now();
            when(transaccionActual.getDate()).thenReturn(now);
            when(cuentaDestino.getCreationDate()).thenReturn(now.minusHours(72));

            Alert alert = accountService.validateNewAccountTransfers(cuentaDestino, transaccionActual);

            assertEquals("Transferencia permitida.", alert.description());
        }

        @Test
        @DisplayName("Debe permitir transferencia si la cuenta fue creada exactamente hace 48 horas")
        void testCuentaCreadaExactamente48Horas() {
            LocalDateTime now = LocalDateTime.now();
            when(transaccionActual.getDate()).thenReturn(now);
            when(cuentaDestino.getCreationDate()).thenReturn(now.minusHours(48));

            Alert alert = accountService.validateNewAccountTransfers(cuentaDestino, transaccionActual);

            assertEquals("Transferencia permitida.", alert.description());
        }

        @Test
        @DisplayName("Debe permitir transferencia si la cuenta fue creada después de la transacción")
        void testCuentaCreadaDespuesDeTransaccion() {
            LocalDateTime now = LocalDateTime.now();
            when(transaccionActual.getDate()).thenReturn(now);
            when(cuentaDestino.getCreationDate()).thenReturn(now.plusHours(1));

            Alert alert = accountService.validateNewAccountTransfers(cuentaDestino, transaccionActual);

            assertEquals("Transferencia permitida.", alert.description());
        }
    }
}
