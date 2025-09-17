package ru.practicum.ewm.events.service.params.assemblers.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.comments.dto.CommentShortDto;
import ru.practicum.ewm.comments.mapper.CommentMapper;
import ru.practicum.ewm.comments.storage.CommentRepository;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventFullDtoWithComments;
import ru.practicum.ewm.events.dto.parameters.MappingEventParameters;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.service.params.stats.EventStatsService;
import ru.practicum.ewm.events.views.EventsViewsGetter;
import ru.practicum.ewm.user.mapper.UserMapper;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DefaultPublicEventDtoAssembler implements PublicEventDtoAssembler {

    private final EventMapper eventMapper;
    private final EventsViewsGetter eventsViewsGetter;
    private final CommentRepository commentRepository;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final EventStatsService eventStatsService;

    public EventFullDtoWithComments createEventFullDtoWithComments(Event event) {
        Long eventId = event.getId();

        Long views = eventsViewsGetter.getEventViews(eventId);
        Map<Long, Long> confirmedRequestsMap = eventStatsService.getConfirmedRequests(List.of(eventId));

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

    public EventFullDto createEventFullDto(Event event, long views, long confirmedRequests) {
        MappingEventParameters params = MappingEventParameters.builder()
                .event(event)
                .categoryDto(categoryMapper.toCategoryDto(event.getCategory()))
                .initiator(userMapper.toUserShortDto(event.getInitiator()))
                .confirmedRequests(confirmedRequests)
                .views(views)
                .build();

        return eventMapper.toEventFullDto(params);
    }

}
