package com.pei.service.filter;

import com.pei.domain.User;

@FunctionalInterface
public interface FiltroCliente {
    boolean aplica(User cliente);
}
