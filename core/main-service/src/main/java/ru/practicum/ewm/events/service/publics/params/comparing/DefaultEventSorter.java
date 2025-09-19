package ru.practicum.ewm.events.service.publics.params.comparing;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.comments.storage.CommentRepository;
import ru.practicum.ewm.events.enums.SortingEvents;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.views.EventsViewsGetter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class DefaultEventSorter implements EventSorter {

    private final EventsViewsGetter eventsViewsGetter;
    private final CommentRepository commentRepository;

    @Override
    public Comparator<Event> getComparator(SortingEvents sort, List<Long> eventIds) {
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

    private Map<Long, Long> getCommentsNumberMap(List<Long> eventIds) {
        Map<Long, Long> rawMap = commentRepository.getCommentsNumberForEvents(eventIds).stream()
                .collect(Collectors.toMap(List::getFirst, List::getLast));

        return eventIds.stream()
                .collect(Collectors.toMap(Function.identity(), id -> rawMap.getOrDefault(id, 0L)));
    }
}