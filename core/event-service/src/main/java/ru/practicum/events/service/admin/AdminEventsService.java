package ru.practicum.events.service.admin;


import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.events.dto.parameters.SearchEventsParameters;
import ru.practicum.interaction.events.dto.requests.UpdateEventAdminRequest;

import java.util.List;

public interface AdminEventsService {

    List<EventFullDto> searchEvents(SearchEventsParameters searchParams);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest);
}
