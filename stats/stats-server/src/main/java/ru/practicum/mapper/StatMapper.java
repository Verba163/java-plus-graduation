package ru.practicum.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.StatHitDto;
import ru.practicum.dto.StatViewDto;
import ru.practicum.model.Stat;

@Component
public class StatMapper {

    public StatHitDto toStatHitDto(Stat stat) {

        return StatHitDto.builder()
                .id(stat.getId())
                .app(stat.getApp())
                .uri(stat.getUri())
                .ip(stat.getIp())
                .timestamp(stat.getTimestamp())
                .build();
    }

    public Stat toStatEntity(StatHitDto statHitDto) {

        return Stat.builder()
                .id(statHitDto.getId())
                .app(statHitDto.getApp())
                .uri(statHitDto.getUri())
                .ip(statHitDto.getIp())
                .timestamp(statHitDto.getTimestamp())
                .build();
    }

    public StatViewDto toStatViewDto(Stat stat) {

        return StatViewDto.builder()
                .app(stat.getApp())
                .uri(stat.getUri())
                .build();
    }
}