package com.pei.config;

import java.math.BigDecimal;
import java.util.Map;

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
        private BigDecimal minMonto;
        private BigDecimal maxMonto;

        public Integer getMinute() {
            return minutesRange;
        }

        public void setMinute(Integer minutesRange) {
            this.minutesRange = minutesRange;
        }

        public Integer getMaxTransactions() {
            return maxTransactions;
        }

        public void setMaxTransactions(Integer maxTransactions) {
            this.maxTransactions = maxTransactions;
        }

        public BigDecimal getMinMonto() {
            return minMonto;
        }

        public void setMinMonto(BigDecimal minMonto) {
            this.minMonto = minMonto;
        }

        public BigDecimal getMaxMonto() {
            return maxMonto;
        }

        public void setMaxMonto(BigDecimal maxMonto) {
            this.maxMonto = maxMonto;
        }

    }

    // INDIVIDUO
    public Integer getIndividuoMinutesRange() {
        return individuo.getMinute();
    }

    public void setIndividuoMinutesRange(Integer minutesRange) {
        this.individuo.setMinute(minutesRange);
    }

    public Integer getIndividuoMaxTransactions() {
        return individuo.getMaxTransactions();
    }

    public void setIndividuoMaxTransactions(Integer maxTransactions) {
        this.individuo.setMaxTransactions(maxTransactions);
    }

    public Map<String, BigDecimal> getIndividuoUmbralMonto() {
        return Map.of(
                "minMonto", this.individuo.getMinMonto(),
                "maxMonto", this.individuo.getMaxMonto());
    }

    public void setIndividuoMinAndMaxMonto(BigDecimal minMonto, BigDecimal maxMonto) {
        this.individuo.setMinMonto(minMonto);
        this.individuo.setMaxMonto(maxMonto);
    }

    // EMPRESA
    public Integer getEmpresaMinutesRange() {
        return empresa.getMinute();
    }

    public void setEmpresaMinutesRange(Integer minutesRange) {
        this.empresa.setMinute(minutesRange);
    }

    public Integer getEmpresaMaxTransactions() {
        return empresa.getMaxTransactions();
    }

    public void setEmpresaMaxTransactions(Integer maxTransactions) {
        this.empresa.setMaxTransactions(maxTransactions);
    }

    public Map<String, BigDecimal> getEmpresaUmbralMonto() {
        return Map.of(
                "minMonto", this.empresa.getMinMonto(),
                "maxMonto", this.empresa.getMaxMonto());
    }

    public void setEmpresaMinAndMaxMonto(BigDecimal minMonto, BigDecimal maxMonto) { // TODO: Consultar implementacion
        this.empresa.setMinMonto(minMonto);
        this.empresa.setMaxMonto(maxMonto);
    }

}