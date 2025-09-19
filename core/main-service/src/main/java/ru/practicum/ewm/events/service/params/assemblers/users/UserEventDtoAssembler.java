package ru.practicum.ewm.events.service.params.assemblers.users;

import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.model.Event;

import java.util.List;

public interface UserEventDtoAssembler {

    EventFullDto mapToEventFullDto(Event event);

    List<EventShortDto> mapToEventShortDtoList(List<Event> events);
}
