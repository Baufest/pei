package com.pei.service;

import com.pei.domain.AlertaSeveridad;
import com.pei.domain.Transaction;
import com.pei.handler.severidadAlertaHandler.AlertaSeveridadALTA;
import com.pei.handler.severidadAlertaHandler.AlertaSeveridadBAJA;
import com.pei.handler.severidadAlertaHandler.AlertaSeveridadMEDIA;
import com.pei.handler.severidadAlertaHandler.SeveridadAlertaChain;

public class CheckSeverityService {
    private SeveridadAlertaChain severidadAlertaChain;

    public CheckSeverityService() {
        SeveridadAlertaChain alertaAlta = new AlertaSeveridadALTA();
        SeveridadAlertaChain alertaMedia = new AlertaSeveridadMEDIA();
        SeveridadAlertaChain alertaBaja = new AlertaSeveridadBAJA();

        alertaAlta.setNextComponent(alertaMedia);
        alertaMedia.setNextComponent(alertaBaja);

        this.severidadAlertaChain = alertaAlta;
    }

    public AlertaSeveridad checkSeveridad(Transaction t) {
        return severidadAlertaChain.procesarSeveridad(t);
    }
}
