package com.pei.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pei.domain.ScoringRanges;
import com.pei.repository.ScoringRangesRepository;

@Service
public class ScoringRangesService {

    private final ScoringRangesRepository scoringRangesRepository;

    public ScoringRangesService(ScoringRangesRepository scoringRepository) {
        this.scoringRangesRepository = scoringRepository;
    }

    public String getScoringColor(Integer clientScoring, String clientType) {

        Optional<ScoringRanges> scoringRange = getRangeForToday(clientType);
        
        if (scoringRange.isEmpty()) {
            throw new IllegalArgumentException("No hay rangos de scoring activos para hoy.");
        }   
        
        if (clientScoring <= scoringRange.get().getRedScoreEnd()) {
            return "ROJO";
        } else if (clientScoring <= scoringRange.get().getYellowScoreEnd()) {
            return "AMARILLO";
        } else {
            return "VERDE";
        }
    }
    
    private Optional<ScoringRanges> getRangeForToday(String clientType) {
        LocalDateTime today = LocalDateTime.now();
        return scoringRangesRepository.findActiveRangeForDate(clientType, today);
    }

   @Transactional
   //Se da una fecha en común para los scorings. Si se solapa fecha con scoring ya creado, no se persiste.
    public ScoringRanges createScoringRange(Integer redScoreEnd,
            Integer yellowScoreEnd,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String clientType) 
        {
        
        if (scoringRangesRepository.existsOverlappingRange(clientType, startDate, endDate)) {
            throw new IllegalArgumentException("Ya existe un rango de scoring que se solapa con las fechas proporcionadas.");
        }

        ScoringRanges newScoringRange = new ScoringRanges(redScoreEnd, yellowScoreEnd, startDate, endDate, clientType);

        return scoringRangesRepository.save(newScoringRange);}
    
}
