package com.pei.config;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "transferencias.internacionales")
public class TransferenciaInternacionalProperties {

    private List<String> paisesRiesgo;
    private BigDecimal  montoAlerta; 

    public List<String> getPaisesRiesgo() {
        return paisesRiesgo;
    }

    public void setPaisesRiesgo(List<String> paisesRiesgo) {
        this.paisesRiesgo = paisesRiesgo;
    }

    public BigDecimal  getMontoAlerta() {
        return montoAlerta;
    }

    public void setMontoAlerta(BigDecimal  montoAlerta) {
        this.montoAlerta = montoAlerta;
    }

    
}
