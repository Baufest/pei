package com.pei.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pei.dto.Chargeback;


@Repository
public interface ChargebackRepository extends JpaRepository<Chargeback, Long>{

    public List<Chargeback> findByUserId(Long userId);
}