package com.pei.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "transaction.frequency.client.individual")
public class IndividualClientFrequencyProperties {
    private int checkWindowHs; // Ventana de chequeo en horas
    private int maxTransfersSameBeneficiary; // Máximo de transferencias al mismo beneficiario
    private int maxDepositsAccountHolder; // Máximo de depósitos al titular de la cuenta
    private List<String> typesAccountHolder; // Tipos de cuentas que se chequean

    public int getCheckWindowHs() {
        return checkWindowHs;
    }

    public void setCheckWindowHs(int checkWindowHs) {
        this.checkWindowHs = checkWindowHs;
    }

    public int getMaxTransfersSameBeneficiary() {
        return maxTransfersSameBeneficiary;
    }

    public void setMaxTransfersSameBeneficiary(int maxTransfersSameBeneficiary) {
        this.maxTransfersSameBeneficiary = maxTransfersSameBeneficiary;
    }

    public int getMaxDepositsAccountHolder() {
        return maxDepositsAccountHolder;
    }

    public void setMaxDepositsAccountHolder(int maxDepositsAccountHolder) {
        this.maxDepositsAccountHolder = maxDepositsAccountHolder;
    }

    public List<String> getTypesAccountHolder() {
        return typesAccountHolder;
    }

    public void setTypesAccountHolder(List<String> typesAccountHolder) {
        this.typesAccountHolder = typesAccountHolder;
    }
}

