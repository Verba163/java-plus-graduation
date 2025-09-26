package ru.practicum.events.service.publics;

import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.QEvent;
import ru.practicum.events.service.assemblers.EventDtoAssemblers;
import ru.practicum.events.service.external.request.RequestService;
import ru.practicum.events.service.publics.params.comparing.EventSorter;
import ru.practicum.events.service.publics.params.filter.EventFilter;
import ru.practicum.events.service.stats.EventStatsService;
import ru.practicum.events.service.validation.EventValidator;
import ru.practicum.events.storage.EventsRepository;
import ru.practicum.events.views.EventsViewsGetter;
import ru.practicum.interaction.comments.dto.CommentShortDto;
import ru.practicum.interaction.error.exception.NotFoundException;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.events.dto.EventFullDtoWithComments;
import ru.practicum.interaction.events.dto.parameters.GetAllCommentsParameters;
import ru.practicum.interaction.events.dto.parameters.SearchPublicEventsParameters;
import ru.practicum.interaction.events.enums.EventPublishState;
import ru.practicum.interaction.feign.clients.CommentsFeignClient;

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
    private final EventsViewsGetter eventsViewsGetter;
    private final EventSorter eventSorter;
    private final EventValidator eventValidator;
    private final RequestService requestService;
    private final CommentsFeignClient commentsFeignClient;
    private final EventDtoAssemblers eventDtoAssemblers;

    @Transactional
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
        Map<Long, Long> confirmedRequestsMap = requestService.getConfirmedRequestsMap(eventIds);

        eventStatsService.recordHit(request);

        Comparator<Event> comparator = eventSorter.getComparator(searchParams.getSort(), eventIds);

        List<Event> sortedEvents = filteredEvents.stream()
                .sorted(comparator)
                .toList();

        return sortedEvents.stream()
                .map(events -> eventDtoAssemblers.createEventFullDto(events,
                        eventsViewsMap.getOrDefault(events.getId(), 0L),
                        confirmedRequestsMap.getOrDefault(events.getId(), 0L)))
                .toList();
    }

    @Override
    @Transactional
    public List<CommentShortDto> getAllEventComments(GetAllCommentsParameters parameters) {

        eventValidator.getEventWithCheck(parameters.getEventId());

        return commentsFeignClient.getCommentsForEvent(parameters.getEventId(), parameters.getFrom(), parameters.getSize());
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

        eventStatsService.recordHit(request);
        return eventDtoAssemblers.createEventFullDtoWithComments(foundEvent);
    }
}