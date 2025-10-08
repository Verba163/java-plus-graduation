package ru.practicum.events.service.publics.params.comparing;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.events.model.Event;
import ru.practicum.events.rating.EventRatingService;
import ru.practicum.interaction.events.enums.SortingEvents;
import ru.practicum.interaction.feign.clients.CommentsFeignClient;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class DefaultEventSorter implements EventSorter {

    private final EventRatingService eventRatingService;
    private final CommentsFeignClient commentsFeignClient;

    @Override
    public Comparator<Event> getComparator(SortingEvents sort, List<Long> eventIds) {
        if (sort == null) {
            return Comparator.comparing(Event::getEventDate);
        }
        return switch (sort) {
            case RATING -> {
                Map<Long, Double> ratingMap = eventRatingService.getRatingMap(eventIds);
                yield Comparator.comparingDouble((Event e) -> ratingMap.getOrDefault(e.getId(), 0.0)).reversed();
            }
            case COMMENTS -> {
                Map<Long, Long> commentsMap = getCommentsNumberMap(eventIds);
                yield Comparator.comparingLong((Event e) -> commentsMap.getOrDefault(e.getId(), 0L)).reversed();
            }
            default -> Comparator.comparing(Event::getEventDate);
        };
    }

    private Map<Long, Long> getCommentsNumberMap(List<Long> eventIds) {
        Map<Long, Long> commentsNumberMap = commentsFeignClient.getCommentsNumberForEvents(eventIds).stream()
                .collect(Collectors.toMap(List::getFirst, List::getLast));

        return eventIds.stream()
                .collect(Collectors.toMap(Function.identity(), id -> commentsNumberMap.getOrDefault(id, 0L)));
    }
}