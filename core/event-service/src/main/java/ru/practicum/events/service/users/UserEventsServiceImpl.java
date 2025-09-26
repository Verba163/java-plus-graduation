package ru.practicum.events.service.users;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.service.assemblers.EventDtoAssemblers;
import ru.practicum.events.service.external.category.CategoryService;
import ru.practicum.events.service.external.request.RequestService;
import ru.practicum.events.service.external.user.UserService;
import ru.practicum.events.service.stats.EventStatsService;
import ru.practicum.events.service.util.PaginationHelper;
import ru.practicum.events.service.util.UpdateProperties;
import ru.practicum.events.service.validation.EventValidator;
import ru.practicum.events.storage.EventsRepository;
import ru.practicum.interaction.category.dto.CategoryDto;
import ru.practicum.interaction.error.exception.DataIntegrityViolationException;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.events.dto.EventShortDto;
import ru.practicum.interaction.events.dto.NewEventDto;
import ru.practicum.interaction.events.dto.parameters.EventsForUserParameters;
import ru.practicum.interaction.events.dto.parameters.UpdateEventParameters;
import ru.practicum.interaction.events.dto.parameters.UpdateRequestsStatusParameters;
import ru.practicum.interaction.events.dto.requests.EventRequestStatusUpdateRequest;
import ru.practicum.interaction.events.dto.requests.EventRequestStatusUpdateResult;
import ru.practicum.interaction.events.dto.requests.UpdateEventCommonRequest;
import ru.practicum.interaction.events.enums.EventPublishState;
import ru.practicum.interaction.events.enums.UserUpdateRequestAction;
import ru.practicum.interaction.feign.clients.RequestFeignClient;
import ru.practicum.interaction.request.dto.ParticipationRequestDto;
import ru.practicum.interaction.request.enums.RequestStatus;
import ru.practicum.interaction.user.dto.UserDto;
import ru.practicum.interaction.util.Util;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserEventsServiceImpl implements UserEventsService {

    private final EventsRepository eventsRepository;
    private final EventValidator eventValidator;
    private final EventMapper eventMapper;
    private final EventDtoAssemblers eventDtoAssemblers;
    private final EventStatsService eventStatsService;
    private final RequestFeignClient requestFeignClient;
    private final PaginationHelper paginationHelper;
    private final RequestService requestService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final UpdateProperties updateProperties;


    @Override
    @Transactional
    public List<EventShortDto> getEventsCreatedByUser(EventsForUserParameters params, HttpServletRequest request) {

        Long userId = params.getUserId();

        userService.getUserShorDto(userId);

        Pageable pageable = paginationHelper.createPageableObject(params.getFrom(), params.getSize());

        List<Event> userEvents = eventsRepository.findAllByInitiatorIdIs(userId, pageable).stream()
                .toList();

        eventStatsService.recordHit(request);

        return eventDtoAssemblers.createEventShortDtoList(userEvents);
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {

        eventValidator.checkEventDateIsValid(newEventDto.getEventDate());

        UserDto user = userService.getUserById(userId);
        CategoryDto category = categoryService.getCategoryWithCheck(newEventDto.getCategory());

        Event event = eventMapper.fromNewEventDto(newEventDto, category.getId());
        event.setInitiatorId(user.getId());
        event.setCreatedOn(Util.getNowTruncatedToSeconds());

        Event saved = eventsRepository.save(event);
        return eventDtoAssemblers.createEventFullDto(saved);
    }

    @Override
    @Transactional
    public EventFullDto getEventById(Long userId, Long eventId, HttpServletRequest request) {
        Event event = eventValidator.getEventWithCheck(eventId);
        eventValidator.checkUserRightsOrThrow(userId, event);

        eventStatsService.recordHit(request);

        return eventDtoAssemblers.createEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(UpdateEventParameters params) {

        Event event = eventValidator.getEventWithCheck(params.getEventId());
        eventValidator.checkUserRightsOrThrow(params.getUserId(), event);

        if (!eventValidator.canUpdateEvent(event.getEventPublishState())) {
            throw new DataIntegrityViolationException("Only pending or canceled events can be changed.");
        }

        UpdateEventCommonRequest commonRequest = eventMapper.userUpdateRequestToCommonRequest(
                params.getUpdateEventUserRequest()
        );

        updateProperties.updateEventProperties(event, commonRequest);

        if (params.getUpdateEventUserRequest().getStateAction() != null) {
            switch (params.getUpdateEventUserRequest().getStateAction()) {
                case CANCEL_REVIEW -> event.setEventPublishState(EventPublishState.CANCELED);
                case SEND_TO_REVIEW -> event.setEventPublishState(EventPublishState.PENDING);
            }
        }

        Event saved = eventsRepository.save(event);
        return eventDtoAssemblers.createEventFullDto(saved);
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId) {
        Event event = eventValidator.getEventWithCheck(eventId);
        eventValidator.checkUserRightsOrThrow(userId, event);

        return requestFeignClient.getEventRequests(eventId);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsForEvent(UpdateRequestsStatusParameters updateParams) {
        Long userId = updateParams.getUserId();
        Long eventId = updateParams.getEventId();

        EventRequestStatusUpdateRequest statusUpdateRequest = updateParams.getEventRequestStatusUpdateRequest();

        Event event = eventValidator.getEventWithCheck(eventId);
        eventValidator.checkUserRightsOrThrow(userId, event);

        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        UserUpdateRequestAction action = statusUpdateRequest.getStatus();

        List<ParticipationRequestDto> requests = requestFeignClient.getRequestsByIds(statusUpdateRequest.getRequestIds());

        Long confirmedRequests = requestService.getConfirmedRequestsMap(List.of(eventId)).getOrDefault(eventId, 0L);
        Integer participantLimit = event.getParticipantLimit();

        long canConfirmRequestsNumber = participantLimit == 0
                ? requests.size()
                : participantLimit - confirmedRequests;

        if (canConfirmRequestsNumber <= 0) {
            throw new DataIntegrityViolationException(String.format(
                    "Event id=%d is full filled for requests.", eventId
            ));
        }

        requests.forEach(request -> {
            if (!request.getStatus().equals(RequestStatus.PENDING.toString())) {
                throw new DataIntegrityViolationException(String.format(
                        "Request id=%d must have status PENDING.", request.getId()
                ));
            }
        });

        for (ParticipationRequestDto request : requests) {
            if (action == UserUpdateRequestAction.REJECTED || canConfirmRequestsNumber <= 0) {
                request.setStatus(RequestStatus.REJECTED.toString());
                result.getRejectedRequests().add(request);
                continue;
            }

            request.setStatus(RequestStatus.CONFIRMED.toString());
            result.getConfirmedRequests().add(request);
            canConfirmRequestsNumber--;
        }

        requestFeignClient.updateRequests(requests);

        return result;
    }
}