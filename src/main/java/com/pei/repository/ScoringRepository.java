package com.pei.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pei.domain.Scoring;

@Repository
public interface ScoringRepository extends JpaRepository<Scoring, Long> {

    //Verifica si existe algun scoring de ese color, que se solape en el rango de fechas
    boolean existsByColorAndStartDateLessThanEqualAndEndDateGreaterThanEqual(String color, LocalDateTime endDate, LocalDateTime startDate);
    //busca el scoring basado en el score dado
    Optional<Scoring> findByStartDateBeforeAndEndDateAfterAndScoreStartLessThanEqualAndScoreEndGreaterThanEqual(
        LocalDateTime now1, LocalDateTime now2, Integer score, Integer score2);
}
