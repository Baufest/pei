package com.pei.handler.severidadAlertaHandler;

import com.pei.domain.AlertaSeveridad;
import com.pei.domain.Transaction;

public abstract class ManejadorDeSeveridad {

    private ManejadorDeSeveridad next;

    public AlertaSeveridad procesarSeveridad(Transaction t) {
        if (validTransaction(t)) {
            throw new IllegalArgumentException("Parametro invalido");
        } else {
            if (match(t))
                return getSeveridad();
            if (next != null)
                return next.procesarSeveridad(t);
            return AlertaSeveridad.ALTA;
        }
    }

    protected abstract boolean match(Transaction t);

    protected abstract AlertaSeveridad getSeveridad();

    public void setNextComponent(ManejadorDeSeveridad next) {
        if (next == null) {
            throw new IllegalArgumentException("Parametro invalido");
        }
        this.next = next;
    }

    public boolean validTransaction(Transaction t) {
        boolean tIsNotNull = t != null;
        boolean somethingInTIsNull = t.getAmount() == null || 
                                    t.getDestinationAccount() == null || 
                                    t.getSourceAccount() == null ||
                                    t.getUser() == null;
        return tIsNotNull && !(somethingInTIsNull);
    }

}
