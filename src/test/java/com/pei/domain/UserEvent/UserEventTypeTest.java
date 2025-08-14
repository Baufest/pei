package com.pei.domain.UserEvent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEventTypeTest {

    @Test
    void getEventType() {
        UserEventType changeEmail = UserEventType.CHANGE_EMAIL;
        assertAll(
                () -> assertEquals("Change Email", changeEmail.getEventType()),
                () -> assertEquals(UserEventType.CHANGE_EMAIL, changeEmail)
        );
    }

    @Test
    void criticEvent() {
        UserEventType changeEmail = UserEventType.CHANGE_EMAIL; // Crucial event
        UserEventType changePhoneNumber = UserEventType.CHANGE_PHONE_NUMBER; // Crucial event
        UserEventType changeName = UserEventType.CHANGE_NAME; // Non-crucial event

        assertAll(
                () -> assertTrue(changeEmail.CriticEvent()),
                () -> assertTrue(changePhoneNumber.CriticEvent()),
                () -> assertFalse(changeName.CriticEvent())
        );
    }
}
