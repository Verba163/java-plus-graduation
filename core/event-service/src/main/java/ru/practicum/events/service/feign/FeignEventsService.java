package ru.practicum.events.service.feign;

import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.events.dto.EventShortDto;

import java.util.List;


public interface FeignEventsService {

    Long getEventsCountByCategoryId(Long catId);

    EventFullDto getEventById(Long eventId);

    List<EventShortDto> getEventsShortDtoByIds(List<Long> eventIds);

    Boolean checkEventExistingByIds(List<Long> eventIds);
}