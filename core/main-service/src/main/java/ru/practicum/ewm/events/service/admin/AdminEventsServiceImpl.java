package ru.practicum.ewm.events.service.admin;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.error.exception.ConflictException;
import ru.practicum.ewm.error.exception.DataIntegrityViolationException;
import ru.practicum.ewm.error.exception.ValidationException;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.parameters.MappingEventParameters;
import ru.practicum.ewm.events.dto.parameters.SearchEventsParameters;
import ru.practicum.ewm.events.dto.requests.UpdateEventAdminRequest;
import ru.practicum.ewm.events.dto.requests.UpdateEventCommonRequest;
import ru.practicum.ewm.events.enums.AdminEventAction;
import ru.practicum.ewm.events.enums.EventPublishState;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.QEvent;
import ru.practicum.ewm.events.storage.EventsRepository;
import ru.practicum.ewm.events.views.EventsViewsGetter;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.util.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminEventsServiceImpl implements AdminEventsService {

    private final EventsRepository eventsRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final EventsViewsGetter eventsViewsGetter;


    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> searchEvents(SearchEventsParameters searchParams) {
        QEvent event = QEvent.event;
        BooleanExpression condition = buildSearchConditions(searchParams, event);
        Pageable page = createPageableObject(searchParams.getFrom(), searchParams.getSize());

        List<Event> events = eventsRepository.findAll(condition, page)
                .stream()
                .toList();

        return createEventFullDtoList(events);
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = getEventWithCheck(eventId);
        UpdateEventCommonRequest commonRequest = eventMapper.adminUpdateRequestToCommonRequest(updateRequest);
        updateCommonEventProperties(event, commonRequest);

        if (updateRequest.getStateAction() != null) {
            processStateAction(event, updateRequest.getStateAction());
        }

        Event savedEvent = eventsRepository.save(event);
        return createEventFullDto(savedEvent);
    }

    private BooleanExpression buildSearchConditions(SearchEventsParameters params, QEvent event) {
        List<BooleanExpression> conditions = new ArrayList<>();

        Optional.ofNullable(params.getUsers())
                .filter(list -> !list.isEmpty())
                .ifPresent(users -> conditions.add(event.initiator.id.in(users)));

        Optional.ofNullable(params.getStates())
                .filter(list -> !list.isEmpty())
                .map(states -> states.stream()
                        .map(EventPublishState::valueOf)
                        .toList())
                .ifPresent(statesEnum -> conditions.add(event.eventPublishState.in(statesEnum)));

        Optional.ofNullable(params.getCategories())
                .filter(list -> !list.isEmpty())
                .ifPresent(categories -> conditions.add(event.category.id.in(categories)));

        Optional.ofNullable(params.getRangeStart())
                .ifPresent(start -> conditions.add(event.eventDate.after(start)));

        Optional.ofNullable(params.getRangeEnd())
                .ifPresent(end -> conditions.add(event.eventDate.before(end)));

        return conditions.stream()
                .reduce(Expressions.asBoolean(true).isTrue(), BooleanExpression::and);
    }

    private void processStateAction(Event event, AdminEventAction stateAction) {
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

    private Event getEventWithCheck(long eventId) {
        return eventsRepository.findById(eventId)
                .orElseThrow(() -> new ConflictException(String.format(
                        "Event id = %d not found.", eventId)
                ));
    }

    private Category getCategoryWithCheck(long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ConflictException(String.format(
                        "Category id = %d not found.", categoryId)
                ));
    }

    private void checkEventDateBeforeHours(LocalDateTime eventDateTime) {
        LocalDateTime now = Util.getNowTruncatedToSeconds();
        if (eventDateTime.isBefore(now.plusHours(2))) {
            throw new ValidationException("DateTime of event must be ahead more than 2 hours.");
        }
    }

    private Pageable createPageableObject(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("'from' must be >= 0 and 'size' must be > 0.");
        }
        return PageRequest.of(from / size, size);
    }

    private void updateCommonEventProperties(Event event, UpdateEventCommonRequest props) {
        Optional.ofNullable(props.getEventDate())
                .ifPresent(date -> {
                    checkEventDateBeforeHours(date);
                    event.setEventDate(date);
                });
        Optional.ofNullable(props.getCategory())
                .ifPresent(catId -> event.setCategory(getCategoryWithCheck(catId)));
        Optional.ofNullable(props.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(props.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(props.getAnnotation()).ifPresent(event::setAnnotation);

        Optional.ofNullable(props.getLocation()).ifPresent(location -> {
            event.setLocationLat(location.getLat());
            event.setLocationLon(location.getLon());
        });

        Optional.ofNullable(props.getRequestModeration()).ifPresent(event::setRequestModeration);
        Optional.ofNullable(props.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(props.getParticipantLimit()).ifPresent(event::setParticipantLimit);
    }

    private Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        return eventsRepository.getConfirmedRequestsForEvents(eventIds).stream()
                .collect(Collectors.toMap(pair -> pair.getFirst(), pair -> pair.getLast()));
    }

    private EventFullDto createEventFullDto(Event event) {
        Map<Long, Long> viewsMap = eventsViewsGetter.getEventsViewsMap(List.of(event.getId()));
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(List.of(event.getId()));

        return mapToEventFullDto(event, viewsMap, confirmedRequestsMap);
    }

    private List<EventFullDto> createEventFullDtoList(List<Event> events) {
        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();

        Map<Long, Long> viewsMap = eventsViewsGetter.getEventsViewsMap(ids);
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(ids);

        return events.stream()
                .map(event -> mapToEventFullDto(event, viewsMap, confirmedRequestsMap))
                .toList();
    }

    private EventFullDto mapToEventFullDto(Event event, Map<Long, Long> viewsMap, Map<Long, Long> confirmedRequestsMap) {
        Long views = viewsMap.getOrDefault(event.getId(), 0L);
        Long confirmed = confirmedRequestsMap.getOrDefault(event.getId(), 0L);

        MappingEventParameters params = MappingEventParameters.builder()
                .event(event)
                .categoryDto(categoryMapper.toCategoryDto(event.getCategory()))
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .views(views)
                .confirmedRequests(confirmed)
                .build();

        return eventMapper.toEventFullDto(params);
    }
}