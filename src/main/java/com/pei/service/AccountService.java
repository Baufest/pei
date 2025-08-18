package com.pei.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pei.domain.User;
import com.pei.dto.Alert;

@Service
public class AccountService {

    private ObjectMapper objectMapper;

    public AccountService() {
        this.objectMapper = new ObjectMapper();
    }

    public Alert validateHighRiskClient(Long userId) {
        String clientJson = ClienteService.obtenerClienteJson(userId.intValue());
        User user = null;
        try {
            user = objectMapper.readValue(clientJson, User.class);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return new Alert(userId, "Alerta: Error al procesar los datos del usuario.");
        }

        if (user != null && user.getRisk() != null) {
            if (user.getRisk().equals("alto")) {
                return new Alert(userId, "Alerta: El cliente es de alto riesgo.");
            } else {
                return new Alert(userId, "Cliente verificado como de bajo riesgo.");
            }
        } else {
            return new Alert(userId, "Alerta: Usuario no encontrado.");
        }
    }
}
