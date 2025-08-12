package com.pei.dto;

import com.pei.domain.Account;
import com.pei.domain.Transaction;

public class TransferReq {

    private Account cuentaDestino;
    private Transaction transaccionActual;

    public Account getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(Account cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public Transaction getTransaccionActual() {
        return transaccionActual;
    }

    public void setTransaccionActual(Transaction transaccionActual) {
        this.transaccionActual = transaccionActual;
    }

}
