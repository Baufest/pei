package com.pei.domain.UserEvent;

public enum UserEventType {
    CHANGE_EMAIL("Change Email"),
    CHANGE_PHONE_NUMBER("Change Phone Number"),
    CHANGE_NAME("Change Name"),
    CHANGE_RISK("Change Risk"),
    CHANGE_PROFILE("Change Profile"),
    CHANGE_AVERAGE_MONTHLY_SPENDING("Change Average Monthly Spending"),
    CHANGE_PASSWORD("Change Password");

    private final String type;

    UserEventType(String evenType) {
        this.type = evenType;
    }

    public String getEventType() {
        return this.type;
    }

    public boolean CriticEvent() {
        return this == CHANGE_EMAIL || this == CHANGE_PHONE_NUMBER || this == CHANGE_PASSWORD;
    }
}
