package ru.practicum.compilation.service.external;

import ru.practicum.interaction.events.dto.EventShortDto;

import java.util.List;

public interface EventService {

    boolean checkEventsExist(List<Long> eventIds);

    List<EventShortDto> getEventShortDtoList(List<Long> eventIds);
}