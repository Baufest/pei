package com.pei.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Scoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String color;

    @Column
    private Integer scoreStart;

    @Column
    private Integer scoreEnd;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    public Scoring() {
    }

    public Scoring(String color, Integer scoreStart, Integer scoreEnd, LocalDateTime startDate, LocalDateTime endDate) {
        this.color = color;
        this.scoreStart = scoreStart;
        this.scoreEnd = scoreEnd;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getScoreStart() {
        return scoreStart;
    }

    public void setScoreStart(Integer scoreStart) {
        this.scoreStart = scoreStart;
    }

    public Integer getScoreEnd() {
        return scoreEnd;
    }

    public void setScoreEnd(Integer scoreEnd) {
        this.scoreEnd = scoreEnd;
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
}
