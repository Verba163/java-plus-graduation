package ru.practicum.ewm.events.views;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.dto.StatViewDto;
import ru.practicum.ewm.client.StatFeignClient;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventsViewsGetter {

    private final StatFeignClient statFeignClient;

    public Long getEventViews(Long eventId) {
        String uri = createURIForEventId(eventId);
        String start = "1900-01-01T00:00:00";
        String end = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();

        try {
            List<StatViewDto> stats = statFeignClient.getStat(start, end, List.of(uri), true);
            return (stats != null && !stats.isEmpty()) ? stats.get(0).getHits() : 0L;
        } catch (Exception e) {
            log.error("Error while retrieving stats for event ID {}: {}", eventId, e.getMessage());
            return 0L;
        }
    }

    public Map<Long, Long> getEventsViewsMap(List<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Map.of();
        }

        List<String> uris = eventIds.stream()
                .map(this::createURIForEventId)
                .collect(Collectors.toList());
        String start = "1900-01-01T00:00:00";
        String end = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString();

        try {
            List<StatViewDto> stats = statFeignClient.getStat(start, end, uris, true);
            Map<String, Long> uriToHits = stats.stream()
                    .collect(Collectors.toMap(StatViewDto::getUri, StatViewDto::getHits));

            return eventIds.stream()
                    .collect(Collectors.toMap(
                            id -> id,
                            id -> uriToHits.getOrDefault(createURIForEventId(id), 0L)
                    ));
        } catch (Exception e) {
            log.error("Error while retrieving stats for events {}: {}", eventIds, e.getMessage());
            return eventIds.stream().collect(Collectors.toMap(id -> id, id -> 0L));
        }
    }

    private String createURIForEventId(Long eventId) {
        return "/events/" + eventId;
    }
}