package com.pei.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ScoringRanges {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer redScoreEnd;

    @Column
    private Integer yellowScoreEnd;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @Column
    private String clientType;

    public ScoringRanges() {
    }

    public ScoringRanges(Integer redScoreEnd,
            Integer yellowScoreEnd,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String clientType) {
        this.redScoreEnd = redScoreEnd;
        this.yellowScoreEnd = yellowScoreEnd;
        this.clientType = clientType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public Integer getRedScoreStart() {
        return 0;
    }

    public Integer setRedScoreEnd() {
        return this.redScoreEnd;
    }

    public Integer getRedScoreEnd() {
        return redScoreEnd;
    }

    public Integer getYellowScoreStart() {
        return redScoreEnd + 1;
    }

    public Integer setYellowScoreEnd() {
        return this.yellowScoreEnd;
    }

    public Integer getYellowScoreEnd() {
        return yellowScoreEnd;
    }

    public Integer getGreenScoreStart() {
        return yellowScoreEnd + 1;
    }

    public Integer getGreenScoreEnd() {
        return 100;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Object getClientType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getClientType'");
    }
}
