package com.pei.service;

import com.pei.domain.*;
import com.pei.repository.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.pei.dto.Alert;

@Service
public class TransactionService {

    private final ChargebackRepository chargebackRepository;
    private final PurchaseRepository purchaseRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionVelocityDetectorService transactionVelocityDetectorService;

    public TransactionService(ChargebackRepository chargebackRepository, PurchaseRepository purchaseRepository, TransactionRepository transactionRepository, TransactionVelocityDetectorService transactionVelocityDetectorService) {
        this.chargebackRepository = chargebackRepository;
        this.purchaseRepository = purchaseRepository;
        this.transactionRepository = transactionRepository;
        this.transactionVelocityDetectorService = transactionVelocityDetectorService;
    }

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
    public TimeRange getAvgTimeRange(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("Transactions list was empty.");
        }
        int minHour = Integer.MAX_VALUE;
        int maxHour = Integer.MIN_VALUE;

        for (Transaction transaction : transactions) {
            LocalDateTime dateHour = transaction.getDate();
            int hora = dateHour.getHour();

            if (hora < minHour) minHour = hora;
            if (hora > maxHour) maxHour = hora;
        }
        return new TimeRange(minHour, maxHour);
    }

    public int getApprovalCount(Long transactionId){
        Transaction transaction =  transactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found in database"));
        return transaction.getApprovalList().size();
    }

    public Alert getFastMultipleTransactionAlert(Long userId, String clientType) {

        Integer minutesRange = clientType.equals("individuo") ? 
            transactionVelocityDetectorService.getIndividuoMinutesRange() : 
            transactionVelocityDetectorService.getEmpresaMinutesRange();
        
        Integer maxTransactions = clientType.equals("individuo") ? 
            transactionVelocityDetectorService.getIndividuoMaxTransactions() : 
            transactionVelocityDetectorService.getEmpresaMaxTransactions();

        LocalDateTime fromDate = LocalDateTime.now().minusMinutes(minutesRange);
        Integer numMaxTransactions = maxTransactions;
        Integer numTransactions = transactionRepository.countTransactionsFromDate(userId, fromDate);

        if (numTransactions > numMaxTransactions){
            return new Alert(userId, "Fast multiple transactions detected for user " + userId);
        } 

        Alert fastMultipleTransactionAlert = null;
        return fastMultipleTransactionAlert;
    }
}
