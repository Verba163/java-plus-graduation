package ru.practicum.events.service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.events.model.Event;
import ru.practicum.events.storage.EventsRepository;
import ru.practicum.interaction.error.exception.ConflictException;
import ru.practicum.interaction.error.exception.ValidationException;
import ru.practicum.interaction.events.enums.EventPublishState;
import ru.practicum.interaction.util.Util;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final EventsRepository eventsRepository;

    public Event getEventWithCheck(long eventId) {
        return eventsRepository.findById(eventId)
                .orElseThrow(() -> new ConflictException(String.format(
                        "Event id = %d not found.", eventId)
                ));
    }

    public void checkEventDateIsValid(LocalDateTime eventDate) {
        LocalDateTime nowPlusTwoHours = Util.getNowTruncatedToSeconds().plusHours(2);
        if (eventDate.isBefore(nowPlusTwoHours)) {
            throw new ValidationException("DateTime of event must be at least 2 hours from now.");
        }
    }

    public void checkUserRightsOrThrow(long userId, Event event) {
        if (!event.getInitiatorId().equals(userId)) {
            throw new ConflictException(
                    String.format("Access denied for user id=%d with event id=%d", userId, event.getId())
            );
        }
    }

    public boolean canUpdateEvent(EventPublishState state) {
        return state == EventPublishState.CANCELED || state == EventPublishState.PENDING;
    }
}
