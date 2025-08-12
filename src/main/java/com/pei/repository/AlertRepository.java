package com.pei.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pei.dto.Alert;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long>{
    
}
