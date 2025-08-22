package com.pei.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pei.domain.ScoringRanges;
import com.pei.service.ScoringRangesService;

@RestController
@RequestMapping("/api/scorings")
public class ScoringRangesController {

    private final ScoringRangesService scoringRangesService;

    public ScoringRangesController(ScoringRangesService scoringServiceInterno) {
        this.scoringRangesService = scoringServiceInterno;
    }

    @PostMapping("")
    public ResponseEntity<ScoringRanges> createScoringRange(
            @RequestParam String startDateStr, // 14-08-2025
            @RequestParam String endDateStr, // 14-09-2025
            @RequestParam Integer rojoEnd, // 49
            @RequestParam Integer amarilloEnd,
            @RequestParam String clientType) // 69
    {

        LocalDateTime startDate = LocalDateTime.parse(startDateStr);
        LocalDateTime endDate = LocalDateTime.parse(endDateStr);

        try {
            ScoringRanges newScoringRange = scoringRangesService.createScoringRange(
                    rojoEnd, amarilloEnd, startDate, endDate, clientType);
            return ResponseEntity.ok(newScoringRange);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).build();
        }

    }
}
