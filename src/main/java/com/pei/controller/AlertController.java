package com.pei.controller;

import com.pei.domain.Transaction;
import com.pei.dto.Alert;
import com.pei.dto.Logins;
import com.pei.dto.TimeRangeRequest;
import com.pei.service.AlertService;
import org.springframework.http.ResponseEntity;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.pei.service.GeolocalizationService;
import com.pei.service.TransactionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.pei.dto.TransferRequest;
import com.pei.dto.UserTransaction;
import com.pei.service.AccountService;

@RestController
@RequestMapping("/api")
public class AlertController {

    private final TransactionService transactionService;
    private final GeolocalizationService geolocalizationService;
    private final AlertService alertService;
    private final AccountService accountService;

    public AlertController(AlertService alertService,
                AccountService accountService, TransactionService transactionService,
                GeolocalizationService geolocalizationService) {
        this.alertService = alertService;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.geolocalizationService = geolocalizationService;
    }

    @PostMapping("/alerta-money-mule")
    public ResponseEntity<Alert> detectMoneyMule(@RequestBody List<Transaction> transactions) {
        try {
            boolean alertFlag = alertService.verifyMoneyMule(transactions);
            //TODO: Deberíamos obtener el ID del usuario con Spring Security, pero aun no esta implementado
            // Por ahora, asumimos que las transacciones tienen un usuario asociado
            Long userId = transactions.isEmpty() ? null : transactions.get(0).getUser().getId();

            if (alertFlag) {
                return ResponseEntity.ok(new Alert(userId, "Alerta: Posible Money Mule detectado del usuario " + userId));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

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

    @PostMapping("/alerta-aprobaciones")
    public ResponseEntity<Alert> evaluateApprovals(@RequestBody Long transactionId){
        Alert alerta = alertService.approvalAlert(transactionId);
        if (alerta != null) {
            return ResponseEntity.ok(alerta);
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/alerta-horario")
    public ResponseEntity<Alert> evaluateTransactionOutOfTimeRange(@RequestBody TimeRangeRequest request) {
        Alert alerta = alertService.timeRangeAlert(request.getTransactions(), request.getNewTransaction());
        if (alerta != null) {
            return ResponseEntity.ok(alerta);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/alerta-dispositivo")
    public ResponseEntity<Alert> checkDeviceLocalization(@RequestBody Logins login) {
        try {
            if (login == null) {
                return ResponseEntity.badRequest().build();
            }
            Alert alerta = geolocalizationService.verifyFraudOfDeviceAndGeolocation(login);
            return ResponseEntity.ok(alerta);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

}
