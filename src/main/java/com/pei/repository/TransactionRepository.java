package com.pei.repository;

import com.pei.domain.Transaction;

import java.time.LocalDateTime;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.user.id = :userId AND t.date >= :fromDate")
    Long countTransactionsFromDate(@Param("userId") Long userId, @Param("fromDate") LocalDateTime fromDate);


    @Query("""
    SELECT t
    FROM Transaction t
    WHERE t.user.id = :userId
      AND t.destinationAccount.owner.id <> :userId
    ORDER BY t.date DESC
    """)
    List<Transaction> findRecentTransferByUserId(@Param("userId") Long userId);
}
