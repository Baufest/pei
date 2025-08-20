package com.pei.handler.severidadAlertaHandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pei.domain.AlertaSeveridad;
import com.pei.domain.Transaction;

public class AlertaSeveridadALTA extends SeveridadAlertaChain{

    @Override
    protected AlertaSeveridad getSeveridad() {
        // TODO Auto-generated method stub
        return AlertaSeveridad.ALTA;
    }

    @Override
    protected boolean match(Transaction t) {
        // TODO Auto-generated method stub
        BigDecimal amount = new BigDecimal(10000);
        boolean isOverLimitAmount = amount.intValue() < t.getAmount().intValue();
        boolean isNewAccount = t.getDestinationAccount().getCreationDate().getMinute() < LocalDateTime.now().minusHours(48).getMinute();
        return  isOverLimitAmount && isNewAccount;
    }
    
}
