package com.pei.handler.severidadAlertaHandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pei.domain.AlertaSeveridad;
import com.pei.domain.Transaction;

public class AlertaSeveridadALTA extends ManejadorDeSeveridad {

    @Override
    protected AlertaSeveridad getSeveridad() {
        return AlertaSeveridad.ALTA;
    }

    @Override
    protected boolean match(Transaction t) {
        BigDecimal amount = new BigDecimal(10000);
        boolean isOverLimitAmount = amount.longValue() < t.getAmount().longValue();
        boolean isNewAccount = t.getDestinationAccount().getCreationDate()
                .isAfter(LocalDateTime.now().minusHours(48));
        return  isOverLimitAmount && isNewAccount;
    }
}
