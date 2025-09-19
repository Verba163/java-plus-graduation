package ru.practicum.ewm.events.service.users;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.error.exception.DataIntegrityViolationException;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.parameters.EventsForUserParameters;
import ru.practicum.ewm.events.dto.parameters.UpdateEventParameters;
import ru.practicum.ewm.events.dto.parameters.UpdateRequestsStatusParameters;
import ru.practicum.ewm.events.dto.requests.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.events.dto.requests.EventRequestStatusUpdateResult;
import ru.practicum.ewm.events.dto.requests.UpdateEventCommonRequest;
import ru.practicum.ewm.events.enums.EventPublishState;
import ru.practicum.ewm.events.enums.UserUpdateRequestAction;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.service.params.assemblers.users.UserEventDtoAssembler;
import ru.practicum.ewm.events.service.params.stats.EventStatsService;
import ru.practicum.ewm.events.service.users.params.validation.UserEventServiceValidation;
import ru.practicum.ewm.events.storage.EventsRepository;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.util.Util;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserEventsServiceImpl implements UserEventsService {

    private final EventsRepository eventsRepository;
    private final UserEventServiceValidation userEventServiceValidation;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final UserEventDtoAssembler userEventDtoAssembler;
    private final RequestMapper requestMapper;
    private final EventStatsService eventStatsService;


    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsCreatedByUser(EventsForUserParameters params, HttpServletRequest request) {

        Long userId = params.getUserId();
        userEventServiceValidation.ensureUserExists(userId);

        Pageable pageable = createPageable(params.getFrom(), params.getSize());

        List<Event> userEvents = eventsRepository.findAllByInitiatorIdIs(userId, pageable).stream()
                .toList();

        eventStatsService.recordHit(request);

        return userEventDtoAssembler.mapToEventShortDtoList(userEvents);
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        userEventServiceValidation.checkEventDateIsValid(newEventDto.getEventDate());
        User user = userEventServiceValidation.getUserOrThrow(userId);
        Category category = userEventServiceValidation.getCategoryOrThrow(newEventDto.getCategory());

        Event event = eventMapper.fromNewEventDto(newEventDto, category);
        event.setInitiator(user);
        event.setCreatedOn(Util.getNowTruncatedToSeconds());

        Event saved = eventsRepository.save(event);
        return userEventDtoAssembler.mapToEventFullDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long userId, Long eventId, HttpServletRequest request) {
        Event event = userEventServiceValidation.getEventOrThrow(eventId);
        userEventServiceValidation.checkUserRightsOrThrow(userId, event);

        eventStatsService.recordHit(request);

        return userEventDtoAssembler.mapToEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(UpdateEventParameters params) {

        Event event = userEventServiceValidation.getEventOrThrow(params.getEventId());
        userEventServiceValidation.checkUserRightsOrThrow(params.getUserId(), event);

        if (!userEventServiceValidation.canUpdateEvent(event.getEventPublishState())) {
            throw new DataIntegrityViolationException("Only pending or canceled events can be changed.");
        }

        UpdateEventCommonRequest commonRequest = eventMapper.userUpdateRequestToCommonRequest(
                params.getUpdateEventUserRequest()
        );

        userEventServiceValidation.updateEventProperties(event, commonRequest);

        if (params.getUpdateEventUserRequest().getStateAction() != null) {
            switch (params.getUpdateEventUserRequest().getStateAction()) {
                case CANCEL_REVIEW -> event.setEventPublishState(EventPublishState.CANCELED);
                case SEND_TO_REVIEW -> event.setEventPublishState(EventPublishState.PENDING);
            }
        }

        Event saved = eventsRepository.save(event);
        return userEventDtoAssembler.mapToEventFullDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId) {
        Event event = userEventServiceValidation.getEventOrThrow(eventId);
        userEventServiceValidation.checkUserRightsOrThrow(userId, event);

        return requestRepository.findByEventId(eventId).stream()
                .map(requestMapper::toRequestDto)
                .toList();
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsForEvent(UpdateRequestsStatusParameters params) {
        Event event = userEventServiceValidation.getEventOrThrow(params.getEventId());
        userEventServiceValidation.checkUserRightsOrThrow(params.getUserId(), event);

        EventRequestStatusUpdateRequest statusRequest = params.getEventRequestStatusUpdateRequest();
        List<Request> requests = requestRepository.findAllById(statusRequest.getRequestIds());

        userEventServiceValidation.validateRequestsArePending(requests);

        long confirmedCount = eventStatsService.getConfirmedRequests(List.of(event.getId())).getOrDefault(event.getId(), 0L);
        int participantLimit = event.getParticipantLimit();

        long slotsLeft = (participantLimit == 0) ? requests.size() : (participantLimit - confirmedCount);

        if (slotsLeft <= 0) {
            throw new DataIntegrityViolationException(
                    String.format("Event id=%d is fully booked for requests.", event.getId())
            );
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(new ArrayList<>(), new ArrayList<>());

        for (Request req : requests) {
            if (statusRequest.getStatus() == UserUpdateRequestAction.REJECTED || slotsLeft <= 0) {
                req.setStatus(RequestStatus.REJECTED);
                result.getRejectedRequests().add(requestMapper.toRequestDto(req));
            } else {
                req.setStatus(RequestStatus.CONFIRMED);
                result.getConfirmedRequests().add(requestMapper.toRequestDto(req));
                slotsLeft--;
            }
        }

        requestRepository.saveAll(requests);
        return result;
    }

    private Pageable createPageable(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Parameters 'from' and 'size' must be positive.");
        }
        return PageRequest.of(from / size, size);
    }
}