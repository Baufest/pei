package com.pei.controller;

import com.pei.domain.Transaction;
import com.pei.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<List<Transaction>> saveTransactions(@RequestBody List<Transaction> transactions) {
        List<Transaction> savedTransactions = transactionService.saveAll(transactions);
        return ResponseEntity.ok(savedTransactions);
    }
}
