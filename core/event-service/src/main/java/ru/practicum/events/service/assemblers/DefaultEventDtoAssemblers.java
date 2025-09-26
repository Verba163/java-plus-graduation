package ru.practicum.events.service.assemblers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.params.MappingEventParameters;
import ru.practicum.events.service.external.category.CategoryService;
import ru.practicum.events.service.external.request.RequestService;
import ru.practicum.events.service.external.user.UserService;
import ru.practicum.events.views.EventsViewsGetter;
import ru.practicum.interaction.comments.dto.CommentShortDto;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.events.dto.EventFullDtoWithComments;
import ru.practicum.interaction.events.dto.EventShortDto;
import ru.practicum.interaction.feign.clients.CommentsFeignClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultEventDtoAssemblers implements EventDtoAssemblers {

    private final EventMapper eventMapper;
    private final EventsViewsGetter eventsViewsGetter;
    private final CategoryService categoryService;
    private final UserService userService;
    private final RequestService requestService;
    private final CommentsFeignClient commentsFeignClient;

    @Override
    public EventFullDto createEventFullDto(Event event, Long views, Long requests) {
        long id = event.getId();
        Map<Long, Long> eventsViewsMap = eventsViewsGetter.getEventsViewsMap(List.of(id));
        Map<Long, Long> confirmedRequestsMap = requestService.getConfirmedRequestsMap(List.of(id));

        MappingEventParameters eventFullDtoParams = EventMapper.createMappingEventParameter(event,
                categoryService.getCategoryWithCheck(event.getCategoryId()),
                userService.getUserShorDto(event.getInitiatorId()),
                eventsViewsMap.getOrDefault(id, 0L),
                confirmedRequestsMap.getOrDefault(id, 0L));
        return eventMapper.toEventFullDto(eventFullDtoParams);
    }

    @Override
    public EventFullDto createEventFullDto(Event event) {
        long id = event.getId();
        Map<Long, Long> eventsViewsMap = eventsViewsGetter.getEventsViewsMap(List.of(id));
        Map<Long, Long> confirmedRequestsMap = requestService.getConfirmedRequestsMap(List.of(id));

        MappingEventParameters eventFullDtoParams = EventMapper.createMappingEventParameter(event,
                categoryService.getCategoryWithCheck(event.getCategoryId()),
                userService.getUserShorDto(event.getInitiatorId()),
                eventsViewsMap.getOrDefault(id, 0L),
                confirmedRequestsMap.getOrDefault(id, 0L));
        return eventMapper.toEventFullDto(eventFullDtoParams);
    }

    @Override
    public EventFullDtoWithComments createEventFullDtoWithComments(Event event) {
        long id = event.getId();
        Map<Long, Long> eventsViewsMap = eventsViewsGetter.getEventsViewsMap(List.of(id));
        Map<Long, Long> confirmedRequestsMap = requestService.getConfirmedRequestsMap(List.of(id));
        List<CommentShortDto> comments;

        try {
            comments = commentsFeignClient.findFirstCommentsForEvent(id, 5);
        } catch (Exception e) {
            log.warn("Failed to fetch comments for event ID {}: {}", id, e.getMessage(), e);
            comments = Collections.emptyList();
        }


        MappingEventParameters eventFullDtoParams = eventMapper.createMappingEventParameterWithComments(event,
                categoryService.getCategoryWithCheck(event.getCategoryId()),
                userService.getUserShorDto(event.getInitiatorId()),
                eventsViewsMap.getOrDefault(id, 0L),
                confirmedRequestsMap.getOrDefault(id, 0L),
                comments);
        return eventMapper.toEventFullDtoWithComments(eventFullDtoParams);
    }

    @Override
    public List<EventFullDto> createEventFullDtoList(List<Event> events) {
        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();
        Map<Long, Long> eventsViewsMap = eventsViewsGetter.getEventsViewsMap(ids);
        Map<Long, Long> confirmedRequestsMap = requestService.getConfirmedRequestsMap(ids);

        return events.stream()
                .map(event -> {
                    MappingEventParameters eventFullDtoParams = EventMapper.createMappingEventParameter(event,
                            categoryService.getCategoryWithCheck(event.getCategoryId()),
                            userService.getUserShorDto(event.getInitiatorId()),
                            eventsViewsMap.getOrDefault(event.getId(), 0L),
                            confirmedRequestsMap.getOrDefault(event.getId(), 0L));
                    return eventMapper.toEventFullDto(eventFullDtoParams);
                })
                .toList();
    }

    @Override
    public List<EventShortDto> createEventShortDtoList(List<Event> events) {
        List<Long> ids = events.stream()
                .map(Event::getId)
                .toList();
        Map<Long, Long> eventsViewsMap = eventsViewsGetter.getEventsViewsMap(ids);
        Map<Long, Long> confirmedRequestsMap = requestService.getConfirmedRequestsMap(ids);

        return events.stream()
                .map(event -> {
                    MappingEventParameters mappingEventParameters = EventMapper.createMappingEventParameter(event,
                            categoryService.getCategoryWithCheck(event.getCategoryId()),
                            userService.getUserShorDto(event.getInitiatorId()),
                            eventsViewsMap.getOrDefault(event.getId(), 0L),
                            confirmedRequestsMap.getOrDefault(event.getId(), 0L));
                    return eventMapper.toEventShortDto(mappingEventParameters);
                })
                .toList();
    }
}
