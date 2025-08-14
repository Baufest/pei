package com.pei.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pei.domain.Chargeback;


@Repository
public interface ChargebackRepository extends JpaRepository<Chargeback, Long>{

    List<Chargeback> findByUserId(Long userId);
}
