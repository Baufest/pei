package com.pei.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import com.pei.dto.Alert;
import com.pei.repository.UserRepository;

@Service
public class AccountService {

    @Autowired
    private UserRepository userRepository;

    public Alert validarTransferenciasCuentasRecienCreadas(Account cuentaDestino, Transaction transaccionActual) {
            LocalDateTime fechaLimite = transaccionActual.getDate().minusHours(48);

            //si la fecha de creacin de la cuenta está entre (48 hs antes) && (fecha de la transacción)
            if (cuentaDestino.getCreationDate().isAfter(fechaLimite) 
            && cuentaDestino.getCreationDate().isBefore(transaccionActual.getDate())) {
                return new Alert(null, "Alerta: Se transfiere dinero a una cuenta creada hace menos de 48 horas.");
            } else {
                return new Alert(null, "Transferencia permitida.");
            }
        
    }

    public Alert validarClienteAltoRiesgo(Long userId) {
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
