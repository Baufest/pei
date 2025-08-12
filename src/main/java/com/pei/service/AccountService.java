package com.pei.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.dto.Alert;

@Service
public class AccountService {


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
}
