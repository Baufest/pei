package com.pei.service;

import com.pei.config.AlertProperties;
import com.pei.domain.Account.Account;
import com.pei.domain.Transaction;
import com.pei.domain.*;
import com.pei.domain.User.User;
import com.pei.dto.*;
import com.pei.repository.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.stream.Collectors;


import static com.pei.service.CriticalityService.getCriticality;

@Service
public class AlertService {

    private TransactionService transactionService;
    private NotificationService notificationService;
    private TransactionRepository transactionRepository;
    private LoginRepository loginRepository;

    public AlertService(TransactionService transactionService, NotificationService notificationService,  TransactionRepository transactionRepository, LoginRepository loginRepository) {
        this.notificationService = notificationService;
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
        this.loginRepository = loginRepository;
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

    public List<Account> verifyMultipleAccountsCashNotRelated(List<Transaction> transactions) {

        /* return a lista de cuentas que recibieron cash de otras cuentas > 2 */
        List<Account> listReturn = transactions.stream().map(Transaction::getDestinationAccount)
                .distinct()
                .filter(destinationAccount -> transactionService.getLast24HoursTransactions(transactions).stream()
                        .map(Transaction::getSourceAccount)
                        .distinct()
                        .count() > 2)
                .toList();
        return listReturn.isEmpty() ? List.of() : listReturn;
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

    /**
     * Método principal que evalúa la transacción y devuelve la primera alerta encontrada.
     * La lógica de verificación de monto y comportamiento está delegada a métodos auxiliares.
     */
    public Alert evaluateTransactionBehavior(Long idTransaction, Long idLogin) {
        // Traer los objetos desde la base de datos
        Transaction transaction = transactionRepository.findById(idTransaction)
            .orElseThrow(() -> new RuntimeException("Transaction no encontrada"));

        Login login = loginRepository.findById(idLogin)
            .orElseThrow(() -> new RuntimeException("Login no encontrado"));

        User user = transaction.getUser();

        // 1. Verificación de monto inusual
        Alert amountAlert = transactionService.checkUnusualAmount(user, transaction.getAmount());
        if (amountAlert != null) {
            return amountAlert;
        }

        // 2. Verificación de comportamiento inusual
        Alert behaviorAlert = transactionService.checkUnusualBehavior(user, transaction, login);
        if (behaviorAlert != null) {
            return behaviorAlert;
        }

        return new Alert(user.getId(), "Alerta estándar");
    }




}
