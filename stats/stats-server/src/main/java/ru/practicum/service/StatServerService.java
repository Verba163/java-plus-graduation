package ru.practicum.service;

import ru.practicum.dto.StatHitDto;
import ru.practicum.dto.StatViewDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatServerService {
    void hit(StatHitDto statHitDto);

    List<StatViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
