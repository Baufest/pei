package com.pei.config;

import com.pei.domain.User.ClientType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "cliente.confiable")
public class ClienteConfiableProperties {

    // Map para distintos tipos de cliente: INDIVIDUAL, COMPANY
    private Map<ClientType, ClienteConfiableConfig> tipos;

    public Map<ClientType, ClienteConfiableConfig> getTipos() {
        return tipos;
    }

    public void setTipos(Map<ClientType, ClienteConfiableConfig> tipos) {
        this.tipos = tipos;
    }

    // Conveniencia para obtener la configuración de un tipo de cliente
    public ClienteConfiableConfig getConfigFor(ClientType clientType) {
        return tipos.get(clientType);
    }

    public static class ClienteConfiableConfig {

        private Antiguedad antiguedad = new Antiguedad();
        private List<String> perfilesNoConfiables;
        private int limiteChargeback;

        public Antiguedad getAntiguedad() {
            return antiguedad;
        }

        public void setAntiguedad(Antiguedad antiguedad) {
            this.antiguedad = antiguedad;
        }

        public List<String> getPerfilesNoConfiables() {
            return perfilesNoConfiables;
        }

        public void setPerfilesNoConfiables(List<String> perfilesNoConfiables) {
            this.perfilesNoConfiables = perfilesNoConfiables;
        }

        public int getLimiteChargeback() {
            return limiteChargeback;
        }

        public void setLimiteChargeback(int limiteChargeback) {
            this.limiteChargeback = limiteChargeback;
        }

        public static class Antiguedad {
            private String medicion; // MES, AÑO, DIA, SEMANA
            private double minimoMedicion;

            public String getMedicion() {
                return medicion;
            }

            public void setMedicion(String medicion) {
                this.medicion = medicion;
            }

            public double getMinimoMedicion() {
                return minimoMedicion;
            }

            public void setMinimoMedicion(double minimoMedicion) {
                this.minimoMedicion = minimoMedicion;
            }
        }
    }
}
