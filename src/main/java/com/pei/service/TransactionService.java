package com.pei.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

import com.pei.domain.Transaction;
import com.pei.dto.Alert;
import com.pei.repository.ChargebackRepository;
import com.pei.repository.PurchaseRepository;

@Service
public class TransactionService {

    private final ChargebackRepository chargebackRepository;
    private final PurchaseRepository purchaseRepository;

    public TransactionService(ChargebackRepository chargebackRepository, PurchaseRepository purchaseRepository) {
        this.chargebackRepository = chargebackRepository;
        this.purchaseRepository = purchaseRepository;}

    //TODO: Probablemente tengamos que hacer una Query SQL para obtener las transacciones, sería más performante
    public List<Transaction> getLast24HoursTransactions(List<Transaction> transactions) {
        return transactions.stream()
            .filter(transaction -> transaction.getDate().isAfter(
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
  
    public Alert getChargebackFraudAlert(Long userId) {
        int numberOfChargebacks = chargebackRepository.findByUserId(userId).size();
        int numberOfPurchases = purchaseRepository.findByUserId(userId).size();
        Alert chargebackFraudAlert = null;

        if (numberOfPurchases == 0 && numberOfChargebacks > 0) {
            chargebackFraudAlert = new Alert(
                    userId, 
                    "Chargeback fraud detected for user " + userId
                );
        }

        if (numberOfPurchases > 0) {
            if ((double) numberOfChargebacks / numberOfPurchases > 0.1) {
                chargebackFraudAlert = new Alert(
                    userId, 
                    "Chargeback fraud detected for user " + userId
                );
            }
        }

        return chargebackFraudAlert;
    }
}
