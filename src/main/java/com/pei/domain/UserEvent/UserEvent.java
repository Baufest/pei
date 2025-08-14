package com.pei.domain.UserEvent;

import com.pei.domain.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class UserEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(nullable = false)
    private UserEventType type;

    @CreationTimestamp
    private LocalDateTime eventDateHour;

    public UserEvent() {}

    public UserEvent(User user, UserEventType type) {
        this.user = user;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserEventType getType() {
        return type;
    }

    public void setType(UserEventType type) {
        this.type = type;
    }

    public LocalDateTime getEventDateHour() {
        return eventDateHour;
    }

    public void setEventDateHour(LocalDateTime eventDateHour) {
        this.eventDateHour = eventDateHour;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserEvent userEvent = (UserEvent) o;
        return Objects.equals(id, userEvent.id) && Objects.equals(user, userEvent.user) && type == userEvent.type && Objects.equals(eventDateHour, userEvent.eventDateHour);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, type, eventDateHour);
    }
}
