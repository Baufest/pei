package com.pei.service;

import com.pei.domain.Account;

public class CriticalityService {

    private static double mediumAmountThreshold = 100000.0;
    private static double highAmountThreshold = 1000000.0;

    public static String getCriticality(final Long userId, final double amount, final Account destinationAccount) {
        if (amount > highAmountThreshold ) {
            return "high";
        }
        if (amount >= mediumAmountThreshold) {
            return "medium";
        }
        return "low";
    }


    //TODO Mejorar la lógica para determinar si el destino es internacional
    //private boolean esDestinoInternacional(final Account account) {
    //    return account != null && !"AR".equalsIgnoreCase(account.getPais());
    //}
}
