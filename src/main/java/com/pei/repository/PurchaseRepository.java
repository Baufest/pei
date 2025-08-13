package com.pei.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pei.dto.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long>{

    public List<Purchase> findByUserId(Long userId);

}
