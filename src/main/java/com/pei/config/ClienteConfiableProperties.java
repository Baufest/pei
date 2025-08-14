package com.pei.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "cliente.confiable")
public class ClienteConfiableProperties {
    private Antiguedad antiguedad = new Antiguedad();
    private List<String> perfilesNoConfiables;
    private int limiteChargeback;

    public static class Antiguedad {
        private String medicion; // MES, AÑO, DIA, SEMANA
        private double minimoMedicion; // Valor mínimo de la medición en días, semanas, meses o años

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
}
