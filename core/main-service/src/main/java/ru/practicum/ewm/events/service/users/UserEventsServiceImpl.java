package ru.practicum.ewm.events.service.users;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.StatHitDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.client.StatFeignClient;
import ru.practicum.ewm.error.exception.ConflictException;
import ru.practicum.ewm.error.exception.DataIntegrityViolationException;
import ru.practicum.ewm.error.exception.ValidationException;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.dto.parameters.EventsForUserParameters;
import ru.practicum.ewm.events.dto.parameters.MappingEventParameters;
import ru.practicum.ewm.events.dto.parameters.UpdateEventParameters;
import ru.practicum.ewm.events.dto.parameters.UpdateRequestsStatusParameters;
import ru.practicum.ewm.events.dto.requests.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.events.dto.requests.EventRequestStatusUpdateResult;
import ru.practicum.ewm.events.dto.requests.UpdateEventCommonRequest;
import ru.practicum.ewm.events.enums.EventPublishState;
import ru.practicum.ewm.events.enums.UserUpdateRequestAction;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.storage.EventsRepository;
import ru.practicum.ewm.events.views.EventsViewsGetter;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserEventsServiceImpl implements UserEventsService {

    private final EventsRepository eventsRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final CategoryMapper categoryMapper;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final RequestMapper requestMapper;
    private final StatFeignClient statFeignClient;
    private final EventsViewsGetter eventsViewsGetter;


    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsCreatedByUser(EventsForUserParameters params, HttpServletRequest request) {

        Long userId = params.getUserId();
        ensureUserExists(userId);

        Pageable pageable = createPageable(params.getFrom(), params.getSize());

        List<Event> userEvents = eventsRepository.findAllByInitiatorIdIs(userId, pageable).stream()
                .toList();

        StatHitDto statHitDto = StatHitDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        statFeignClient.hit(statHitDto);

        return mapToEventShortDtoList(userEvents);
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        checkEventDateIsValid(newEventDto.getEventDate());
        User user = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(newEventDto.getCategory());

        Event event = eventMapper.fromNewEventDto(newEventDto, category);
        event.setInitiator(user);
        event.setCreatedOn(Util.getNowTruncatedToSeconds());

        Event saved = eventsRepository.save(event);
        return mapToEventFullDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long userId, Long eventId, HttpServletRequest request) {
        Event event = getEventOrThrow(eventId);
        checkUserRightsOrThrow(userId, event);

        StatHitDto statHitDto = StatHitDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        statFeignClient.hit(statHitDto);

        return mapToEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(UpdateEventParameters params) {

        Event event = getEventOrThrow(params.getEventId());
        checkUserRightsOrThrow(params.getUserId(), event);

        if (!canUpdateEvent(event.getEventPublishState())) {
            throw new DataIntegrityViolationException("Only pending or canceled events can be changed.");
        }

        UpdateEventCommonRequest commonRequest = eventMapper.userUpdateRequestToCommonRequest(
                params.getUpdateEventUserRequest()
        );

        updateEventProperties(event, commonRequest);

        if (params.getUpdateEventUserRequest().getStateAction() != null) {
            switch (params.getUpdateEventUserRequest().getStateAction()) {
                case CANCEL_REVIEW -> event.setEventPublishState(EventPublishState.CANCELED);
                case SEND_TO_REVIEW -> event.setEventPublishState(EventPublishState.PENDING);
            }
        }

        Event saved = eventsRepository.save(event);
        return mapToEventFullDto(saved);
    }

    private void updateEventProperties(Event event, UpdateEventCommonRequest props) {
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
    public EventRequestStatusUpdateResult updateRequestsForEvent(UpdateRequestsStatusParameters params) {
        Event event = getEventOrThrow(params.getEventId());
        checkUserRightsOrThrow(params.getUserId(), event);

        EventRequestStatusUpdateRequest statusRequest = params.getEventRequestStatusUpdateRequest();
        List<Request> requests = requestRepository.findAllById(statusRequest.getRequestIds());

        validateRequestsArePending(requests);

        long confirmedCount = getConfirmedRequestsMap(List.of(event.getId())).getOrDefault(event.getId(), 0L);
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

    private void validateRequestsArePending(List<Request> requests) {
        for (Request request : requests) {
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new DataIntegrityViolationException(
                        String.format("Request id=%d must have status PENDING.", request.getId())
                );
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId) {
        Event event = getEventOrThrow(eventId);
        checkUserRightsOrThrow(userId, event);

        return requestRepository.findByEventId(eventId).stream()
                .map(requestMapper::toRequestDto)
                .toList();
    }

    private Event getEventOrThrow(long eventId) {
        return eventsRepository.findById(eventId)
                .orElseThrow(() -> new ConflictException(String.format("Event id=%d not found.", eventId)));
    }

    private User getUserOrThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ConflictException(String.format("User id=%d not found.", userId)));
    }

    private Category getCategoryOrThrow(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ConflictException(String.format("Category id=%d not found.", categoryId)));
    }

    private void ensureUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ConflictException(String.format("User id=%d not found.", userId));
        }
    }

    private void checkUserRightsOrThrow(long userId, Event event) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(
                    String.format("Access denied for user id=%d with event id=%d", userId, event.getId())
            );
        }
    }

    private void checkEventDateIsValid(LocalDateTime eventDate) {
        LocalDateTime nowPlusTwoHours = Util.getNowTruncatedToSeconds().plusHours(2);
        if (eventDate.isBefore(nowPlusTwoHours)) {
            throw new ValidationException("DateTime of event must be at least 2 hours from now.");
        }
    }

    private boolean canUpdateEvent(EventPublishState state) {
        return state == EventPublishState.CANCELED || state == EventPublishState.PENDING;
    }

    private Pageable createPageable(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("Parameters 'from' and 'size' must be positive.");
        }
        return PageRequest.of(from / size, size);
    }


    private Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        return eventsRepository.getConfirmedRequestsForEvents(eventIds).stream()
                .collect(Collectors.toMap(List::getFirst, List::getLast));
    }

    private Map<Long, Long> getEventsViewsMap(List<Long> eventIds) {
        return eventsViewsGetter.getEventsViewsMap(eventIds);
    }

    private EventFullDto mapToEventFullDto(Event event) {
        long id = event.getId();
        Map<Long, Long> confirmedReqs = getConfirmedRequestsMap(List.of(id));
        Map<Long, Long> viewsMap = getEventsViewsMap(List.of(id));

        return eventMapper.toEventFullDto(
                MappingEventParameters.builder()
                        .event(event)
                        .categoryDto(categoryMapper.toCategoryDto(event.getCategory()))
                        .initiator(userMapper.toUserShortDto(event.getInitiator()))
                        .confirmedRequests(confirmedReqs.getOrDefault(id, 0L))
                        .views(viewsMap.getOrDefault(id, 0L))
                        .build()
        );
    }

    private List<EventShortDto> mapToEventShortDtoList(List<Event> events) {
        List<Long> ids = events.stream().map(Event::getId).toList();
        Map<Long, Long> confirmedReqs = getConfirmedRequestsMap(ids);
        Map<Long, Long> viewsMap = getEventsViewsMap(ids);

        return events.stream().map(event -> eventMapper.toEventShortDto(
                MappingEventParameters.builder()
                        .event(event)
                        .categoryDto(categoryMapper.toCategoryDto(event.getCategory()))
                        .initiator(userMapper.toUserShortDto(event.getInitiator()))
                        .confirmedRequests(confirmedReqs.getOrDefault(event.getId(), 0L))
                        .views(viewsMap.getOrDefault(event.getId(), 0L))
                        .build()
        )).toList();
    }
}