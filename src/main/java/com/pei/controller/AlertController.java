package com.pei.controller;


import com.pei.dto.Alert;
import com.pei.domain.Transaction;
import com.pei.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AlertController {
    private AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping("/alerta-money-mule")
    public ResponseEntity<Alert> detectMoneyMule(@RequestBody List<Transaction> transactions) {
        boolean alertFlag = alertService.verifyMoneyMule(transactions);
        //TODO: Deberíamos obtener el ID del usuario con Spring Security, pero aun no esta implementado
        // Por ahora, asumimos que las transacciones tienen un usuario asociado
        Long userId = transactions.isEmpty() ? null : transactions.get(0).getUser().getId();

        if (alertFlag) {
            return ResponseEntity.ok(new Alert(userId, "Alerta: Posible Money Mule detectado del usuario " + userId));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
