package com.pei.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDTO(
    Long id,
    String codCoelsa,
    BigDecimal amount,
    String currency,
    Long accountDestinationId,
    LocalDateTime dateTime) { }

