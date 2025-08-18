package com.pei.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pei.dto.Alert;
import com.pei.service.AccountService;

@RestController 
@RequestMapping("/api")
public class AlertController {
    

    private final AccountService accountService;


    public AlertController(
                AccountService accountService
    ) {
        this.accountService = accountService;
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

}
