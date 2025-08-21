package com.pei.service;

import com.pei.config.CompanyClientFrequencyProperties;
import com.pei.config.IndividualClientFrequencyProperties;
import com.pei.domain.Account.AccountType;
import com.pei.domain.Transaction;
import com.pei.domain.User.ClientType;
import com.pei.domain.User.User;
import com.pei.domain.alerts.RecurringBeneficiaryAlert;
import com.pei.repository.RecurringBeneficiaryAlertRepository;
import com.pei.service.exceptions.VerificadorBeneficiarioRecurrenteException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionFrequencyService {
    private final RecurringBeneficiaryAlertRepository recurringBeneficiaryAlertRepository;

    private final TransactionService transactionService;
    private final IndividualClientFrequencyProperties individualProps;
    private final CompanyClientFrequencyProperties companyProps;

    public TransactionFrequencyService(
            IndividualClientFrequencyProperties individualProps,
            CompanyClientFrequencyProperties companyProps,
            RecurringBeneficiaryAlertRepository recurringBeneficiaryAlertRepository,
            TransactionService transactionService) {
        this.transactionService = transactionService;
        this.recurringBeneficiaryAlertRepository = recurringBeneficiaryAlertRepository;
        this.individualProps = individualProps;
        this.companyProps = companyProps;
    }

    public void analyzeTransactionFrequency(Long userId) {
        List<Transaction> transactions = transactionService.getAllTransactionsByUserId(userId);

        if (transactions == null || transactions.isEmpty()) {
            throw new VerificadorBeneficiarioRecurrenteException("Error al obtener las transacciones");
        }

        User client = transactions.get(0).getUser();

        if (client == null) {
            throw new VerificadorBeneficiarioRecurrenteException("Error al obtener el cliente de la transacción");
        }

        if (client.getClientType() == null) {
            throw new VerificadorBeneficiarioRecurrenteException("Error al obtener el tipo de cliente");
        }

        if (client.getClientType() == ClientType.INDIVIDUAL) {
            verifyIndividualClientFrequency(transactions);
        } else {
            verifyCompanyClientFrequency(transactions);
        }
    }

    private void verifyCompanyClientFrequency(List<Transaction> transactions) {
        List<RecurringBeneficiaryAlert> alerts = checkFrequency(transactions, companyProps);

        if(alerts.isEmpty()) {
            return;
        }

        recurringBeneficiaryAlertRepository.saveAll(alerts);
    }

    private void verifyIndividualClientFrequency(List<Transaction> transactions) {
        List<RecurringBeneficiaryAlert> alerts = checkFrequency(transactions, individualProps);

        if(alerts.isEmpty()) {
            return;
        }

        recurringBeneficiaryAlertRepository.saveAll(alerts);
    }

    private List<RecurringBeneficiaryAlert> checkFrequency(List<Transaction> transactions, Object props) {
        try {
            if (props == null) {
                throw new VerificadorBeneficiarioRecurrenteException("Error: las propiedades de frecuencia no están configuradas");
            }

            List<RecurringBeneficiaryAlert> alerts = new ArrayList<>();

            int checkWindowHs;
            int maxTransfersSameBeneficiary;
            int maxDepositsAccountHolder;
            List<String> typesAccountHolder;

            if (props instanceof IndividualClientFrequencyProperties p) {
                checkWindowHs = p.getCheckWindowHs();
                maxTransfersSameBeneficiary = p.getMaxTransfersSameBeneficiary();
                maxDepositsAccountHolder = p.getMaxDepositsAccountHolder();
                typesAccountHolder = Objects.requireNonNull(p.getTypesAccountHolder());
            } else if (props instanceof CompanyClientFrequencyProperties p) {
                checkWindowHs = p.getCheckWindowHs();
                maxTransfersSameBeneficiary = p.getMaxTransfersSameBeneficiary();
                maxDepositsAccountHolder = p.getMaxDepositsAccountHolder();
                typesAccountHolder = Objects.requireNonNull(p.getTypesAccountHolder());
            } else {
                throw new VerificadorBeneficiarioRecurrenteException("Unknown properties type");
            }

            // Convertimos lista de Strings a enums válidos
            Set<AccountType> allowedAccountTypes = typesAccountHolder.stream()
                .map(AccountType::valueOf)
                .collect(Collectors.toSet());

            LocalDateTime windowStart = LocalDateTime.now().minusHours(checkWindowHs);

            // Filtrar por ventana y por tipo de cuenta permitido
            List<Transaction> filtered = transactions.stream()
                .filter(t -> Objects.requireNonNull(t.getDate()).isAfter(windowStart))
                .filter(t -> allowedAccountTypes.contains(Objects.requireNonNull(t.getDestinationAccount().getAccountType())))
                .toList();

            // Agrupar por (usuario, cuenta destino)
            record UserBeneficiary(@NonNull Long userId, @NonNull Long destinationAccountId) { }

            Map<UserBeneficiary, List<Transaction>> grouped = filtered.stream()
                .collect(Collectors.groupingBy(tx ->
                    new UserBeneficiary(tx.getUser().getId(), tx.getDestinationAccount().getId())
                ));

            // Revisar cada grupo
        /* Ejemplo:
        Si Pepe hizo 3 transferencias a la cuenta C100 y 2 a C200, el Map queda:
        {
            (Pepe, C100) -> [Tx1, Tx2, Tx3],
            (Pepe, C200) -> [Tx4, Tx5]
         } */
            for (Map.Entry<UserBeneficiary, List<Transaction>> entry : grouped.entrySet()) {
                List<Transaction> txs = entry.getValue();
                if (txs.isEmpty()) continue;

            /*
            Para cada grupo:
                - Si el user es dueño del destinationAccount → depósito.
                - Si no → transferencia.
            */
                Transaction sample = txs.get(0);
                User actor = sample.getUser();
                User destinoOwner = sample.getDestinationAccount().getOwner();

                boolean deposito = actor.getId().equals(destinoOwner.getId());

                // TODO: Averiguar que hacer con las alertas de las transacciones
                // Si es depósito, verificamos el número de depósitos al mismo titular
                // Si es transferencia, verificamos el número de transferencias al mismo beneficiario externo
                // Cargo la alerta correspondiente si se supera el límite
                if (deposito) {
                    if (txs.size() >= maxDepositsAccountHolder) {
                        alerts.add(new RecurringBeneficiaryAlert(
                            actor.getId(),
                            LocalDateTime.now(),
                            String.format("⚠️ Alerta: demasiados depósitos (%d) al mismo titular (userId=%d, cuentaDestino=%d)%n",
                                txs.size(), actor.getId(), sample.getDestinationAccount().getId())
                        ));
                    }
                } else {
                    if (txs.size() >= maxTransfersSameBeneficiary) {
                        alerts.add(new RecurringBeneficiaryAlert(
                            actor.getId(),
                            LocalDateTime.now(),
                            String.format("⚠️ Alerta: demasiadas transferencias (%d) al mismo beneficiario externo (userId=%d, cuentaDestino=%d)%n",
                                txs.size(), actor.getId(), sample.getDestinationAccount().getId())
                        ));
                    }
                }
            }
            return alerts;
        } catch (Exception e) {
            throw new VerificadorBeneficiarioRecurrenteException("Error al verificar la frecuencia de transacciones: " + e.getMessage());
        }
    }
}

