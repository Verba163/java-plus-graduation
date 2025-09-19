package ru.practicum.ewm.events.service.publics;

import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentShortDto;
import ru.practicum.ewm.comments.mapper.CommentMapper;
import ru.practicum.ewm.comments.storage.CommentRepository;
import ru.practicum.ewm.error.exception.ConflictException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventFullDtoWithComments;
import ru.practicum.ewm.events.dto.parameters.GetAllCommentsParameters;
import ru.practicum.ewm.events.dto.parameters.SearchPublicEventsParameters;
import ru.practicum.ewm.events.enums.EventPublishState;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.model.QEvent;
import ru.practicum.ewm.events.service.params.assemblers.publics.PublicEventDtoAssembler;
import ru.practicum.ewm.events.service.publics.params.comparing.EventSorter;
import ru.practicum.ewm.events.service.publics.params.filter.EventFilter;
import ru.practicum.ewm.events.service.params.stats.EventStatsService;
import ru.practicum.ewm.events.storage.EventsRepository;
import ru.practicum.ewm.events.views.EventsViewsGetter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PublicEventsServiceImpl implements PublicEventsService {

    private final EventsRepository eventsRepository;
    private final EventFilter eventFilter;
    private final EventStatsService eventStatsService;
    private final CommentRepository commentRepository;
    private final PublicEventDtoAssembler publicEventDtoAssembler;
    private final EventsViewsGetter eventsViewsGetter;
    private final EventSorter eventSorter;

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> searchPublicEvents(SearchPublicEventsParameters searchParams, HttpServletRequest request) {
        QEvent event = QEvent.event;
        BooleanExpression filterCondition = eventFilter.getCondition(event, searchParams);

        int pageNum = searchParams.getFrom() / searchParams.getSize();
        int pageSize = searchParams.getSize();

        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Event> filteredEventsPage = eventsRepository.findAll(filterCondition, pageable);

        List<Event> filteredEvents = filteredEventsPage.getContent();

        List<Long> eventIds = filteredEvents.stream().map(Event::getId).toList();

        Map<Long, Long> eventsViewsMap = eventsViewsGetter.getEventsViewsMap(eventIds);
        Map<Long, Long> confirmedRequestsMap = eventStatsService.getConfirmedRequests(eventIds);

        eventStatsService.recordHit(request);

        Comparator<Event> comparator = eventSorter.getComparator(searchParams.getSort(), eventIds);

        List<Event> sortedEvents = filteredEvents.stream()
                .sorted(comparator)
                .toList();

        return sortedEvents.stream()
                .map(events -> publicEventDtoAssembler.createEventFullDto(events,
                        eventsViewsMap.getOrDefault(events.getId(), 0L),
                        confirmedRequestsMap.getOrDefault(events.getId(), 0L)))
                .toList();
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

    @Transactional(readOnly = true)
    @Override
    public EventFullDtoWithComments getPublicEventById(Long eventId, HttpServletRequest request) {
        QEvent event = QEvent.event;

        Event foundEvent = eventsRepository.findOne(
                        event.id.eq(eventId)
                                .and(event.eventPublishState.eq(EventPublishState.PUBLISHED)))
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event id=%d not found or is not published.", eventId)));

        eventStatsService.recordHit(request);

        return publicEventDtoAssembler.createEventFullDtoWithComments(foundEvent);
    }

    private Event getEventWithCheck(long eventId) {
        return eventsRepository.findById(eventId)
                .orElseThrow(() -> new ConflictException(String.format(
                        "Event id=%d not found.", eventId)
                ));
    }
}