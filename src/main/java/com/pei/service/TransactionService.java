package com.pei.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pei.config.TransferenciaInternacionalProperties;
import com.pei.domain.TimeRange;
import com.pei.domain.Transaction;
import com.pei.dto.Alert;
import com.pei.repository.AccountRepository;
import com.pei.repository.ChargebackRepository;
import com.pei.repository.PurchaseRepository;
import com.pei.repository.TransactionRepository;
import com.pei.service.bbva.ScoringService;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;

    private final ChargebackRepository chargebackRepository;
    private final PurchaseRepository purchaseRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionVelocityDetectorService transactionVelocityDetectorService;
    private final ScoringServiceInterno scoringServiceInterno;
    private final Gson gson;
    private final NotificationService notificationService;
    private final RiskCountryService riskCountryService;
    private final TransactionParamsService transactionParamsService;

    public TransactionService(ChargebackRepository chargebackRepository,
            PurchaseRepository purchaseRepository, TransactionRepository transactionRepository,
            TransactionVelocityDetectorService transactionVelocityDetectorService,
            Gson gson, ScoringServiceInterno scoringServiceInterno, AccountRepository accountRepository,
            TransferenciaInternacionalProperties internationalCountryConfig, NotificationService notificationService,
            RiskCountryService riskCountryService, TransactionParamsService transactionParamsService) {
        this.chargebackRepository = chargebackRepository;
        this.purchaseRepository = purchaseRepository;
        this.transactionRepository = transactionRepository;
        this.transactionVelocityDetectorService = transactionVelocityDetectorService;
        this.gson = gson;
        this.scoringServiceInterno = scoringServiceInterno;
        this.accountRepository = accountRepository;
        this.notificationService = notificationService;
        this.riskCountryService = riskCountryService;
        this.transactionParamsService = transactionParamsService;
    }

    // TODO: Probablemente tengamos que hacer una Query SQL para obtener las
    // transacciones, sería más performante
    public List<Transaction> getLast24HoursTransactions(List<Transaction> transactions) {
        return transactions.stream()
                .filter(transaction -> transaction.getDate().isAfter(
                        java.time.LocalDateTime.now().minusDays(1)))
                .toList();
    }

    public BigDecimal totalDeposits(List<Transaction> transactions) {
        // Obtengo los montos de los depósitos
        // donde la cuenta de destino es la del usuario
        return transactions.stream()
                .filter(transaction -> transaction.getDestinationAccount().getOwner().equals(transaction.getUser()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalTransfers(List<Transaction> transactions) {
        // Obtengo los montos de las transferencias
        // donde la cuenta de destino es diferente a la del usuario
        return transactions.stream()
                .filter(transaction -> !transaction.getDestinationAccount().getOwner().equals(transaction.getUser()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Alert getChargebackFraudAlert(Long userId) {
        int numberOfChargebacks = chargebackRepository.findByUserId(userId).size();
        int numberOfPurchases = purchaseRepository.findByUserId(userId).size();
        Alert chargebackFraudAlert = null;

        if (numberOfPurchases == 0 && numberOfChargebacks > 0) {
            chargebackFraudAlert = new Alert(
                    userId,
                    "Chargeback fraud detected for user " + userId);
        }

        if (numberOfPurchases > 0) {
            if ((double) numberOfChargebacks / numberOfPurchases > 0.1) {
                chargebackFraudAlert = new Alert(
                        userId,
                        "Chargeback fraud detected for user " + userId);
            }
        }
        return chargebackFraudAlert;
    }

    public TimeRange getAvgTimeRange(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new IllegalArgumentException("Transactions list was empty.");
        }
        int minHour = Integer.MAX_VALUE;
        int maxHour = Integer.MIN_VALUE;

        for (Transaction transaction : transactions) {
            LocalDateTime dateHour = transaction.getDate();
            int hora = dateHour.getHour();

            if (hora < minHour)
                minHour = hora;
            if (hora > maxHour)
                maxHour = hora;
        }
        return new TimeRange(minHour, maxHour);
    }

    public int getApprovalCount(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found in database"));
        return transaction.getApprovalList().size();
    }

    // PROCESAR TRANSACCION GENERAL
    // @Transactional
    // public List<Alert> processTransactionGeneral(Transaction transaction){
    // //Alert alertCountryInternational =
    // processVerifyCountryInternational(transaction); DEVUELVE BOOLEAN
    // //Alert alertScoring = processVerifyScoring(transaction.getUser().getId());
    // DEVUELVE COLOR
    // //List..accountRepository

    // //transactionRepository.save(transaction); (GUARDAMOS LA TRANSACCIÓN CON SU
    // ESTADO (APROBADA, RECHAZADA, REQUIERE_APROBACION))
    // return new Alert("")
    // }

    // @Transactional
    public Alert processTransactionCountryInternational(Transaction transaction) {
        if (transaction == null ||
                transaction.getSourceAccount() == null ||
                transaction.getDestinationAccount() == null ||
                transaction.getUser() == null ||
                transaction.getUser().getId() == null) {
            return new Alert(null, "Alerta: Transacción inválida (faltan datos obligatorios)");
        }

        if (transaction.getAmount() == null || transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return new Alert(transaction.getUser().getId(), "Alerta: Monto de transacción inválido");
        }

        String origen = transaction.getSourceAccount().getCountry();
        String destino = transaction.getDestinationAccount().getCountry();

        if (origen == null || origen.isBlank() || destino == null || destino.isBlank()) {
            return new Alert(transaction.getUser().getId(), "Alerta: País de origen o destino inválido");
        }

        if (riskCountryService.isRiskCountry(origen) || riskCountryService.isRiskCountry(destino)) {
            transaction.setStatus(Transaction.TransactionStatus.REQUIERE_APROBACION);
            return new Alert(transaction.getUser().getId(),
                    "Alerta: Transacción hacia/desde país de riesgo (origen: " + origen + ", destino: " + destino
                            + ")");
        }

        if (transaction.isInternational()) {
            BigDecimal limiteInternacional = transactionParamsService.getMontoAlertaInternacional();
            if (limiteInternacional == null) {
                throw new IllegalStateException("Parámetro de monto internacional no configurado");
            }
            if (transaction.getAmount().compareTo(limiteInternacional) > 0) {
                transaction.setStatus(Transaction.TransactionStatus.APROBADA);

                Alert alert = new Alert(transaction.getUser().getId(),
                        "Alerta: Transacción internacional con monto mayor a: "
                                + limiteInternacional);
                notificationService.notifyCompliance(transaction, alert);

                return alert;
            } else {
                transaction.setStatus(Transaction.TransactionStatus.APROBADA);
                return new Alert(transaction.getUser().getId(), "Alerta: Transacción internacional aprobada");
            }
        }

        transaction.setStatus(Transaction.TransactionStatus.APROBADA);
        // transactionRepository.save(transaction); -> DESCOMENTAR AL IMPLEMENTAR BD
        return new Alert(transaction.getUser().getId(), "Alerta: Transacción aprobada");
    }

    public Alert processTransactionScoring(Long idCliente) { // processVerifyScoring

        String scoringJson = ScoringService.consultarScoring(idCliente.intValue());

        JsonObject responseScoringService = gson.fromJson(scoringJson, JsonObject.class);
        int status = responseScoringService.get("status").getAsInt();

        if (status != 200) {
            return new Alert(idCliente, "Alerta: Transaccion rechazada");
        }
        int scoringCliente = responseScoringService.get("scoring").getAsInt();

        String color = scoringServiceInterno.getScoringColorBasedInUserScore(scoringCliente);

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

    public Alert getFastMultipleTransactionAlert(Long userId, String clientType) {

        if (userId == null || clientType == null || clientType.isBlank()) {
            throw new IllegalArgumentException("Parametros invalidos");
        }

        Integer minutesRange;
        Integer maxTransactions;
        BigDecimal minMonto;
        BigDecimal maxMonto;

        if ("individuo".equals(clientType)) {
            minutesRange = transactionVelocityDetectorService.getIndividuoMinutesRange();
            maxTransactions = transactionVelocityDetectorService.getIndividuoMaxTransactions();
            minMonto = transactionVelocityDetectorService.getIndividuoUmbralMonto().get("minMonto");
            maxMonto = transactionVelocityDetectorService.getIndividuoUmbralMonto().get("maxMonto");
        } else if ("empresa".equals(clientType)) {
            minutesRange = transactionVelocityDetectorService.getEmpresaMinutesRange();
            maxTransactions = transactionVelocityDetectorService.getEmpresaMaxTransactions();
            minMonto = transactionVelocityDetectorService.getEmpresaUmbralMonto().get("minMonto");
            maxMonto = transactionVelocityDetectorService.getEmpresaUmbralMonto().get("maxMonto");
        } else {
            throw new IllegalArgumentException();
        }

        if (minutesRange == null || maxTransactions == null || minMonto == null || maxMonto == null) {
            return null;
        }

        LocalDateTime fromDate = LocalDateTime.now().minusMinutes(minutesRange);
        int numTransactions = transactionRepository
                .countTransactionsByUserAfterDateBetweenMontos(userId, fromDate, minMonto, maxMonto);

        if (numTransactions > maxTransactions) {
            return new Alert(userId, "Fast multiple transactions detected for user " + userId);
        }

        return null;
    }

    public Optional<Transaction> getMostRecentTransferByUserId(Long userId) {
        return transactionRepository.findRecentTransferByUserId(userId).stream().findFirst();
    }

    public boolean isLastTransferInLastHour(Transaction transaction, LocalDateTime eventDateHour) {
        // Fecha de la transferencia
        LocalDateTime transferDate = transaction.getDate();

        // Calcula la diferencia en minutos entre el evento y la transferencia
        long minutesDifference = Duration.between(eventDateHour, transferDate).toMinutes();

        // Considera sospechoso si la transferencia es posterior al evento y dentro de
        // 60 minutos
        return minutesDifference >= 0 && minutesDifference <= 60;
    }

    public Alert getAmountLimitAlert(Long userId, BigDecimal limitAmount, Boolean isANewUser) {
        BigDecimal totalAmountToday = getTotalAmountByUserAndDate(userId);
        
        if (isANewUser) { 
            limitAmount = limitAmount.multiply(BigDecimal.valueOf(0.5)); }
        if (totalAmountToday.compareTo(limitAmount) > 0) {
            return new Alert(userId, "Amount limit exceeded for user " + userId);
        }

        return null;
    }

    public BigDecimal getTotalAmountByUserAndDate(Long userId) {
        LocalDate date = LocalDate.now();
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        return transactionRepository.getTotalAmountByUserAndDate(userId, startOfDay, endOfDay);
    }

    public List<Transaction> getAllTransactionsByUserId(Long userId) {
        return transactionRepository.findRecentTransferByUserId(userId);
    }
}
