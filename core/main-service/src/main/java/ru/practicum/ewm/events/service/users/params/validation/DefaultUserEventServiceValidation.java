package ru.practicum.ewm.events.service.users.params.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.error.exception.ConflictException;
import ru.practicum.ewm.error.exception.DataIntegrityViolationException;
import ru.practicum.ewm.error.exception.ValidationException;
import ru.practicum.ewm.events.dto.LocationDto;
import ru.practicum.ewm.events.dto.requests.UpdateEventCommonRequest;
import ru.practicum.ewm.events.enums.EventPublishState;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.storage.EventsRepository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.Util;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DefaultUserEventServiceValidation implements UserEventServiceValidation {

    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public void validateRequestsArePending(List<Request> requests) {
        for (Request request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new DataIntegrityViolationException(
                        String.format("Request id=%d must have status PENDING.", request.getId())
                );
            }
        }
    }

    @Override
    public void updateEventProperties(Event event, UpdateEventCommonRequest props) {
        if (props.getEventDate() != null) {
            checkEventDateIsValid(props.getEventDate());
            event.setEventDate(props.getEventDate());
        }
        if (props.getCategory() != null) {
            event.setCategory(getCategoryOrThrow(props.getCategory()));
        }
        if (props.getTitle() != null) event.setTitle(props.getTitle());
        if (props.getDescription() != null) event.setDescription(props.getDescription());
        if (props.getAnnotation() != null) event.setAnnotation(props.getAnnotation());
        if (props.getLocation() != null) {
            LocationDto loc = props.getLocation();
            event.setLocationLat(loc.getLat());
            event.setLocationLon(loc.getLon());
        }
        if (props.getRequestModeration() != null) event.setRequestModeration(props.getRequestModeration());
        if (props.getPaid() != null) event.setPaid(props.getPaid());
        if (props.getParticipantLimit() != null) event.setParticipantLimit(props.getParticipantLimit());
    }

    @Override
    public Event getEventOrThrow(long eventId) {
        return eventsRepository.findById(eventId)
                .orElseThrow(() -> new ConflictException(String.format("Event id=%d not found.", eventId)));
    }

    @Override
    public User getUserOrThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ConflictException(String.format("User id=%d not found.", userId)));
    }

    @Override
    public Category getCategoryOrThrow(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ConflictException(String.format("Category id=%d not found.", categoryId)));
    }

    @Override
    public void ensureUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ConflictException(String.format("User id=%d not found.", userId));
        }
    }

    @Override
    public void checkUserRightsOrThrow(long userId, Event event) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(
                    String.format("Access denied for user id=%d with event id=%d", userId, event.getId())
            );
        }
    }

    @Override
    public void checkEventDateIsValid(LocalDateTime eventDate) {
        LocalDateTime nowPlusTwoHours = Util.getNowTruncatedToSeconds().plusHours(2);
        if (eventDate.isBefore(nowPlusTwoHours)) {
            throw new ValidationException("DateTime of event must be at least 2 hours from now.");
        }
    }

    @Override
    public boolean canUpdateEvent(EventPublishState state) {
        return state == EventPublishState.CANCELED || state == EventPublishState.PENDING;
    }
}
