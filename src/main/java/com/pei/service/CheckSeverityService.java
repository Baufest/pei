package com.pei.service;

import org.springframework.stereotype.Service;

import com.pei.domain.AlertaSeveridad;
import com.pei.domain.Transaction;
import com.pei.handler.severidadAlertaHandler.AlertaSeveridadALTA;
import com.pei.handler.severidadAlertaHandler.AlertaSeveridadBAJA;
import com.pei.handler.severidadAlertaHandler.AlertaSeveridadMEDIA;
import com.pei.handler.severidadAlertaHandler.ManejadorDeSeveridad;

@Service
public class CheckSeverityService {
    private ManejadorDeSeveridad manejadorDeSeveridad;

    public CheckSeverityService() {
        ManejadorDeSeveridad alertaAlta = new AlertaSeveridadALTA();
        ManejadorDeSeveridad alertaMedia = new AlertaSeveridadMEDIA();
        ManejadorDeSeveridad alertaBaja = new AlertaSeveridadBAJA();

        alertaAlta.setNextComponent(alertaMedia);
        alertaMedia.setNextComponent(alertaBaja);

        this.manejadorDeSeveridad = alertaAlta;
    }

    public AlertaSeveridad checkSeveridad(Transaction t) {
        return manejadorDeSeveridad.procesarSeveridad(t);
    }
}
