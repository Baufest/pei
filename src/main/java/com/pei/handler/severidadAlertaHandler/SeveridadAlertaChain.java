package com.pei.handler.severidadAlertaHandler;

import com.pei.domain.AlertaSeveridad;
import com.pei.domain.Transaction;

public abstract class SeveridadAlertaChain {
    protected SeveridadAlertaChain next;

    public void setNextComponent(SeveridadAlertaChain next) {
        this.next = next;
    }

    public AlertaSeveridad procesarSeveridad(Transaction t) {
        if (match(t))
            return getSeveridad();
        if (next != null)
            return next.procesarSeveridad(t);
        return AlertaSeveridad.ALTA;
    }

    protected abstract boolean match(Transaction t);
    protected abstract AlertaSeveridad getSeveridad();
}
