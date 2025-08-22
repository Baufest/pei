package com.pei.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pei.domain.AmountLimit;

@Repository
public interface AmountLimitRepository extends JpaRepository<AmountLimit, Long> {

    AmountLimit findByClientTypeAndStartingDateBeforeAndExpirationDateAfter(String clientType, LocalDateTime now,
            LocalDateTime now2);

    boolean existsByClientTypeAndStartingDateLessThanEqualAndExpirationDateGreaterThanEqual(String clientType,
            LocalDateTime expirationDate, LocalDateTime startingDate);
    
}
