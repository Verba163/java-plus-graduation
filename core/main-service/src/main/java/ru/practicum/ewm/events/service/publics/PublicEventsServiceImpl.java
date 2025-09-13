package ru.practicum.ewm.events.service.publics;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.StatHitDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.client.StatFeignClient;
import ru.practicum.ewm.comments.dto.CommentShortDto;
import ru.practicum.ewm.comments.mapper.CommentMapper;
import ru.practicum.ewm.comments.storage.CommentRepository;
import ru.practicum.ewm.error.exception.ConflictException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.exception.ValidationException;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventFullDtoWithComments;
import ru.practicum.ewm.events.dto.parameters.GetAllCommentsParameters;
import ru.practicum.ewm.events.dto.parameters.MappingEventParameters;
import ru.practicum.ewm.events.dto.parameters.SearchPublicEventsParameters;
import ru.practicum.ewm.events.enums.EventPublishState;
import ru.practicum.ewm.events.enums.SortingEvents;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.QEvent;
import ru.practicum.ewm.events.storage.EventsRepository;
import ru.practicum.ewm.events.views.EventsViewsGetter;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.util.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional
public class PublicEventsServiceImpl implements PublicEventsService {

    private final EventsRepository eventsRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final EventMapper eventMapper;
    private final StatFeignClient statFeignClient;
    private final EventsViewsGetter eventsViewsGetter;

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> searchPublicEvents(SearchPublicEventsParameters searchParams, HttpServletRequest request) {

        QEvent event = QEvent.event;

        BooleanExpression filterCondition = buildFilterCondition(event, searchParams);

        List<Event> filteredEvents = StreamSupport.stream(eventsRepository.findAll(filterCondition).spliterator(), false)
                .toList();

        List<Long> eventIds = filteredEvents.stream().map(Event::getId).toList();

        Map<Long, Long> eventsViewsMap = eventsViewsGetter.getEventsViewsMap(eventIds);
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(eventIds);

        Comparator<Event> sortingComparator = buildSortingComparator(searchParams.getSort(), eventIds);

        StatHitDto statHitDto = StatHitDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        statFeignClient.hit(statHitDto);

        return filteredEvents.stream()
                .sorted(sortingComparator)
                .skip(searchParams.getFrom())
                .limit(searchParams.getSize())
                .map(ev -> createEventFullDto(ev,
                        eventsViewsMap.getOrDefault(ev.getId(), 0L),
                        confirmedRequestsMap.getOrDefault(ev.getId(), 0L)))
                .toList();
    }

    private BooleanExpression buildFilterCondition(QEvent event, SearchPublicEventsParameters params) {

        List<BooleanExpression> conditions = new ArrayList<>();
        conditions.add(event.eventPublishState.eq(EventPublishState.PUBLISHED));

        if (params.getText() != null && !params.getText().isBlank()) {
            String text = params.getText();
            conditions.add(event.annotation.containsIgnoreCase(text).or(event.description.containsIgnoreCase(text)));
        }

        if (params.getCategories() != null && !params.getCategories().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(params.getCategories());

            conditions.add(event.category.id.in(params.getCategories()));
        }

        if (Boolean.TRUE.equals(params.getOnlyAvailable())) {
            List<Long> availableEventIds = eventsRepository.getAvailableEventIdsByParticipantLimit();
            conditions.add(event.id.in(availableEventIds));
        }

        if (params.getPaid() != null) {
            conditions.add(event.paid.eq(params.getPaid()));
        }

        if (params.getRangeStart() != null || params.getRangeEnd() != null) {
            if (params.getRangeStart() != null) {
                conditions.add(event.eventDate.after(params.getRangeStart()));
            }
            if (params.getRangeEnd() != null) {
                conditions.add(event.eventDate.before(params.getRangeEnd()));
            }
        } else {
            LocalDateTime now = Util.getNowTruncatedToSeconds();
            conditions.add(event.eventDate.after(now));
        }

        return conditions.stream()
                .reduce(Expressions.asBoolean(true).isTrue(), BooleanExpression::and);
    }

    private Comparator<Event> buildSortingComparator(SortingEvents sort, List<Long> eventIds) {
        if (sort == null) {
            return Comparator.comparing(Event::getEventDate);
        }
        return switch (sort) {
            case VIEWS -> {
                Map<Long, Long> viewsMap = eventsViewsGetter.getEventsViewsMap(eventIds);
                yield Comparator.comparingLong((Event e) -> viewsMap.getOrDefault(e.getId(), 0L)).reversed();
            }
            case COMMENTS -> {
                Map<Long, Long> commentsMap = getCommentsNumberMap(eventIds);
                yield Comparator.comparingLong((Event e) -> commentsMap.getOrDefault(e.getId(), 0L)).reversed();
            }
            default -> Comparator.comparing(Event::getEventDate);
        };
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentShortDto> getAllEventComments(GetAllCommentsParameters parameters) {
        Event event = getEventWithCheck(parameters.getEventId());
        return commentRepository.findPageableCommentsForEvent(event.getId(), parameters.getFrom(), parameters.getSize())
                .stream()
                .map(CommentMapper::toCommentShortDto)
                .toList();
    }

    @Transactional
    @Override
    public EventFullDtoWithComments getPublicEventById(Long eventId, HttpServletRequest request) {
        QEvent event = QEvent.event;
        Event foundEvent = eventsRepository.findOne(
                        event.id.eq(eventId)
                                .and(event.eventPublishState.eq(EventPublishState.PUBLISHED)))
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event id=%d not found or is not published.", eventId)));

        StatHitDto statHitDto = StatHitDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        statFeignClient.hit(statHitDto);

        return createEventFullDtoWithComments(foundEvent);
    }

    private Event getEventWithCheck(long eventId) {
        return eventsRepository.findById(eventId)
                .orElseThrow(() -> new ConflictException(String.format("Event id=%d not found.", eventId)));
    }

    private Map<Long, Long> getCommentsNumberMap(List<Long> eventIds) {
        Map<Long, Long> rawMap = commentRepository.getCommentsNumberForEvents(eventIds).stream()
                .collect(Collectors.toMap(List::getFirst, List::getLast));

        return eventIds.stream()
                .collect(Collectors.toMap(Function.identity(), id -> rawMap.getOrDefault(id, 0L)));
    }

    private EventFullDtoWithComments createEventFullDtoWithComments(Event event) {
        Long eventId = event.getId();

        Long views = eventsViewsGetter.getEventViews(eventId);
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(List.of(eventId));

        List<CommentShortDto> comments = commentRepository.findFirstCommentsForEvent(eventId, 5L).stream()
                .map(CommentMapper::toCommentShortDto)
                .toList();

        MappingEventParameters params = MappingEventParameters.builder()
                .event(event)
                .categoryDto(categoryMapper.toCategoryDto(event.getCategory()))
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .confirmedRequests(confirmedRequestsMap.getOrDefault(eventId, 0L))
                .views(views)
                .comments(comments)
                .build();

        return eventMapper.toEventFullDtoWithComments(params);
    }

    private EventFullDto createEventFullDto(Event event, long views, long confirmedRequests) {
        MappingEventParameters params = MappingEventParameters.builder()
                .event(event)
                .categoryDto(categoryMapper.toCategoryDto(event.getCategory()))
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .confirmedRequests(confirmedRequests)
                .views(views)
                .build();

        return eventMapper.toEventFullDto(params);
    }

    private Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        return eventsRepository.getConfirmedRequestsForEvents(eventIds).stream()
                .collect(Collectors.toMap(List::getFirst, List::getLast));
    }

}