package ru.practicum.ewm.events.service.params.assemblers.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.parameters.MappingEventParameters;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.service.params.stats.EventStatsService;
import ru.practicum.ewm.events.views.EventsViewsGetter;
import ru.practicum.ewm.user.mapper.UserMapper;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class DefaultUserEventDtoAssembler implements UserEventDtoAssembler {

    private final EventMapper eventMapper;
    private final EventsViewsGetter eventsViewsGetter;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final EventStatsService eventStatsService;

    @Override
    public EventFullDto mapToEventFullDto(Event event) {
        long id = event.getId();
        Map<Long, Long> confirmedReqs = eventStatsService.getConfirmedRequests(List.of(id));
        Map<Long, Long> viewsMap = eventsViewsGetter.getEventsViewsMap(List.of(id));

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

    public List<EventShortDto> mapToEventShortDtoList(List<Event> events) {
        List<Long> ids = events.stream().map(Event::getId).toList();
        Map<Long, Long> confirmedReqs = eventStatsService.getConfirmedRequests(ids);
        Map<Long, Long> viewsMap = eventsViewsGetter.getEventsViewsMap(ids);

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
