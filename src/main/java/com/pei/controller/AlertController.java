package com.pei.controller;

import com.pei.domain.Transaction;
import com.pei.dto.Alert;
import com.pei.dto.TimeRangeRequest;
import com.pei.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AlertController {

    @Autowired
    private AlertService alertService;


    @PostMapping("/alerta-aprobaciones")
    public ResponseEntity<Alert> evaluateApprovals(@RequestBody Long transactionId){
        Alert alerta = alertService.approvalAlert(transactionId);
        if (alerta != null) {
            return ResponseEntity.ok(alerta);
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/alerta-horario")
    public ResponseEntity<Alert> evaluateTransactionOutOfTimeRange(@RequestBody TimeRangeRequest request){
        Alert alerta = alertService.timeRangeAlert(request.getTransactions(), request.getNewTransaction());
        if (alerta != null) {
            return ResponseEntity.ok(alerta);
        }
        return ResponseEntity.notFound().build();
    }

}
