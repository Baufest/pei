package com.pei.controller;
import com.pei.domain.Transaction;
import com.pei.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pei.dto.Alert;

@RestController 
@RequestMapping("/api")
public class AlertController {
    

    private final AccountService accountService;
    private final AlertService alertService;


    public AlertController(
                AccountService accountService,
                AlertService alertService
    ) {
        this.accountService = accountService;
        this.alertService = alertService;
    }

    @GetMapping("/alerta-cliente-alto-riesgo/{userId}")
    public ResponseEntity<Alert> validateHighRiskClient(@PathVariable Long userId) {
        try {
            Alert alert = accountService.validateHighRiskClient(userId);
            return ResponseEntity.ok(alert);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Alert(null, "Error interno del servidor."));
        }
    }

    @PostMapping("/alerta-canales")
    public ResponseEntity<Alert> evaluatecriticalityAndSendAlert(@RequestBody Transaction transaction) {

        Alert alerta = alertService.alertCriticality(transaction);
        if (alerta != null) {
            return ResponseEntity.ok(alerta);
        }
        return ResponseEntity.notFound().build();
    }
}
