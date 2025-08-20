package com.pei.service.alertnotificator;

import com.pei.dto.TransactionDTO;

@FunctionalInterface
public interface AlertNotificatorStrategy {
    // Metodo para enviar una alerta crítica a un usuario específico
    void sendCriticalAlert(Long userId, TransactionDTO transactionDTO) throws AlertNotificatorException;
}
