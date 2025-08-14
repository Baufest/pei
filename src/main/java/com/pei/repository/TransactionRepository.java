package com.pei.repository;

import com.pei.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
    SELECT t
    FROM Transaction t
    WHERE t.user.id = :userId
      AND t.destinationAccount.owner.id <> :userId
    ORDER BY t.date DESC
    """)
    List<Transaction> findRecentTransferByUserId(@Param("userId") Long userId);
}
