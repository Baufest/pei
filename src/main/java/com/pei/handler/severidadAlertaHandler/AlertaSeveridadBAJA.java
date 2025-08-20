package com.pei.handler.severidadAlertaHandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.pei.domain.AlertaSeveridad;
import com.pei.domain.Transaction;

public class AlertaSeveridadBAJA extends SeveridadAlertaChain {
    
    @Override
    protected AlertaSeveridad getSeveridad() {
        // TODO Auto-generated method stub
        return AlertaSeveridad.BAJA;
    }

    @Override
    protected boolean match(Transaction t) {
        // TODO Auto-generated method stub
       List<BigDecimal> umbral = List.of(new BigDecimal(0), new BigDecimal(50000));
        boolean isBetweenUmbral = umbral.get(0).longValue() < t.getAmount().longValue() && t.getAmount().longValue() > umbral.get(1).longValue();
        return  isBetweenUmbral;
    }
}
