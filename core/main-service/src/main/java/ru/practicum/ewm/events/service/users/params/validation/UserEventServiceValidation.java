package ru.practicum.ewm.events.service.users.params.validation;

import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.events.dto.requests.UpdateEventCommonRequest;
import ru.practicum.ewm.events.enums.EventPublishState;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface UserEventServiceValidation {

    void validateRequestsArePending(List<Request> requests);

    void updateEventProperties(Event event, UpdateEventCommonRequest props);

    Event getEventOrThrow(long eventId);

    User getUserOrThrow(long userId);

    Category getCategoryOrThrow(long categoryId);

    void ensureUserExists(long userId);

    void checkUserRightsOrThrow(long userId, Event event);

    void checkEventDateIsValid(LocalDateTime eventDate);

    boolean canUpdateEvent(EventPublishState state);
}
