package com.pei.service;

import com.pei.domain.Transaction;
import com.pei.domain.*;
import com.pei.dto.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

import static com.pei.service.CriticalityService.getCriticality;

@Service
public class AlertService {
    private TransactionService transactionService;
    private NotificationService notificationService;

    public AlertService(TransactionService transactionService, NotificationService notificationService) {
        this.notificationService = notificationService;
        this.transactionService = transactionService;
    }

    public Alert approvalAlert(Long transactionId) {
        int approvalCount = transactionService.getApprovalCount(transactionId);
        Alert alerta = null;
        if (approvalCount > 2) {
            alerta = new Alert(transactionId, "Transacción con ID = " + transactionId + " tiene más de 2 aprobaciones");
        }
        return alerta;
    }
    public Alert timeRangeAlert(List<Transaction> transactions, Transaction transaction) {
        Alert alerta = null;
        TimeRange timeRange = transactionService.getAvgTimeRange(transactions);

        int lastHour = transaction.getDate().getHour();
        Long transactionId = transaction.getId();

        if (lastHour < timeRange.getMinHour() || lastHour > timeRange.getMaxHour()) {
            alerta = new Alert(transactionId, "Transacción con ID = " + transactionId + ", realizada fuera del rango de horas promedio: " + timeRange.getMinHour() + " - " + timeRange.getMaxHour());
        }
        return alerta;
    }


    public boolean verifyMoneyMule(List<Transaction> transactions) {
        List<Transaction> last24HoursTransactions = transactionService.getLast24HoursTransactions(transactions);

        BigDecimal totalDeposits = transactionService.totalDeposits(last24HoursTransactions);
        BigDecimal totalTransfers = transactionService.totalTransfers(last24HoursTransactions);

        if(totalDeposits == null || totalTransfers == null || last24HoursTransactions.isEmpty()) {
            return false; // No hay transacciones relevantes
        }

        // Si en 24 horas: sum(depósitos) > 5 y sum(transferencias) >= 0.8 * sum(depósitos) → alerta.
        return totalDeposits.compareTo(BigDecimal.valueOf(5)) > 0 &&
            totalTransfers.compareTo(totalDeposits.multiply(BigDecimal.valueOf(0.8))) >= 0;
    }

    public Alert alertCriticality(Transaction transaction) {

        String criticality = getCriticality(transaction.getUser().getId(),
        transaction.getAmount().doubleValue(), transaction.getDestinationAccount());

        if ("high".equalsIgnoreCase(criticality)) {
            notificationService.sendCriticalAlertEmail(transaction.getUser(), transaction);
            return new Alert(transaction.getId(), "Transacción con ID = " + transaction.getId() + " tiene criticidad alta. Se le notificará mediante Mail.");
        } else if ("medium".equalsIgnoreCase(criticality)) {
            return new Alert(transaction.getId(), "Transacción con ID = " + transaction.getId() + " tiene criticidad media. Se le notificará mediante SMS.");
        }
        else {
            return new Alert(transaction.getId(), "Transacción con ID = " + transaction.getId() + " tiene criticidad baja. Se le notificará mediante Slack.");
        }
    }
}
