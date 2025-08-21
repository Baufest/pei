package com.pei.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pei.domain.Account;
import com.pei.domain.Transaction;
import com.pei.domain.User;
import com.pei.dto.Alert;
import com.pei.dto.ChargebackDTO;

@Service
public class AccountService {

    private ObjectMapper objectMapper;

    private AccountParamsService accountParamsService;

    public AccountService(AccountParamsService accountParamsService, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.accountParamsService = accountParamsService;
    }

    public Alert validateNewAccountTransfers(Account destinationAccount, Transaction currentTransaction) {
        LocalDateTime limitDate = currentTransaction.getDate().minusHours(48);

        // If the account creation date is between (48 hours before) && (transaction
        // date)
        if (destinationAccount.getCreationDate().isAfter(limitDate)
                && destinationAccount.getCreationDate().isBefore(currentTransaction.getDate())) {
            return new Alert(null, "Alerta: Se transfiere dinero a una cuenta creada hace menos de 48 horas.");
        } else {
            return new Alert(null, "Transferencia permitida.");
        }
    }

    public Alert validateHighRiskClient(Long userId) {
        if (userId == null) {
            return new Alert(null, "Alerta: userId no puede ser null.");
        }
        String clientJson = ClienteService.obtenerClienteJson(userId.intValue());
        if (clientJson == null || clientJson.isBlank()) {
            return new Alert(userId, "Alerta: Datos de cliente no encontrados. (JSON vacío)");
        }
        List<ChargebackDTO> chargebacks = new ArrayList<>();

        try { // Se implementa esta lógica a fin de no modificar generarChargebacks() en
              // ClienteService
            JsonNode root = objectMapper.readTree(clientJson); // Para leer campos que nos interesen (mapea a arbol de
                                                               // nodos)

            // Guardamos clientType buscado por el fieldName
            String clientType = root.path("clientType").asText(null);
            if (clientType == null) {
                return new Alert(userId, "Alerta: Tipo de cliente no encontrado. (NULL)");
            }

            // Guardamos el JsonNode de chargebacks buscado por el fieldName
            JsonNode chargebacksNode = root.path("chargebacks");
            if (chargebacksNode.isMissingNode() || !chargebacksNode.isArray()) {
                return new Alert(userId, "Alerta: Lista de chargebacks no encontrada. (NULL)");
            }

            // Mapeamos cada JsonNode(chargebackNode) a un ChargebackDTO y lo añadimos a la
            // lista de chargebacks(dto)
            for (JsonNode cbNode : chargebacksNode) {
                if (!cbNode.hasNonNull("fechaCreacion")) {
                    return new Alert(userId, "Alerta: Chargeback con fecha de creación inválida.");
                }
                if (!cbNode.hasNonNull("monto") || cbNode.get("monto").asInt() <= 0) {
                    return new Alert(userId, "Alerta: Chargeback con monto inválido.");
                }
                if (!cbNode.has("aceptado")) {
                    return new Alert(userId, "Alerta: Chargeback sin campo 'aceptado'.");
                }
                String fechaPago = "";
                if (cbNode.has("fechaPago") && !cbNode.get("fechaPago").isNull()) {
                    String valor = cbNode.get("fechaPago").asText();
                    if (!valor.isBlank()) {
                        fechaPago = valor;
                    }
                }
                ChargebackDTO cb = new ChargebackDTO();
                cb.setFechaCreacion(cbNode.path("fechaCreacion").asText());
                cb.setMonto(cbNode.path("monto").asInt());
                cb.setAceptado(cbNode.path("aceptado").asBoolean());
                cb.setFechaPago(fechaPago);
                chargebacks.add(cb);
            }

            // logica de negocio
            if (clientType.equals("empresa")) {
                if (chargebacks.size() >= accountParamsService.getLimiteAlertaAltoRiesgoEmpresa()) {
                    return new Alert(userId, "Alerta: Cliente empresarial de alto riesgo, con múltiples chargebacks: "
                            + chargebacks.size());
                }
            } else if (clientType.equals("individuo")) {
                if (chargebacks.size() >= accountParamsService.getLimiteAlertaAltoRiesgoIndividuo()) {
                    return new Alert(userId,
                            "Alerta: Cliente individual de alto riesgo, con chargebacks: " + chargebacks.size());
                }
            } else {
                return new Alert(userId, "Alerta: Tipo de cliente desconocido: " + clientType);
            }
            return new Alert(userId, "Alerta: Cliente validado sin alertas de riesgo.");

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return new Alert(userId, "Alerta: Error al procesar los datos del cliente.");
        }

    }

    public Alert validateUserProfileTransaction(User user, Transaction transaction) {
        if (user == null || user.getProfile() == null) {
            return new Alert(null, "Alerta: Datos de usuario inválidos.");
        }

        if (transaction == null || transaction.getAmount() == null) {
            return new Alert(null, "Alerta: Datos de transacción inválidos.");
        }

        if (user.getAverageMonthlySpending() != null
                && transaction.getAmount()
                        .compareTo(user.getAverageMonthlySpending().multiply(java.math.BigDecimal.valueOf(3))) > 0
                && user.getProfile().equals("ahorrista")) {
            return new Alert(null, "Alerta: Monto inusual para perfil.");
        }

        return new Alert(null, "Perfil de usuario validado para la transacción.");
    }

}
