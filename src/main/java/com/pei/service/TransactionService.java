package com.pei.service;

import com.pei.domain.*;
import com.pei.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private  TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public int getApprovalCount(Long transactionId){
        Transaction transaction =  transactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("Transaction not found in database"));
        return transaction.getApprovalList().size();
    }

    public TimeRange getAvgTimeRange(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("Transactions list was empty.");
        }
        int minHour = Integer.MAX_VALUE;
        int maxHour = Integer.MIN_VALUE;

        for (Transaction transaction : transactions) {
            LocalDateTime dateHour = transaction.getDateHour();
            int hora = dateHour.getHour();

            if (hora < minHour) minHour = hora;
            if (hora > maxHour) maxHour = hora;
        }
        return new TimeRange(minHour, maxHour);
    }

}
