package com.pei.domain;

import jakarta.persistence.*;

@Embeddable
public class TimeRange {
    @Column(name = "min_hour")
    private int minHour;
    @Column(name = "max_hour")
    private int maxHour;

    protected TimeRange() {
    }

    public TimeRange(int minHour, int maxHour) {
        this.minHour = minHour;
        this.maxHour = maxHour;
    }

    public int getMinHour() {
        return minHour;
    }

    public int getMaxHour() {
        return maxHour;
    }
}

