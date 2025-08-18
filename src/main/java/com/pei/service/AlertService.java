package com.pei.service;

import com.pei.domain.Transaction;
import com.pei.dto.Alert;
import org.springframework.stereotype.Service;

import static com.pei.service.CriticalityService.getCriticality;

@Service
public class AlertService {

    private NotificationService notificationService;

    public AlertService(NotificationService notificationService) {
        this.notificationService = notificationService;
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
