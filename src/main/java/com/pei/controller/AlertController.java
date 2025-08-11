package com.pei.controller;


import com.pei.model.Transaction;
import com.pei.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class AlertController {

    TransactionService transactionService;

    public AlertController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<?> detectMoneyMule(@RequestBody List<Transaction> transactions) {
        boolean alertFlag = verifyMoneyMule(transactions);


        if (alertFlag) {
            return ResponseEntity.ok("Alerta: posible actividad de Money Mule");
        } else {
            return ResponseEntity.ok("Sin alertas");
        }
    }

    private boolean verifyMoneyMule(List<Transaction> transactions) {
        List<Transaction> last24HoursTransactions = transactionService.getLast24HoursTransactions(transactions);

        BigDecimal totalDeposits = transactionService.totalDeposits(transactions);
        BigDecimal totalTransfers = transactionService.totalTransfers(transactions);

        // Si en 24 horas: sum(depósitos) > 5 y sum(transferencias) >= 0.8 * sum(depósitos) → alerta.
        return totalDeposits.compareTo(BigDecimal.valueOf(5)) > 0 &&
                totalTransfers.compareTo(totalDeposits.multiply(BigDecimal.valueOf(0.8))) >= 0;
    }
}
