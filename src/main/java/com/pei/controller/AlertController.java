package com.pei.controller;

import com.pei.dto.Alert;
import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.service.AlertService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AlertController {

    private AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping("/alerta-money-mule")
    public ResponseEntity<Alert> detectMoneyMule(@RequestBody List<Transaction> transactions) {
        boolean alertFlag = alertService.verifyMoneyMule(transactions);
        // TODO: Deberíamos obtener el ID del usuario con Spring Security, pero aun no
        // esta implementado
        // Por ahora, asumimos que las transacciones tienen un usuario asociado
        Long userId = transactions.isEmpty() ? null : transactions.get(0).getUser().getId();

        if (alertFlag) {
            return ResponseEntity.ok(new Alert(userId, "Alerta: Posible Money Mule detectado del usuario " + userId));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/alerta-red-transacciones")
    public ResponseEntity<Alert> checkMultipleAccountsCashNotRelated(@RequestBody List<Transaction> transactions) {
        try {
            /* si no viene nada, manda 404 */
            if (transactions == null) {
                return ResponseEntity.badRequest().build();
            }

            /* deberia controlar que ninguna transaccion sea nula */
            if (transactions.stream().anyMatch(t -> t == null)) {
                return ResponseEntity.badRequest().build();
            }

            List<Account> alertAccounts = alertService.verifyMultipleAccountsCashNotRelated(transactions);

            if (alertAccounts.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                Long userId = alertAccounts.get(0).getOwner().getId() != null
                        ? alertAccounts.get(0).getOwner().getId()
                        : null;
                return ResponseEntity.ok(new Alert(userId,
                        "Alert: Multiples transactions not related to the account of " + userId + " detected"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
