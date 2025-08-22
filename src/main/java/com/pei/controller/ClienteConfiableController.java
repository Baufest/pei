package com.pei.controller;

import com.pei.domain.User.User;
import com.pei.dto.Alert;
import com.pei.repository.UserRepository;
import com.pei.service.ClienteConfiableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController("/api/clientes-confiables")
public class ClienteConfiableController {
    private UserRepository userRepository;

    private ClienteConfiableService clienteConfiableService;

    @PostMapping
    public ResponseEntity<Alert> clienteConfiable(@RequestBody User user) {
        try {
            Optional<User> userOpt = userRepository.findById(user.getId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body(new Alert(user.getId(), "Cliente no encontrado"));
            }
            User client = userOpt.get();

            if (clienteConfiableService.esClienteConfiable(client)) {
                return ResponseEntity.ok(new Alert(client.getId(), "El Cliente de ID = " + client.getId() + " es confiable"));
            } else {
                return ResponseEntity.ok(new Alert(client.getId(), "El Cliente de ID = " + client.getId() + " no es confiable"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new Alert(user.getId(), "Error al verificar si el cliente es confiable: " + e.getMessage()));
        }
    }
}
