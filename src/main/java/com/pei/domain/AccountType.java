package com.pei.domain;

public enum AccountType {
    CUENTA_AHORROS("Cuenta de Ahorros"),
    CUENTA_CORRIENTE("Cuenta Corriente"),
    CUENTA_PLAZO_FIJO("Cuenta a Plazo Fijo"),
    CUENTA_INVERSION("Cuenta de Inversión"),
    BILLETERA_VIRTUAL("Billetera Virtual");

    private final String typeName;

    public String getTypeName() {
        return this.typeName;
    }
    AccountType(String typeName) {
        this.typeName = typeName;
    }
}
