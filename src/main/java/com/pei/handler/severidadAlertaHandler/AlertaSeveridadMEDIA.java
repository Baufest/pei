package com.pei.handler.severidadAlertaHandler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.pei.domain.AlertaSeveridad;
import com.pei.domain.Transaction;

public class AlertaSeveridadMEDIA extends ManejadorDeSeveridad {
    @Override
    protected AlertaSeveridad getSeveridad() {
        return AlertaSeveridad.MEDIA;
    }

    @Override
    protected boolean match(Transaction t) {
        List<BigDecimal> umbral = List.of(new BigDecimal(5000), new BigDecimal(10000));
        boolean isBetweenUmbral = umbral.get(0).longValue() <= t.getAmount().longValue()
                && t.getAmount().longValue() <= umbral.get(1).longValue();
        return  isBetweenUmbral;
    }
}
