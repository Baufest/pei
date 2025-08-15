package com.pei.service;

import com.pei.domain.UserEvent.UserEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserEventService {

    public List<UserEvent> filterCriticEvents(List<UserEvent> userEvents) {
        if (userEvents != null && !userEvents.isEmpty()) {
            return userEvents.stream()
                    .filter(event -> event.getType().CriticEvent())
                    .toList();
        }
        // Si la lista es nula o vacía, retornamos una lista vacía
        return List.of();
    }
}
