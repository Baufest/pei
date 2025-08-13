package com.pei.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.pei.dto.Alert;
import com.pei.service.GeolocalizationService;
import com.pei.service.TransactionService;

@RestController
public class AlertController {
    
    private final TransactionService transactionService;
    private final GeolocalizationService geolocalizationService;

    public AlertController(TransactionService transactionService, 
                GeolocalizationService geolocalizationService) {
        this.transactionService = transactionService;
        this.geolocalizationService = geolocalizationService;
    }

    @GetMapping("/alerta-chargeback/{userId}")
    public ResponseEntity<Alert> getChargebackAlert(@PathVariable Long userId) {
        Alert alert = transactionService.getChargebackFraudAlert(userId);

        if (alert != null) {
            return ResponseEntity.ok(alert);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/alerta-logins/{userId}")
    public ResponseEntity<Alert> getLoginAlert(@PathVariable Long userId) {
        Alert alert = geolocalizationService.getLoginAlert(userId);

        if (alert != null) {
            return ResponseEntity.ok(alert);
        } else {
            return ResponseEntity.notFound().build();
        }

    }


}
