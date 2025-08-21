package com.pei.service.alertnotificator;

import com.pei.dto.TransactionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AlertNotificatorSms implements AlertNotificatorStrategy {
    private final Logger logger = LoggerFactory.getLogger(AlertNotificatorSms.class);

    @Override
    public void sendCriticalAlert(Long userId, TransactionDTO transactionDTO) {
        try {
            logger.info("Enviando SMS de alerta crítica al usuario con ID: {}", userId);
        } catch (Exception e) {
            logger.error("Error al enviar SMS de alerta crítica: {}", e.getMessage());
            throw new RuntimeException("Error al enviar SMS de alerta crítica", e);
        }
    }
}
