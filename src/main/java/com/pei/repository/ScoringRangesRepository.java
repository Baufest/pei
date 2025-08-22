package com.pei.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pei.domain.ScoringRanges;

@Repository
public interface ScoringRangesRepository extends JpaRepository<ScoringRanges, Long> {

    @Query("SELECT CASE WHEN COUNT(sr) > 0 THEN TRUE ELSE FALSE END " +
           "FROM ScoringRanges sr " +
           "WHERE sr.clientType = :clientType " +
           "AND sr.startDate <= :endDate " +
           "AND sr.endDate >= :startDate")
    boolean existsOverlappingRange(
            @Param("clientType") String clientType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

        @Query("SELECT sr FROM ScoringRanges sr " +
           "WHERE sr.clientType = :clientType " +
           "AND sr.startDate <= :date " +
           "AND sr.endDate >= :date")
    Optional<ScoringRanges> findActiveRangeForDate(
            @Param("clientType") String clientType,
            @Param("date") LocalDateTime date
    );
}
