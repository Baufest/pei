package com.pei.service.alertnotificator;


import com.pei.dto.TransactionDTO;
import org.springframework.stereotype.Service;

// Servicio para gestionar la notificación de alertas críticas
// Utiliza una estrategia de notificación que puede ser configurada en tiempo de ejecución (Por default es AlertNotificatorEmail)
// Permite cambiar la estrategia de notificación sin modificar el código del servicio
// Maneja excepciones específicas relacionadas con la notificación de alertas
@Service
public class AlertNotificatorService {

    private AlertNotificatorStrategy alertNotificatorStrategy;

    private AlertNotificatorService(AlertNotificatorStrategy alertNotificatorStrategy) {
        this.alertNotificatorStrategy = alertNotificatorStrategy;
    }

    public void setAlertNotificatorStrategy(AlertNotificatorStrategy alertNotificatorStrategy) {
        this.alertNotificatorStrategy = alertNotificatorStrategy;
    }

    public void executeNotificator(Long userId, TransactionDTO transactionDTO) throws AlertNotificatorException {
        try {
            alertNotificatorStrategy.sendCriticalAlert(userId, transactionDTO);
        } catch(Exception e) {
            throw new AlertNotificatorException(e.getMessage());
        }
    }
}
