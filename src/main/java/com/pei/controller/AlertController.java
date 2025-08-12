package com.pei.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pei.dto.Alert;
import com.pei.dto.TransferRequest;
import com.pei.dto.UserTransaction;
import com.pei.service.AccountService;


@RestController
@RequestMapping("/api")
public class AlertController {

    @Autowired
    AccountService accountService;

    @PostMapping("/alerta-cuenta-nueva")
    public ResponseEntity<Alert> validateNewAccountTransfers(@RequestBody TransferRequest transferReq) {
        try {
            Alert alert = accountService.validateNewAccountTransfers(transferReq.getDestinationAccount(), transferReq.getCurrentTransaction());
            return ResponseEntity.ok(alert);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Alert(null, "Error interno del servidor."));
        }
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

    @PostMapping("/alerta-perfil")
    public ResponseEntity<Alert> validateUserProfileTransaction(@RequestBody UserTransaction userTransaction) {
        try {
            Alert alert = accountService.validateUserProfileTransaction(userTransaction.getUser(), userTransaction.getTransaction());
            return ResponseEntity.ok(alert);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Alert(null, "Error interno del servidor."));
        }
    }

}

