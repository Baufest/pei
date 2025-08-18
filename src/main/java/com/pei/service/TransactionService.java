package com.pei.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.pei.dto.Alert;
import com.pei.repository.TransactionRepository;


@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionVelocityDetectorService transactionVelocityDetectorService;


    public TransactionService(TransactionRepository transactionRepository, 
    TransactionVelocityDetectorService transactionVelocityDetectorService) {
        this.transactionRepository = transactionRepository;
        this.transactionVelocityDetectorService = transactionVelocityDetectorService;
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