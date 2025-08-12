package com.pei.service;

import com.pei.domain.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AlertService {
    private TransactionService transactionService;

    public AlertService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public boolean verifyMoneyMule(List<Transaction> transactions) {
        List<Transaction> last24HoursTransactions = transactionService.getLast24HoursTransactions(transactions);

        BigDecimal totalDeposits = transactionService.totalDeposits(transactions);
        BigDecimal totalTransfers = transactionService.totalTransfers(transactions);

        // Si en 24 horas: sum(depósitos) > 5 y sum(transferencias) >= 0.8 * sum(depósitos) → alerta.
        return totalDeposits.compareTo(BigDecimal.valueOf(5)) > 0 &&
            totalTransfers.compareTo(totalDeposits.multiply(BigDecimal.valueOf(0.8))) >= 0;
    }
}
