package ru.practicum.events.service.publics;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.QEvent;
import ru.practicum.events.rating.EventRatingService;
import ru.practicum.events.service.assemblers.EventDtoAssemblers;
import ru.practicum.events.service.external.request.RequestService;
import ru.practicum.events.service.publics.params.comparing.EventSorter;
import ru.practicum.events.service.publics.params.filter.EventFilter;
import ru.practicum.events.service.validation.EventValidator;
import ru.practicum.events.storage.EventsRepository;
import ru.practicum.ewm.client.RecommendationsClient;
import ru.practicum.ewm.client.UserActionClient;
import ru.practicum.grpc.stats.action.ActionTypeProto;
import ru.practicum.grpc.stats.recommendation.RecommendedEventProto;
import ru.practicum.interaction.comments.dto.CommentShortDto;
import ru.practicum.interaction.error.exception.NotFoundException;
import ru.practicum.interaction.error.exception.ValidationException;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.events.dto.EventFullDtoWithComments;
import ru.practicum.interaction.events.dto.EventShortDto;
import ru.practicum.interaction.events.dto.parameters.GetAllCommentsParameters;
import ru.practicum.interaction.events.dto.parameters.SearchPublicEventsParameters;
import ru.practicum.interaction.events.enums.EventPublishState;
import ru.practicum.interaction.feign.clients.CommentsFeignClient;
import ru.practicum.interaction.request.dto.ParticipationRequestDto;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class PublicEventsServiceImpl implements PublicEventsService {

    private final EventsRepository eventsRepository;
    private final EventFilter eventFilter;
    private final EventSorter eventSorter;
    private final EventValidator eventValidator;
    private final RequestService requestService;
    private final CommentsFeignClient commentsFeignClient;
    private final EventDtoAssemblers eventDtoAssemblers;
    private final EventRatingService eventRatingService;
    private final RecommendationsClient recommendationsClient;
    private final UserActionClient userActionClient;

    @Transactional
    @Override
    public List<EventFullDto> searchPublicEvents(SearchPublicEventsParameters searchParams) {
        QEvent event = QEvent.event;
        BooleanExpression filterCondition = eventFilter.getCondition(event, searchParams);

        int pageNum = searchParams.getFrom() / searchParams.getSize();
        int pageSize = searchParams.getSize();

        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Event> filteredEventsPage = eventsRepository.findAll(filterCondition, pageable);

        List<Event> filteredEvents = filteredEventsPage.getContent();

        List<Long> eventIds = filteredEvents.stream().map(Event::getId).toList();

        Map<Long, Double> eventsRatingMap = eventRatingService.getRatingMap(eventIds);
        Map<Long, Long> confirmedRequestsMap = requestService.getConfirmedRequestsMap(eventIds);


        Comparator<Event> comparator = eventSorter.getComparator(searchParams.getSort(), eventIds);

        List<Event> sortedEvents = filteredEvents.stream()
                .sorted(comparator)
                .toList();

        return sortedEvents.stream()
                .map(events -> eventDtoAssemblers.createEventFullDto(events,
                        eventsRatingMap.getOrDefault(events.getId(), 0.0),
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
    public EventFullDtoWithComments getPublicEventById(Long eventId) {
        QEvent event = QEvent.event;

        Event foundEvent = eventsRepository.findOne(
                        event.id.eq(eventId)
                                .and(event.eventPublishState.eq(EventPublishState.PUBLISHED)))
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event id=%d not found or is not published.", eventId)));

        return eventDtoAssemblers.createEventFullDtoWithComments(foundEvent);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getRecommendation(Long userId, int maxResult) {
        Stream<RecommendedEventProto> recommendedEvent = recommendationsClient.getRecommendationsForUser(userId, maxResult);
        List<Long> eventIds = recommendedEvent.map(RecommendedEventProto::getEventId).toList();

        List<Event> events = eventsRepository.findAllById(eventIds);

        return eventDtoAssemblers.createEventShortDtoList(events);
    }

    @Override
    public void addLikeToEvent(Long eventId, Long userId) {

        ParticipationRequestDto requestDto = requestService.getUsersRequest(userId, eventId);

        if (!requestDto.getStatus().equals("CONFIRMED")) {
            throw new ValidationException("You cannot like an event without confirmed participation");
        }

        Event event = eventValidator.getEventWithCheck(eventId);
        eventValidator.checkEventDateIsValid(event.getEventDate());
        userActionClient.collectUserAction(userId, eventId, ActionTypeProto.ACTION_LIKE, Instant.now());
    }

}