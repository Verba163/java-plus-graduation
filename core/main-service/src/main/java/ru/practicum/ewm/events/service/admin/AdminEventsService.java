package ru.practicum.ewm.events.service.admin;

import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.events.dto.parameters.SearchEventsParameters;

import java.util.List;

public interface AdminEventsService {

    List<EventFullDto> searchEvents(SearchEventsParameters searchParams);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest);
}
