package com.pei.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pei.domain.TransactionParam;

public interface TransactionParamsRepository extends JpaRepository<TransactionParam, Long> {
    Optional<TransactionParam> findByKeyNameAndActiveTrue(String keyName);
}
