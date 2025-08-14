package com.pei.service;

import com.pei.config.ClienteConfiableProperties;
import com.pei.domain.User;
import com.pei.service.filter.FiltroCliente;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClienteConfiableService {
    private final ClienteConfiableProperties config;
    private final List<FiltroCliente> filtros = new ArrayList<>();

    public ClienteConfiableService(ClienteConfiableProperties config) {
        this.config = config;

        // Registramos los filtros usando lambdas - esto permite una sintaxis más limpia
        // y evita la necesidad de crear clases anónimas o métodos adicionales.
        filtros.add(this::filtroAntiguedad);
        filtros.add(this::filtroChargeback);
        filtros.add(this::filtroPerfil);
    }

    // Metodo que verifica si un cliente es confiable
    // según los filtros definidos en la configuración.
    public boolean esClienteConfiable(User cliente) {
        return filtros.stream().allMatch(f -> f.aplica(cliente));
    }


    // Filtros específicos que se aplican a los clientes.
    // Cada filtro verifica una condición específica
    private boolean filtroAntiguedad(User cliente) {
        LocalDate fechaAlta = cliente.getCreationDate(); // Debe existir en User
        double minima = config.getAntiguedad().getMinimoMedicion();

        ChronoUnit unidad = mapearUnidad(config.getAntiguedad().getMedicion());
        long diferencia = unidad.between(fechaAlta, LocalDate.now());

        return diferencia >= minima;
    }

    private boolean filtroChargeback(User cliente) {
        int limite = config.getLimiteChargeback();
        long cantidadChargebacks = cliente.getChargebacks() != null
            ? cliente.getChargebacks().size()
            : 0;
        return cantidadChargebacks <= limite;
    }

    private boolean filtroPerfil(User cliente) {
        return config.getPerfilesNoConfiables()
            .stream()
            .noneMatch(p -> p.equalsIgnoreCase(cliente.getProfile()));
    }

    private ChronoUnit mapearUnidad(String medicion) {
        return switch (medicion.toUpperCase()) {
            case "DIA" -> ChronoUnit.DAYS;
            case "SEMANA" -> ChronoUnit.WEEKS;
            case "MES" -> ChronoUnit.MONTHS;
            case "AÑO" -> ChronoUnit.YEARS;
            default -> throw new IllegalArgumentException("Unidad no soportada: " + medicion);
        };
    }
}
