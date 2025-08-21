package com.pei.service;

import java.math.*;
import java.util.HashSet;
import java.time.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.pei.config.AlertProperties;
import com.pei.domain.*;
import com.pei.domain.User.User;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pei.domain.TimeRange;
import com.pei.domain.Transaction;
import java.util.Optional;
import java.util.stream.Collectors;

import com.pei.dto.Alert;
import com.pei.repository.ChargebackRepository;
import com.pei.repository.PurchaseRepository;
import com.pei.repository.TransactionRepository;
import com.pei.service.bbva.ScoringService;

@Service
public class TransactionService {

    private final ChargebackRepository chargebackRepository;
    private final PurchaseRepository purchaseRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionVelocityDetectorService transactionVelocityDetectorService;
    private final ScoringServiceInterno scoringServiceInterno;
    private final Gson gson;
    private final AlertProperties alertProperties;

    public TransactionService(ChargebackRepository chargebackRepository,
            PurchaseRepository purchaseRepository, TransactionRepository transactionRepository,
            TransactionVelocityDetectorService transactionVelocityDetectorService,
            Gson gson, ScoringServiceInterno scoringServiceInterno, AlertProperties alertProperties) {
        this.chargebackRepository = chargebackRepository;
        this.purchaseRepository = purchaseRepository;
        this.transactionRepository = transactionRepository;
        this.transactionVelocityDetectorService = transactionVelocityDetectorService;
        this.gson = gson;
        this.scoringServiceInterno = scoringServiceInterno;
        this.alertProperties = alertProperties;
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

    public Alert processTransaction(Long idCliente) {
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

        Integer minutesRange;
        Integer maxTransactions;
        BigDecimal minMonto;
        BigDecimal maxMonto;

        if ("individuo".equals(clientType)) {
            minutesRange = transactionVelocityDetectorService.getIndividuoMinutesRange();
            maxTransactions = transactionVelocityDetectorService.getIndividuoMaxTransactions();
            minMonto = transactionVelocityDetectorService.getIndividuoUmbralMonto().get("minMonto");
            maxMonto = transactionVelocityDetectorService.getIndividuoUmbralMonto().get("maxMonto");
        } else {
            minutesRange = transactionVelocityDetectorService.getEmpresaMinutesRange();
            maxTransactions = transactionVelocityDetectorService.getEmpresaMaxTransactions();
            minMonto = transactionVelocityDetectorService.getEmpresaUmbralMonto().get("minMonto");
            maxMonto = transactionVelocityDetectorService.getEmpresaUmbralMonto().get("maxMonto");
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

    public List<Transaction> getAllTransactionsByUserId(Long userId) {
        return transactionRepository.findRecentTransferByUserId(userId);
    }

    /**
         * Verifica si el monto de la transacción supera el umbral esperado según el perfil del cliente.
         */
    public Alert checkUnusualAmount(User user, BigDecimal transactionAmount) {
        //Validaciones NullPointeErException
        if (user == null || transactionAmount == null) {
            throw new IllegalArgumentException("User y transactionAmount no pueden ser null");
        }
        //Calculo el promedio de gasto mensual
        updateAverageMonthlySpending(user);
        BigDecimal avgSpending = user.getAverageMonthlySpending();
        if (avgSpending == null || avgSpending.compareTo(BigDecimal.ZERO) == 0) {
            return null; // No hay datos históricos para comparar
        }
        //Obtengo el umbral según el tipo de cliente
        double thresholdMultiplier = alertProperties.getThresholdFor(user.getClientType());
        BigDecimal thresholdAmount = avgSpending.multiply(BigDecimal.valueOf(thresholdMultiplier));

        //Hago la comparacion entre el monto de la transaccion y el umbral
        if (transactionAmount.compareTo(thresholdAmount) > 0) {
            String reason = String.format(
                "El monto de la transacción (%.2f) supera el %.0f%% del promedio histórico (%.2f).",
                transactionAmount.doubleValue(),
                thresholdMultiplier * 100,
                avgSpending.doubleValue()
            );
            return new Alert(user.getId(), reason);
        }
        return null;
    }

    /**
     * Verifica si la transacción tiene nuevo dispositivo y esta en horario fuera del rango típico).
     */
    public Alert checkUnusualBehavior(User user, Transaction transaction, Login login) {
        //Validaciones NullPointerException
        if (user == null || transaction == null || login == null) {
            throw new IllegalArgumentException("User, Transaction y Login no pueden ser null");
        }
        if (transaction.getDate() == null) {
            throw new IllegalArgumentException("Transaction date no puede ser null");
        }
        // Verifico si el dispositivo es nuevo y si la hora es inusual
        boolean newDevice = isNewDevice(login);
        boolean unusualTime = isUnusualTime(user, transaction.getDate());

        // Si ambas condiciones se cumplen, genero una alerta
        if ( newDevice && unusualTime) {
            return new Alert(user.getId(),
                "Dispositivo nuevo y horario de transacción fuera del rango esperado.");
        }
        return null;
    }

    /**
     * Verifica si la hora de una nueva transacción está fuera del rango típico del usuario (percentiles 5 y 95).
     */
    private boolean isUnusualTime(User user, LocalDateTime transactionTime) {
        //Validaciones NullPointerException
        if (user == null || transactionTime == null) throw new IllegalArgumentException("User y transactionTime no pueden ser null");

        List<Transaction> transactions = transactionRepository.findByUserId(user.getId());

        List<LocalTime> transactionHours = transactions.stream()
            .map(t -> t.getDate().toLocalTime())
            .sorted()
            .collect(Collectors.toList());

        if (transactionHours.isEmpty()) {
            return false;
        }

        LocalTime p5 = percentile(transactionHours, 5);
        LocalTime p95 = percentile(transactionHours, 95);
        LocalTime currentTime = transactionTime.toLocalTime();

        return currentTime.isBefore(p5) || currentTime.isAfter(p95);
    }

    /**
     * Calcula el percentil de una lista ORDENADA (resuelto con .sorted() cuando traigo la lista) de tiempos.
     */
    private LocalTime percentile(List<LocalTime> sortedTimes, double percentile) {
        int n = sortedTimes.size();
        double rank = percentile / 100.0 * (n - 1);
        int lowerIndex = (int) Math.floor(rank);
        int upperIndex = (int) Math.ceil(rank);

        if (lowerIndex == upperIndex) {
            return sortedTimes.get(lowerIndex);
        }

        LocalTime lower = sortedTimes.get(lowerIndex);
        LocalTime upper = sortedTimes.get(upperIndex);

        long secondsLower = lower.toSecondOfDay();
        long secondsUpper = upper.toSecondOfDay();
        long interpolated = (long) (secondsLower + (rank - lowerIndex) * (secondsUpper - secondsLower));

        return LocalTime.ofSecondOfDay(interpolated);
    }

    public boolean isNewDevice(Login login) {
        //Validaciones NullPointerException
        if (login == null || login.getUser() == null || login.getDevice() == null) {
            throw new IllegalArgumentException("Login, User y Device no pueden ser null");
        }

        User user = login.getUser();

        // Inicializar el set de devices si es null
        if (user.getDevices() == null) {
            user.setDevices(new HashSet<>());
        }

        // Verificar si el device ya existe en el set
        boolean deviceExists = user.getDevices().stream()
            .anyMatch(d -> d.getDeviceId() != null
                && d.getDeviceId().equals(login.getDevice().getDeviceId()));

        if (deviceExists) {
            return false; // dispositivo ya conocido
        }

        // Si no existe, agregarlo al set
        user.getDevices().add(login.getDevice());
        return true; // dispositivo nuevo
    }



    public void updateAverageMonthlySpending(User user) {
        if (user == null) return;
        LocalDateTime fromDate = LocalDateTime.now().minusMonths(1);

        BigDecimal total = transactionRepository.sumTransactionsFromDate(user.getId(), fromDate);
        Integer count = transactionRepository.countTransactionsFromDate(user.getId(), fromDate);

        BigDecimal average = (count != null && count > 0 && total != null) ?
            total.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP) :
            BigDecimal.ZERO;

        user.setAverageMonthlySpending(average);
    }


}
