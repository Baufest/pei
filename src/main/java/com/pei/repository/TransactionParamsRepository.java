package com.pei.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pei.domain.TransactionParam;

@Repository
public interface TransactionParamsRepository extends JpaRepository<TransactionParam, Long> {
    Optional<TransactionParam> findByKeyNameAndActiveTrue(String keyName);
}
