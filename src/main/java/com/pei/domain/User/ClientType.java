package com.pei.domain.User;

public enum ClientType {
    INDIVIDUAL("Cliente Individuo"),
    COMPANY("Cliente Empresa");

    private final String typeName;

    public String getTypeName() {
        return this.typeName;
    }

    ClientType(String typeName) {
        this.typeName = typeName;
    }
}
