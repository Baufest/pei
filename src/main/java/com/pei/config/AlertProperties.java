package com.pei.config;

import com.pei.domain.User.ClientType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "alert")
public class AlertProperties {

    // Mapa de thresholds por tipo de cliente (INDIVIDUAL, COMPANY)
    private Map<ClientType, Double> thresholds;

    public Map<ClientType, Double> getThresholds() {
        return thresholds;
    }

    public void setThresholds(Map<ClientType, Double> thresholds) {
        this.thresholds = thresholds;
    }

    // Método de conveniencia para obtener el threshold de un cliente
    public Double getThresholdFor(ClientType clientType) {
        return thresholds.getOrDefault(clientType, 2.0); // default 200% si no está configurado
    }
}
