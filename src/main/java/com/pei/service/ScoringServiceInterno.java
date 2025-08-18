package com.pei.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pei.domain.Scoring;
import com.pei.repository.ScoringRepository;

@Service
public class ScoringServiceInterno {

    private final ScoringRepository scoringRepository;

    public ScoringServiceInterno(ScoringRepository scoringRepository) {
        this.scoringRepository = scoringRepository;
    }

    public String getScoringColorBasedInUserScore(Integer userScore) {
    LocalDateTime now = LocalDateTime.now();
    return scoringRepository
            .findByStartDateBeforeAndEndDateAfterAndScoreStartLessThanEqualAndScoreEndGreaterThanEqual(
                    now, now, userScore, userScore)
            .map(Scoring::getColor)
            .orElse("Sin scoring asignado");
    }

   @Transactional
   //Se da una fecha en común para los scorings. Si se solapa fecha con scoring ya creado, no se persiste.
    public void createPeriodScorings(LocalDateTime startDate, LocalDateTime endDate,
                                    Integer rojoStart, Integer rojoEnd,
                                    Integer amarilloStart, Integer amarilloEnd,
                                    Integer verdeStart, Integer verdeEnd) {

        List<Scoring> newScorings = List.of(
            new Scoring("Rojo", rojoStart, rojoEnd, startDate, endDate),
            new Scoring("Amarillo", amarilloStart, amarilloEnd, startDate, endDate),
            new Scoring("Verde", verdeStart, verdeEnd, startDate, endDate)
        );

        for (Scoring scoring : newScorings) {
            // Validar solapamiento para el mismo color
            if (scoringRepository.existsByColorAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                    scoring.getColor(), scoring.getEndDate(), scoring.getStartDate())) {
                throw new IllegalArgumentException(
                        "El rango de fechas se solapa con otro scoring del mismo color: " + scoring.getColor());
            }
            scoringRepository.save(scoring);
        }
    }
}