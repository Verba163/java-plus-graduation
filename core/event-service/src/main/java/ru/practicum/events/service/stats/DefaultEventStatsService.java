package ru.practicum.events.service.stats;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.dto.StatHitDto;
import ru.practicum.ewm.client.StatFeignClient;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DefaultEventStatsService implements EventStatsService {

    private final StatFeignClient statFeignClient;

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
}
