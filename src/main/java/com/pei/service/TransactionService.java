package com.pei.service;

import com.pei.domain.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    //TODO: Probablemente tengamos que hacer una Query SQL para obtener las transacciones, sería más performante
    public List<Transaction> getLast24HoursTransactions(List<Transaction> transactions) {
        return transactions.stream()
            .filter(transaction -> transaction.getTransactionDate().isAfter(
                java.time.LocalDateTime.now().minusDays(1)))
            .toList();
    }

    public BigDecimal totalDeposits(List<Transaction> transactions) {
        // Obtengo los montos de los depósitos
        // donde la cuenta de destino es la del usuario
        return transactions.stream()
            .filter(transaction ->
                transaction.getDestinationAccount().getOwner().equals(transaction.getUser()))
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalTransfers(List<Transaction> transactions) {
        // Obtengo los montos de las transferencias
        // donde la cuenta de destino es diferente a la del usuario
        return transactions.stream()
            .filter(transaction ->
                !transaction.getDestinationAccount().getOwner().equals(transaction.getUser()))
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}