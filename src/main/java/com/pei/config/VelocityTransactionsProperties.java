package com.pei.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "velocity-transaction-params")
public class VelocityTransactionsProperties {

    private VelocityType individuo;
    private VelocityType empresa;

    public static class VelocityType {
        private Integer minutesRange;
        private Integer maxTransactions;

        public Integer getMinute() { return minutesRange; }
        public void setMinute(Integer minutesRange) { this.minutesRange = minutesRange; }
        public Integer getMaxTransactions() { return maxTransactions; }
        public void setMaxTransactions(Integer maxTransactions) { this.maxTransactions = maxTransactions; }
    }

    public Integer getIndividuoMinutesRange () {
        return individuo.getMinute();
    }

    public void setIndividuoMinutesRange(Integer minutesRange) {
        this.individuo.setMinute(minutesRange);
    }

    public Integer getIndividuoMaxTransactions () {
        return individuo.getMaxTransactions();
    }

    public void setIndividuoMaxTransactions(Integer maxTransactions) {
        this.individuo.setMaxTransactions(maxTransactions);
    }

    public Integer getEmpresaMinutesRange () {
        return empresa.getMinute(); }

    public void setEmpresaMinutesRange(Integer minutesRange) {
        this.empresa.setMinute(minutesRange);
    }

    public Integer getEmpresaMaxTransactions () {
        return empresa.getMaxTransactions();
    }

    public void setEmpresaMaxTransactions(Integer maxTransactions) {
        this.empresa.setMaxTransactions(maxTransactions);
    }
}