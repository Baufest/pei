package com.pei.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class AmountLimit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long amountLimitId;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private String clientType;
    @Column(nullable = false)
    private LocalDateTime startingDate;
    @Column(nullable = false)
    private LocalDateTime expirationDate;

    public AmountLimit(String clientType, BigDecimal amount, LocalDateTime startingDate,
            LocalDateTime expirationDate) {
    }

    public AmountLimit() {}

    public BigDecimal getAmount() {
        return amount;
    }
}
