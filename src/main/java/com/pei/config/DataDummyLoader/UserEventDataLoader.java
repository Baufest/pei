package com.pei.config.DataDummyLoader;
import com.pei.domain.User;
import com.pei.domain.UserEvent.UserEvent;
import com.pei.domain.UserEvent.UserEventType;
import com.pei.repository.UserEventRepository;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserEventDataLoader {

    private final UserEventRepository userEventRepository;

    public UserEventDataLoader(UserEventRepository userEventRepository) {
        this.userEventRepository = userEventRepository;
    }

    public void insertUserEvents(List<User> users) {
        if (userEventRepository.count() > 0) return;

        UserEvent e1 = new UserEvent();
        e1.setUser(users.get(0));
        e1.setType(UserEventType.CHANGE_EMAIL);

        UserEvent e2 = new UserEvent();
        e2.setUser(users.get(1));
        e2.setType(UserEventType.CHANGE_PHONE_NUMBER);

        userEventRepository.saveAll(List.of(e1, e2));
    }
}
