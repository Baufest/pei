package com.pei.repository;

import com.pei.domain.Account;
import com.pei.domain.Transaction;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByDestinationAccountAndTransactionDateBetween(Account destinationAccount, LocalDateTime start,
            LocalDateTime end);
}
