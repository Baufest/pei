package com.pei.service;

import java.time.LocalDate;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ClienteService {

    private static final String[] NOMBRES = {"Juan", "María", "Pedro", "Ana", "Luis", "Carmen"};
    private static final String[] APELLIDOS = {"Gómez", "Pérez", "Rodríguez", "Fernández", "López", "Martínez"};
    private static final String[] PERFILES = {"ahorrista", "inversionista", "alto riesgo", "cliente regular"};
    private static final String[] RIESGO = {"normal", "bajo", "medio", "alto", "irrecuperable"};
    private static final String[] TIPOCLIENTE = {"individuo", "empresa"};

    private static final Random random = new Random();
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Simula la obtención de un cliente en formato JSON a partir de su ID.
     * Los datos se generan dinámicamente en cada invocación.
     */
    public static String obtenerClienteJson(int idCliente) {
        try {
            ObjectNode cliente = mapper.createObjectNode();
            cliente.put("id", idCliente);
            cliente.put("name", NOMBRES[random.nextInt(NOMBRES.length)]);
            cliente.put("apellidos", APELLIDOS[random.nextInt(APELLIDOS.length)]);
            cliente.put("telefono", generarNumeroTelefono(false));
            cliente.put("celular", generarNumeroTelefono(true));
            cliente.put("direccion", "Calle " + (random.nextInt(200) + 1) + ", Ciudad X");
            cliente.put("codigoPostal", String.format("%05d", random.nextInt(100000)));
            cliente.put("fechaNacimiento", generarFechaNacimiento());
            cliente.put("numeroDocumento", String.format("%08d", random.nextInt(100000000)));
            cliente.put("profile", PERFILES[random.nextInt(PERFILES.length)]);
            cliente.put("fechaAlta", LocalDate.now().minusDays(random.nextInt(2000)).toString());
            cliente.put("gastoPromedioMensual", random.nextInt(9000) + 1000);
            cliente.put("risk", RIESGO[random.nextInt(PERFILES.length)]);
            cliente.put("tipoCliente", TIPOCLIENTE[random.nextInt(TIPOCLIENTE.length)]);
            cliente.set("chargebacks", generarChargebacks());

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(cliente);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    private static ArrayNode generarChargebacks() {
        ArrayNode chargebacks = mapper.createArrayNode();
        int cantidad = random.nextInt(11); // 0 a 10 registros

        for (int i = 0; i < cantidad; i++) {
            ObjectNode cb = mapper.createObjectNode();
            cb.put("fechaCreacion", LocalDate.now().minusDays(random.nextInt(365)).toString());
            cb.put("monto", random.nextInt(99000) + 1000);
            boolean aceptado = random.nextBoolean();
            cb.put("aceptado", aceptado);

            if (aceptado) {
                cb.put("fechaPago", LocalDate.now().minusDays(random.nextInt(30)).toString());
            } else {
                cb.putNull("fechaPago");
            }

            chargebacks.add(cb);
        }
        return chargebacks;
    }

    private static String generarNumeroTelefono(boolean celular) {
        String prefijo = celular ? "9" : "4";
        return prefijo + String.format("%09d", random.nextInt(1000000000));
    }

    private static String generarFechaNacimiento() {
        int edadMinima = 18;
        int edadMaxima = 80;
        int edad = edadMinima + random.nextInt(edadMaxima - edadMinima + 1);

        LocalDate hoy = LocalDate.now();
        LocalDate fechaNacimiento = hoy.minusYears(edad).minusDays(random.nextInt(365));
        return fechaNacimiento.toString();
    }
}
