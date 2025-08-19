package com.pei.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.UserEvent.UserEvent;
import com.pei.dto.Alert;
import com.pei.dto.Logins;
import com.pei.dto.TimeRangeRequest;
import com.pei.dto.TransferRequest;
import com.pei.dto.UserTransaction;
import com.pei.service.AccountService;
import com.pei.service.AlertService;
import com.pei.service.ClienteService;
import com.pei.service.GeolocalizationService;
import com.pei.service.TransactionService;

@RestController
@RequestMapping("/api")
public class AlertController {

    private final TransactionService transactionService;
    private final GeolocalizationService geolocalizationService;
    private final AlertService alertService;
    private final AccountService accountService;
    private final ClienteService clienteService;

    public AlertController(AlertService alertService,
                AccountService accountService, TransactionService transactionService,
                GeolocalizationService geolocalizationService, ClienteService clienteService) {
        this.alertService = alertService;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.geolocalizationService = geolocalizationService;
        this.clienteService = clienteService;
        
    }

    @PostMapping("/alerta-money-mule")
    public ResponseEntity<Alert> detectMoneyMule(@RequestBody List<Transaction> transactions) {
        try {
            boolean alertFlag = alertService.verifyMoneyMule(transactions);
            // TODO: Deberíamos obtener el ID del usuario con Spring Security, pero aun no
            // esta implementado
            // Por ahora, asumimos que las transacciones tienen un usuario asociado
            Long userId = transactions.isEmpty() ? null : transactions.get(0).getUser().getId();

            if (alertFlag) {
                return ResponseEntity
                        .ok(new Alert(userId, "Alerta: Posible Money Mule detectado del usuario " + userId));
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
            Alert alert = accountService.validateNewAccountTransfers(transferReq.getDestinationAccount(),
                    transferReq.getCurrentTransaction());
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
            Alert alert = accountService.validateUserProfileTransaction(userTransaction.getUser(),
                    userTransaction.getTransaction());
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

    @PostMapping("/alerta-red-transacciones")
    public ResponseEntity<Alert> checkMultipleAccountsCashNotRelated(@RequestBody List<Transaction> transactions) {
        try {
            /* si no viene nada, manda 400 */
            if (transactions.isEmpty()) {
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
    public ResponseEntity<Alert> evaluateApprovals(@RequestBody Long transactionId) {
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

    @PostMapping("/alerta-canales")
    public ResponseEntity<Alert> evaluatecriticalityAndSendAlert(@RequestBody Transaction transaction) {

        Alert alerta = alertService.alertCriticality(transaction);
        if (alerta != null) {
            return ResponseEntity.ok(alerta);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/alerta-scoring")
    public ResponseEntity<Alert> checkProccesTransaction(@RequestBody Long idCliente) {
        try {
            Alert alerta = transactionService.processTransactionScoring(idCliente);
            if (alerta != null) {
                return ResponseEntity.ok(alerta);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Alert(null, "Error interno del servidor."));
        }
    }

    @PostMapping("/alerta-account-takeover")
    public ResponseEntity<Alert> evaluateAccountTakeover(@RequestBody List<UserEvent> userEvents) {
        try {
            // Si tengo algún evento de usuario que sea crítico, entonces se genera una alerta
            boolean userEventFlag = userEvents.stream()
                .anyMatch(userEvent -> userEvent.getType().CriticEvent());

            Optional<Transaction> mostRecentTransfer = transactionService.getMostRecentTransferByUserId(userEvents.get(0).getUser().getId());

            boolean lastTransferFlag = mostRecentTransfer.isPresent() &&
                transactionService.isLastTransferInLastHour(mostRecentTransfer.get(), userEvents.get(0).getEventDateHour());

            if (userEventFlag && lastTransferFlag) {
                // Crear alerta de account takeover
                Long userId = mostRecentTransfer.get().getUser().getId();
                Alert alert = new Alert(
                    userId,
                    "Alerta: Posible Account Takeover detectado para el usuario " + userId
                    );
                return ResponseEntity.ok(alert);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(400).body(new Alert(null, "Error: No se han proporcionado eventos de usuario."));
        }
    }

    @PostMapping("/alerta-transaccion-internacional")
    public ResponseEntity<Alert> postMethodName(@RequestBody Transaction transaction) {
        
        try {
            Alert alert = transactionService.processTransactionCountryInternational(transaction);
            if (alert != null) {
                return ResponseEntity.ok(alert);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Alert(null, "Error interno del servidor."));
        }
    }
    

}
