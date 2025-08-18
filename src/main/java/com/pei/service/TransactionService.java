package com.pei.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pei.dto.Alert;
import com.pei.repository.TransactionRepository;
import com.pei.service.bbva.ScoringService;


@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionVelocityDetectorService transactionVelocityDetectorService;

    private final ScoringServiceInterno scoringService;
    private final Gson gson;

    public TransactionService(TransactionRepository transactionRepository, 
            TransactionVelocityDetectorService transactionVelocityDetectorService, ScoringServiceInterno scoringService,
            Gson gson) {
        this.transactionRepository = transactionRepository;
        this.transactionVelocityDetectorService = transactionVelocityDetectorService;
        this.scoringService = scoringService;
        this.gson = gson;
    }
    
    public Alert getFastMultipleTransactionAlert(Long userId, String clientType) {

        Integer minutesRange = clientType.equals("individuo")
                ? transactionVelocityDetectorService.getIndividuoMinutesRange()
                : transactionVelocityDetectorService.getEmpresaMinutesRange();

        Integer maxTransactions = clientType.equals("individuo")
                ? transactionVelocityDetectorService.getIndividuoMaxTransactions()
                : transactionVelocityDetectorService.getEmpresaMaxTransactions();

        LocalDateTime fromDate = LocalDateTime.now().minusMinutes(minutesRange);
        Integer numMaxTransactions = maxTransactions;
        Integer numTransactions = transactionRepository.countTransactionsFromDate(userId, fromDate);

        if (numTransactions > numMaxTransactions) {
            return new Alert(userId, "Fast multiple transactions detected for user " + userId);
        }

        Alert fastMultipleTransactionAlert = null;
        return fastMultipleTransactionAlert;
    }

    public Alert processTransaction(Long idCliente) {
        String scoringJson = ScoringService.consultarScoring(idCliente.intValue());

        JsonObject responseScoringService = gson.fromJson(scoringJson, JsonObject.class);
        int status = responseScoringService.get("status").getAsInt();

        if (status != 200) {
            return new Alert(idCliente, "Alerta: Transaccion rechazada");
        }
        int scoringCliente = responseScoringService.get("scoring").getAsInt();

        String color = scoringService.getScoringColorBasedInUserScore(scoringCliente);

        String msj = null;
        switch (color) {
            case "Verde":
                msj = "Alerta: Transaccion aprobada para cliente " + idCliente
                        + " con scoring de: " + scoringCliente;
                break;
            case "Amarillo":
                msj = "Alerta: Transaccion en revision para cliente " + idCliente
                        + " con scoring de: " + scoringCliente;

                break;
            case "Rojo":
                msj = "Alerta: Transaccion rechazada para cliente " + idCliente
                        + " con scoring de: " + scoringCliente;
                break;
        }
        return new Alert(idCliente, msj);
    }
}