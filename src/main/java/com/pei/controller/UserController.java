package com.pei.controller;

import com.pei.domain.User.User;
import com.pei.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    // Guardar un nuevo usuario
    @PostMapping
    public ResponseEntity<List<User>> saverUsers(@RequestBody List<User> users) {
        List<User> savedUsers = userService.saveAll(users);
        return ResponseEntity.ok(savedUsers);
    }

    // Obtener usuario por id
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
