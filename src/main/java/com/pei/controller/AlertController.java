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
    private final TransactionService transactionService;
    private final ClienteService clienteService;

    public AlertController(
                AccountService accountService,
                AlertService alertService,
                ClienteService clienteService,
                TransactionService transactionService
    ) {
        this.accountService = accountService;
        this.alertService = alertService;
        this.transactionService = transactionService;
        this.clienteService = clienteService;
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

    @GetMapping("/alerta-fast-multiple-transaction/{userId}")
    public ResponseEntity<Alert> getFastMultipleTransactionsAlert(@PathVariable Long userId) {
        
        String clientType = clienteService.getClientType(userId);
        if (clientType == null || (!clientType.equals("individuo") && !clientType.equals("empresa"))) {
            return ResponseEntity.notFound().build();
        }
        
        Alert alert = transactionService.getFastMultipleTransactionAlert(userId, clientType);

        if (alert != null) {
            return ResponseEntity.ok(alert);
        } else {
            return ResponseEntity.notFound().build();
        }

    }
}
