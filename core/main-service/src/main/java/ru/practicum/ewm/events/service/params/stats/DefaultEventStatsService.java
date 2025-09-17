package ru.practicum.ewm.events.service.params.stats;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.StatHitDto;
import ru.practicum.ewm.client.StatFeignClient;
import ru.practicum.ewm.events.storage.EventsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DefaultEventStatsService implements EventStatsService {

    private final StatFeignClient statFeignClient;
    private final EventsRepository eventsRepository;

    @Override
    public void recordHit(HttpServletRequest request) {

        StatHitDto statHitDto = StatHitDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        statFeignClient.hit(statHitDto);
    }

    @Override
    public Map<Long, Long> getConfirmedRequests(List<Long> eventIds) {
        return eventsRepository.getConfirmedRequestsForEvents(eventIds).stream()
                .collect(Collectors.toMap(List::getFirst, List::getLast));
    }
}
