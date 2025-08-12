package com.pei.controller;

import com.pei.dto.Alert;
import com.pei.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alerta-aprobaciones")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @PostMapping
    public ResponseEntity<Alert> evaluateApprovals(@RequestBody Long transactionId){
        Alert alerta = alertService.approvalAlert(transactionId);
        if (alerta != null) {
            return ResponseEntity.ok(alerta);
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

}
