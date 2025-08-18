package com.pei.repository;

import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("Debe devolver las transferencias más recientes de un usuario a cuenta de otro usuario")
    void findRecentTransferByUserId_shouldReturnMostTransfer() {
        // Crear usuarios
        User user1 = new User();
        user1.setName("Juan");
        user1.setRisk("LOW");
        user1.setProfile("NORMAL");
        user1.setEmail("juan@mail.com");
        user1.setAverageMonthlySpending(BigDecimal.valueOf(1000));

        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Pedro");
        user2.setRisk("LOW");
        user2.setProfile("NORMAL");
        user2.setEmail("pedro@mail.com");
        user2.setAverageMonthlySpending(BigDecimal.valueOf(2000));
        userRepository.save(user2);

        // Crear cuentas
        Account accountUser1 = new Account(user1);
        accountUser1.setType("SAVINGS");
        accountRepository.save(accountUser1);

        Account accountUser2 = new Account(user2);
        accountUser2.setType("SAVINGS");
        accountRepository.save(accountUser2);

        // Crear transacciones
        Transaction oldTransaction = new Transaction(
            user1,
            BigDecimal.valueOf(100),
            LocalDateTime.now().minusDays(1),
            accountUser1,
            accountUser2 // distinto dueño
        );
        transactionRepository.save(oldTransaction);

        Transaction latestTransaction1 = new Transaction(
            user1,
            BigDecimal.valueOf(200),
            LocalDateTime.now(), // Ultima Transacción
            accountUser1,
            accountUser2 // distinto dueño
        );
        transactionRepository.save(latestTransaction1);

        Transaction latestTransaction2 = new Transaction(
            user1,
            BigDecimal.valueOf(500),
            LocalDateTime.now().minusHours(4),
            accountUser1,
            accountUser2 // distinto dueño
        );
        transactionRepository.save(latestTransaction2);

        // La lista debe estar ordenada de forma que la última transacción sea la primera
        // y la más antigua sea la última.
        List<Transaction> expectedTransactions = List.of(latestTransaction1, latestTransaction2, oldTransaction);

        // Invocar query
        List<Transaction> result = transactionRepository.findRecentTransferByUserId(user1.getId());

        // Verificar
        assertAll(
            () -> assertFalse(result.isEmpty()),
            () -> assertArrayEquals(expectedTransactions.toArray(), result.toArray())
        );
    }

    @Test
    @DisplayName("No Debe devolver las transferencias más recientes de un usuario a cuenta de otro usuario por que No tiene transferencias")
    void Should_ReturnEmptyList_When_NoContainsTransfers() {
        // Crear usuarios
        User user1 = new User();
        user1.setName("Juan");
        user1.setRisk("LOW");
        user1.setProfile("NORMAL");
        user1.setAverageMonthlySpending(BigDecimal.valueOf(1000));
        user1.setEmail("juan@mail.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Pedro");
        user2.setRisk("LOW");
        user2.setProfile("NORMAL");
        user2.setAverageMonthlySpending(BigDecimal.valueOf(2000));
        user2.setEmail("pedro@mail.com");
        userRepository.save(user2);

        // Crear cuentas
        Account accountUser1 = new Account(user1);
        accountUser1.setType("SAVINGS");
        accountRepository.save(accountUser1);

        Account accountUser2 = new Account(user2);
        accountUser2.setType("SAVINGS");
        accountRepository.save(accountUser2);

        // Crear transacciones
        Transaction oldTransaction = new Transaction(
            user1,
            BigDecimal.valueOf(100),
            LocalDateTime.now().minusDays(1),
            accountUser2,
            accountUser1 // distinto dueño
        );
        transactionRepository.save(oldTransaction);

        Transaction latestTransaction1 = new Transaction(
            user1,
            BigDecimal.valueOf(200),
            LocalDateTime.now(), // Ultima Transacción
            accountUser2,
            accountUser1 // distinto dueño
        );
        transactionRepository.save(latestTransaction1);

        Transaction latestTransaction2 = new Transaction(
            user1,
            BigDecimal.valueOf(500),
            LocalDateTime.now().minusHours(4),
            accountUser2,
            accountUser1 // distinto dueño
        );
        transactionRepository.save(latestTransaction2);

        // La lista debe estar ordenada de forma que la última transacción sea la primera
        // y la más antigua sea la última.
        List<Transaction> expectedTransactions = List.of();

        // Invocar query
        List<Transaction> result = transactionRepository.findRecentTransferByUserId(user1.getId());

        // Verificar
        assertAll(
            () -> assertTrue(result.isEmpty()),
            () -> assertArrayEquals(expectedTransactions.toArray(), result.toArray())
        );
    }

    @Test
    @DisplayName("Debe devolver solo las transferencias más recientes de un usuario a cuenta de otro usuario cuando contiene transferencias y depósitos")
    void Should_ReturnOnlyTransfers_When_ContainsTransfersAndDeposits() {
        // Crear usuarios
        User user1 = new User();
        user1.setName("Juan");
        user1.setRisk("LOW");
        user1.setProfile("NORMAL");
        user1.setAverageMonthlySpending(BigDecimal.valueOf(1000));
        user1.setEmail("juan@mail.com");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("Pedro");
        user2.setRisk("LOW");
        user2.setProfile("NORMAL");
        user2.setEmail("pedro@mail.com");
        user2.setAverageMonthlySpending(BigDecimal.valueOf(2000));
        userRepository.save(user2);

        // Crear cuentas
        Account accountUser1 = new Account(user1);
        accountUser1.setType("SAVINGS");
        accountRepository.save(accountUser1);

        Account accountUser2 = new Account(user2);
        accountUser2.setType("SAVINGS");
        accountRepository.save(accountUser2);

        // Crear transacciones
        Transaction transfer1 = new Transaction(
            user1,
            BigDecimal.valueOf(100),
            LocalDateTime.now().minusDays(1),
            accountUser1,
            accountUser2 // distinto dueño
        );
        transactionRepository.save(transfer1);

        Transaction deposit = new Transaction(
            user1,
            BigDecimal.valueOf(200),
            LocalDateTime.now(),
            accountUser2,
            accountUser1 // Depósito
        );
        transactionRepository.save(deposit);

        Transaction transfer2 = new Transaction(
            user1,
            BigDecimal.valueOf(500),
            LocalDateTime.now().minusHours(4),
            accountUser1,
            accountUser2 // distinto dueño
        );
        transactionRepository.save(transfer2);

        // La lista debe contener solo las transferencias ordenadas, no los depósitos
        List<Transaction> expectedTransactions = List.of(transfer2, transfer1);

        // Invocar query
        List<Transaction> result = transactionRepository.findRecentTransferByUserId(user1.getId());

        // Verificar
        assertAll(
            () -> assertFalse(result.isEmpty()),
            () -> assertArrayEquals(expectedTransactions.toArray(), result.toArray())
        );
    }
}
