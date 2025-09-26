package ru.practicum.request.service.external.event;

import ru.practicum.interaction.events.dto.EventFullDto;

public interface EventService {

    EventFullDto getEventFullDto(Long eventId);
}
