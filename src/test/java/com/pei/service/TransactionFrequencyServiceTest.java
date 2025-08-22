package com.pei.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pei.config.CompanyClientFrequencyProperties;
import com.pei.config.IndividualClientFrequencyProperties;
import com.pei.domain.Transaction;
import com.pei.domain.Account.Account;
import com.pei.domain.Account.AccountType;
import com.pei.domain.User.ClientType;
import com.pei.domain.User.User;
import com.pei.domain.alerts.RecurringBeneficiaryAlert;
import com.pei.repository.RecurringBeneficiaryAlertRepository;
import com.pei.service.exceptions.VerificadorBeneficiarioRecurrenteException;

@ExtendWith(MockitoExtension.class)
class TransactionFrequencyServiceTest {
    @Mock
    private RecurringBeneficiaryAlertRepository recurringBeneficiaryAlertRepository;
    @Mock
    private TransactionService transactionService;

    private IndividualClientFrequencyProperties individualProps;
    private CompanyClientFrequencyProperties companyProps;

    @InjectMocks
    private TransactionFrequencyService transactionFrequencyService;

    @BeforeEach
    void setUp() {
        individualProps = new IndividualClientFrequencyProperties();
        individualProps.setCheckWindowHs(24);
        individualProps.setMaxTransfersSameBeneficiary(3);
        individualProps.setMaxDepositsAccountHolder(2);
        individualProps.setTypesAccountHolder(Arrays.asList("CUENTA_CORRIENTE", "CUENTA_AHORROS"));

        companyProps = new CompanyClientFrequencyProperties();
        companyProps.setCheckWindowHs(24);
        companyProps.setMaxTransfersSameBeneficiary(2);
        companyProps.setMaxDepositsAccountHolder(2);
        companyProps.setTypesAccountHolder(Arrays.asList("CUENTA_CORRIENTE", "CUENTA_AHORROS"));

        transactionFrequencyService = new TransactionFrequencyService(
                individualProps, companyProps, recurringBeneficiaryAlertRepository, transactionService);
    }

    @Test
    void analyzeTransactionFrequency_CuandoTransaccionesNulas_NoGuardaAlertas() {
        when(transactionService.getAllTransactionsByUserId(anyLong())).thenReturn(null);
        Executable executable = () -> transactionFrequencyService.analyzeTransactionFrequency(anyLong());
        assertAll(
            () -> assertThrows(VerificadorBeneficiarioRecurrenteException.class, executable)
        );
        verify(recurringBeneficiaryAlertRepository, never()).saveAll(anyList());
    }

    @Test
    void analyzeTransactionFrequency_CuandoTransaccionesVacias_NoGuardaAlertas() {
        when(transactionService.getAllTransactionsByUserId(anyLong())).thenReturn(Collections.emptyList());
        Executable executable = () -> transactionFrequencyService.analyzeTransactionFrequency(anyLong());
        assertThrows(VerificadorBeneficiarioRecurrenteException.class, executable);
        verify(recurringBeneficiaryAlertRepository, never()).saveAll(anyList());
    }

    @Test
    void analyzeTransactionFrequency_CuandoIndividualSuperaLimiteTransferencias_GuardaAlerta() {
        User user = crearUsuario(1L, ClientType.INDIVIDUAL);
        Account cuentaDestino = crearCuenta(10L, AccountType.CUENTA_CORRIENTE, user);
        List<Transaction> transacciones = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            transacciones.add(crearTransaccion(user, cuentaDestino, LocalDateTime.now().minusHours(1)));
        }
        when(transactionService.getAllTransactionsByUserId(anyLong())).thenReturn(transacciones);
        transactionFrequencyService.analyzeTransactionFrequency(anyLong());
        ArgumentCaptor<List<RecurringBeneficiaryAlert>> captor = ArgumentCaptor.forClass(List.class);
        verify(recurringBeneficiaryAlertRepository).saveAll(captor.capture());
        List<RecurringBeneficiaryAlert> alerts = captor.getValue();
        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0).getMessage().contains("demasiados depósitos"));
    }

    @Test
    void analyzeTransactionFrequency_CuandoIndividualSuperaLimiteDepositos_GuardaAlerta() {
        User user = crearUsuario(2L, ClientType.INDIVIDUAL);
        Account cuentaDestino = crearCuenta(20L, AccountType.CUENTA_CORRIENTE, user);
        List<Transaction> transacciones = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            transacciones.add(crearTransaccion(user, cuentaDestino, LocalDateTime.now().minusHours(2)));
        }
        when(transactionService.getAllTransactionsByUserId(anyLong())).thenReturn(transacciones);
        transactionFrequencyService.analyzeTransactionFrequency(anyLong());
        ArgumentCaptor<List<RecurringBeneficiaryAlert>> captor = ArgumentCaptor.forClass(List.class);
        verify(recurringBeneficiaryAlertRepository).saveAll(captor.capture());
        List<RecurringBeneficiaryAlert> alerts = captor.getValue();
        assertNotNull(alerts);
        assertEquals(1, alerts.size());
        assertTrue(alerts.get(0).getMessage().contains("demasiados depósitos"));
    }

    @Test
    void analyzeTransactionFrequency_CuandoEmpresaSuperaLimiteTransferencias_GuardaAlerta() {
        User user = crearUsuario(3L, ClientType.COMPANY);
        User otro = crearUsuario(4L, ClientType.COMPANY);
        Account cuentaDestino = crearCuenta(30L, AccountType.CUENTA_CORRIENTE, otro);
        List<Transaction> transacciones = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            transacciones.add(crearTransaccion(user, cuentaDestino, LocalDateTime.now().minusHours(3)));
        }
        when(transactionService.getAllTransactionsByUserId(anyLong())).thenReturn(transacciones);
        transactionFrequencyService.analyzeTransactionFrequency(anyLong());
        ArgumentCaptor<List<RecurringBeneficiaryAlert>> captor = ArgumentCaptor.forClass(List.class);
        verify(recurringBeneficiaryAlertRepository).saveAll(captor.capture());
        List<RecurringBeneficiaryAlert> alerts = captor.getValue();

        assertAll(
            () -> assertNotNull(alerts),
            () -> assertEquals(1, alerts.size()),
            () -> assertEquals(alerts.get(0).getIdBeneficiary(), user.getId())
        );
    }

    @Test
    void analyzeTransactionFrequency_CuandoEmpresaNoSuperaLimite_NoGuardaAlerta() {
        User user = crearUsuario(5L, ClientType.COMPANY);
        User otro = crearUsuario(6L, ClientType.COMPANY);
        Account cuentaDestino = crearCuenta(40L, AccountType.CUENTA_CORRIENTE, otro);
        List<Transaction> transacciones = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            transacciones.add(crearTransaccion(user, cuentaDestino, LocalDateTime.now().minusDays(4)));
        }
        when(transactionService.getAllTransactionsByUserId(anyLong())).thenReturn(transacciones);
        transactionFrequencyService.analyzeTransactionFrequency(anyLong());
        verify(recurringBeneficiaryAlertRepository, never()).saveAll(anyList());
    }

    private User crearUsuario(Long id, ClientType tipo) {
        User user = mock(User.class);
        lenient().when(user.getId()).thenReturn(id);
        lenient().when(user.getClientType()).thenReturn(tipo);
        return user;
    }

    private Account crearCuenta(Long id, AccountType tipo, User owner) {
        Account cuenta = mock(Account.class);
        lenient().when(cuenta.getId()).thenReturn(id);
        lenient().when(cuenta.getAccountType()).thenReturn(tipo);
        lenient().when(cuenta.getOwner()).thenReturn(owner);
        return cuenta;
    }

    private Transaction crearTransaccion(User user, Account cuentaDestino, LocalDateTime fecha) {
        Transaction tx = mock(Transaction.class);
        lenient().when(tx.getUser()).thenReturn(user);
        lenient().when(tx.getDestinationAccount()).thenReturn(cuentaDestino);
        lenient().when(tx.getDate()).thenReturn(fecha);
        return tx;
    }
}
