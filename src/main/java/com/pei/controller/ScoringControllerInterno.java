package com.pei.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pei.service.ScoringServiceInterno;

@RestController
@RequestMapping("/api/scorings")
public class ScoringControllerInterno {

    private final ScoringServiceInterno scoringServiceInterno;

    public ScoringControllerInterno(ScoringServiceInterno scoringServiceInterno) {
        this.scoringServiceInterno = scoringServiceInterno;
    }

    @PostMapping("")
        public ResponseEntity<Void> createPeriod(
                @RequestParam String startDateStr, //14-08-2025
                @RequestParam String endDateStr, //14-09-2025
                @RequestParam Integer rojoStart, //0
                @RequestParam Integer rojoEnd, //49
                @RequestParam Integer amarilloStart, //50
                @RequestParam Integer amarilloEnd, //69
                @RequestParam Integer verdeStart, //70
                @RequestParam Integer verdeEnd) { //100

            LocalDateTime startDate = LocalDateTime.parse(startDateStr);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr);

            scoringServiceInterno.createPeriodScorings(startDate, endDate,
                    rojoStart, rojoEnd, amarilloStart, amarilloEnd, verdeStart, verdeEnd);

            return ResponseEntity.ok().build();
}
}
