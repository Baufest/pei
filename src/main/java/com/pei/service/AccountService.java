package com.pei.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import com.pei.dto.Alert;
import com.pei.repository.UserRepository;

@Service
public class AccountService {

    private UserRepository userRepository;
    private ObjectMapper objectMapper;

    public AccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();
    }

    public Alert validateNewAccountTransfers(Account destinationAccount, Transaction currentTransaction) {
        LocalDateTime limitDate = currentTransaction.getDate().minusHours(48);

        // If the account creation date is between (48 hours before) && (transaction
        // date)
        if (destinationAccount.getCreationDate().isAfter(limitDate)
                && destinationAccount.getCreationDate().isBefore(currentTransaction.getDate())) {
            return new Alert(null, "Alerta: Se transfiere dinero a una cuenta creada hace menos de 48 horas.");
        } else {
            return new Alert(null, "Transferencia permitida.");
        }
    }

    public Alert validateHighRiskClient(Long userId) {
        String clientJson = ClienteService.obtenerClienteJson(userId);
        User user = null;
        try {
            user = objectMapper.readValue(clientJson, User.class);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return new Alert(userId, "Alerta: Error al procesar los datos del usuario.");
        }

        if (user != null && user.getRisk() != null) {
            if (user.getRisk().equals("alto")) {
                return new Alert(userId, "Alerta: El cliente es de alto riesgo.");
            } else {
                return new Alert(userId, "Cliente verificado como de bajo riesgo.");
            }
        } else {
            return new Alert(userId, "Alerta: Usuario no encontrado.");
        }
    }

    public Alert validateUserProfileTransaction(User user, Transaction transaction) {
        if (user == null || user.getProfile() == null) {
            return new Alert(null, "Alerta: Datos de usuario inválidos.");
        }

        if(transaction == null || transaction.getAmount() == null) {
            return new Alert(null, "Alerta: Datos de transacción inválidos.");
        }

        if (user.getAverageMonthlySpending() != null
                && transaction.getAmount().compareTo(user.getAverageMonthlySpending().multiply(java.math.BigDecimal.valueOf(3))) > 0
                && user.getProfile().equals("ahorrista")) {
            return new Alert(null, "Alerta: Monto inusual para perfil.");
        }

        return new Alert(null, "Perfil de usuario validado para la transacción.");
    }

}
