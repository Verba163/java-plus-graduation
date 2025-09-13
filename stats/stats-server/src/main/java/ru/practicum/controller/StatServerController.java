package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatHitDto;
import ru.practicum.dto.StatViewDto;
import ru.practicum.error.exception.StatsIllegalDateTime;
import ru.practicum.service.StatServerService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatServerController {
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final StatServerService statServerService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@Valid @RequestBody StatHitDto statHitDto) {
        log.info("Received request to register hit: app='{}', uri='{}', ip='{}', timestamp='{}'",
                statHitDto.getApp(), statHitDto.getUri(), statHitDto.getIp(), statHitDto.getTimestamp());
        statServerService.hit(statHitDto);
        log.info("Hit registered successfully for URI: {}", statHitDto.getUri());
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatViewDto> getStats(
            @RequestParam @DateTimeFormat(pattern = DATETIME_PATTERN) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATETIME_PATTERN) LocalDateTime end,
            @RequestParam(required = false, defaultValue = "") List<String> uris,
            @RequestParam(required = false, defaultValue = "false") boolean unique
    ) {
        log.info("Received request to get stats: start='{}', end='{}', unique='{}', uris='{}'",
                start, end, unique, uris);

        validateDateTimeParams(start, end);

        List<StatViewDto> stats = statServerService.getStats(start, end, uris, unique);
        log.info("Stats retrieved successfully: {} records found", stats.size());
        return stats;
    }

    private void validateDateTimeParams(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            log.warn("Validation failed: 'start' or 'end' is null");
            throw new StatsIllegalDateTime("Request parameters 'start' and 'end' cannot be null.");
        }
        if (end.isBefore(start)) {
            log.warn("Validation failed: 'end' ({}) is before 'start' ({})", end, start);
            throw new StatsIllegalDateTime("Request parameter 'end' must be after 'start'.");
        }
    }
}