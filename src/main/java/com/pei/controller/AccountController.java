package com.pei.controller;

import com.pei.domain.Account.Account;
import com.pei.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Crear cuentas para un usuario
    @PostMapping
    public ResponseEntity<List<Account>> saveAccounts(@RequestBody List<Account> accounts) {
        List<Account> savedAccounts = accountService.saveAll(accounts);
        return ResponseEntity.ok(savedAccounts);
    }

    // Traer todas las cuentas de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Account>> getAccountsByUser(@PathVariable Long userId) {
        List<Account> accounts = accountService.findByUserId(userId);
        return ResponseEntity.ok(accounts);
    }
}

