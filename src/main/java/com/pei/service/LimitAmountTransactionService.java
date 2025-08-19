package com.pei.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.pei.domain.AmountLimit;
import com.pei.repository.AmountLimitRepository;

@Service
public class LimitAmountTransactionService {

    private final AmountLimitRepository amountLimitRepository;
    private final ClienteService clienteService;

    public LimitAmountTransactionService(TransactionService transactionService,
    AmountLimitRepository amountLimitRepository, ClienteService clienteService
    ) {
        this.amountLimitRepository = amountLimitRepository;
        this.clienteService = clienteService;}

    public BigDecimal getAvailableAmount(Long userId) {
        String clientType = clienteService.getClientType(userId);
        LocalDateTime now = LocalDateTime.now();
        AmountLimit amountLimit = amountLimitRepository.findByClientTypeAndStartingDateBeforeAndExpirationDateAfter(
                clientType, now, now);
        return amountLimit.getAmount();
    }

    public void createAmountLimit(String clientType, BigDecimal amount, LocalDateTime
            startingDate, LocalDateTime expirationDate) {

                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("El monto debe ser mayor que cero.");
                }
                if (startingDate.isAfter(expirationDate) || startingDate.isBefore(LocalDateTime.now())) {
                    throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de expiración y no puede estar en el pasado.");}
                if (clientType.isEmpty()) {
                    throw new IllegalArgumentException("El tipo de cliente no puede estar vacío.");
                }
                if (amountLimitRepository.existsByClientTypeAndStartingDateLessThanEqualAndExpirationDateGreaterThanEqual(
                        clientType, expirationDate, startingDate)) {
                    throw new IllegalArgumentException(
                            "El rango de fechas se solapa con otro límite de monto para el mismo tipo de cliente: " + clientType);
                }

                AmountLimit amountLimit = new AmountLimit(clientType, amount, startingDate, expirationDate);

                amountLimitRepository.save(amountLimit);
            }
    
}
