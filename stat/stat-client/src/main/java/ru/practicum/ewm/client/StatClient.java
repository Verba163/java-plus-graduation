package ru.practicum.ewm.client;

import ru.practicum.dto.StatHitDto;
import ru.practicum.dto.StatViewDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatClient {

    void hit(StatHitDto statHitDto);

    List<StatViewDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
