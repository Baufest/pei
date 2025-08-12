package com.pei.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import com.pei.dto.Alert;
import com.pei.repository.UserRepository;

@Service
public class AccountService {

    private UserRepository userRepository;

    public AccountService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public Alert validateNewAccountTransfers(Account destinationAccount, Transaction currentTransaction) {
        LocalDateTime limitDate = currentTransaction.getDate().minusHours(48);

        // If the account creation date is between (48 hours before) && (transaction date)
        if (destinationAccount.getCreationDate().isAfter(limitDate)
                && destinationAccount.getCreationDate().isBefore(currentTransaction.getDate())) {
            return new Alert(null, "Alerta: Se transfiere dinero a una cuenta creada hace menos de 48 horas.");
        } else {
            return new Alert(null, "Transferencia permitida.");
        }
    }

    public Alert validateHighRiskClient(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isHighRisk()) {
                return new Alert(userId, "Alerta: El cliente es de alto riesgo.");
            } else {
                return new Alert(userId, "Cliente verificado como de bajo riesgo.");
            }
        } else {
            return new Alert(userId, "Alerta: Usuario no encontrado.");
        }
    }
}
