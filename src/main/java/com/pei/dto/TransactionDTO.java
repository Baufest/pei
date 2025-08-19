package com.pei.dto;

import java.math.BigDecimal;

public record TransactionDTO(Long id, BigDecimal amount, String currency, Long accountDestinationId) {}

