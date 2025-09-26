package ru.practicum.events.service.util;

import org.springframework.stereotype.Component;
import ru.practicum.events.model.Event;
import ru.practicum.interaction.error.exception.DataIntegrityViolationException;
import ru.practicum.interaction.events.enums.AdminEventAction;
import ru.practicum.interaction.events.enums.EventPublishState;
import ru.practicum.interaction.util.Util;

import java.time.LocalDateTime;

@Component
public class ProcessAction {

    public void processStateAction(Event event, AdminEventAction stateAction) {
        EventPublishState currentState = event.getEventPublishState();

        switch (stateAction) {
            case REJECT_EVENT -> {
                if (currentState == EventPublishState.PUBLISHED) {
                    throw new DataIntegrityViolationException(
                            "Can't REJECT event which is PUBLISHED already.");
                }
                event.setEventPublishState(EventPublishState.CANCELED);
            }
            case PUBLISH_EVENT -> {
                if (currentState != EventPublishState.PENDING) {
                    throw new DataIntegrityViolationException(
                            "Can't PUBLISH event which is not PENDING yet.");
                }
                LocalDateTime now = Util.getNowTruncatedToSeconds();

                if (now.plusHours(1).isAfter(event.getEventDate())) {
                    throw new DataIntegrityViolationException(
                            "There are less than 1 hour between publish time and event time.");
                }

                event.setEventPublishState(EventPublishState.PUBLISHED);
                event.setPublishedOn(now);
            }
            default -> throw new UnsupportedOperationException(String.format(
                    "Unknown state action: %s", stateAction
            ));
        }
    }
}
