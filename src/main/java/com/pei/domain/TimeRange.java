package com.pei.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class TimeRange {
    private int minHour;
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

