package ru.practicum.request.params;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.interaction.error.exception.DataIntegrityViolationException;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.events.enums.EventPublishState;
import ru.practicum.interaction.request.enums.RequestStatus;
import ru.practicum.request.repository.RequestRepository;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestValidator {

    EventFullDto event;
    Long userId;
    Long eventId;
    RequestRepository requestRepository;

    public void validate() {
        validateInitiator();
        validateParticipantLimit();
        validateExistingRequest();
        validateEventState();
    }

    private void validateInitiator() {
        if (event.getInitiator().getId().equals(userId)) {
            throw new DataIntegrityViolationException("Initiator can not create requests");
        }
    }

    private void validateParticipantLimit() {
        Integer confirmedParticipants = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() > 0 && confirmedParticipants >= event.getParticipantLimit()) {
            throw new DataIntegrityViolationException("You can not create requests to Full filled events");
        }
    }

    private void validateExistingRequest() {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new DataIntegrityViolationException("You have already created a request for this event");
        }
    }

    private void validateEventState() {
        if (!event.getState().equals(EventPublishState.PUBLISHED)) {
            throw new DataIntegrityViolationException("You can not create requests to CANCEL or PENDING events");
        }
    }
}

