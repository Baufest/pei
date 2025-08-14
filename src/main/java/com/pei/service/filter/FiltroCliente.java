package com.pei.service.filter;

import com.pei.domain.User;

// Interfaz con fin de filtrar clientes
// que cumplen con una condición específica.
// Por ejemplo, filtrar clientes con un riesgo específico,
// o clientes que tienen un gasto mensual promedio mayor a un valor determinado.
@FunctionalInterface
public interface FiltroCliente {
    boolean aplica(User cliente);
}
