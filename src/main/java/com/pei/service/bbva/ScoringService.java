package com.pei.service.bbva;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.gson.Gson;

public class ScoringService {

    private static final Random random = new Random();
    private static final Gson gson = new Gson();

    public static String consultarScoring(int idCliente) {
        // Simular un 20% de probabilidad de error
        if (random.nextDouble() < 0.2) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 500);
            errorResponse.put("mensaje", "Error interno del servidor de scoring");
            errorResponse.put("timestamp", Instant.now().toString());
            return gson.toJson(errorResponse);
        }

        // Respuesta exitosa
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("status", 200);
        successResponse.put("mensaje", "Consulta exitosa");
        successResponse.put("idCliente", idCliente);
        successResponse.put("scoring", random.nextInt(100) + 1); // 1-100
        successResponse.put("timestamp", Instant.now().toString());

        return gson.toJson(successResponse);
    }
}