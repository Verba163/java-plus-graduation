package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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
        log.info(String.format("Request to make hit: %s", statHitDto));
        statServerService.hit(statHitDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatViewDto> getStats(
            @RequestParam @DateTimeFormat(pattern = DATETIME_PATTERN) LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATETIME_PATTERN) LocalDateTime end,
            @RequestParam(required = false, defaultValue = "") List<String> uris,
            @RequestParam(required = false, defaultValue = "false") boolean unique
            ) {
        log.info("Request to get stats: from '{}' to '{}'. Unique is '{}', uris: '{}'", start, end, unique, uris);

        if (start == null || end == null) {
            throw new StatsIllegalDateTime("Params 'end' and 'start' can not be NULL.");
        }

        if (end.isBefore(start)) {
            throw new StatsIllegalDateTime("Request param 'end' must be after 'start'.");
        }

        return statServerService.getStats(start, end, uris, unique);
    }
}
