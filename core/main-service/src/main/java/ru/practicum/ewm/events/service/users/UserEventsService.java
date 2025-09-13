package ru.practicum.ewm.events.service.users;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.requests.EventRequestStatusUpdateResult;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.parameters.EventsForUserParameters;
import ru.practicum.ewm.events.dto.parameters.UpdateEventParameters;
import ru.practicum.ewm.events.dto.parameters.UpdateRequestsStatusParameters;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface UserEventsService {
    
    
    List<EventShortDto> getEventsCreatedByUser(EventsForUserParameters eventsForUserParameters, HttpServletRequest request);

    EventFullDto updateEvent(UpdateEventParameters updateEventParameters);

    EventRequestStatusUpdateResult updateRequestsForEvent(UpdateRequestsStatusParameters updateParams);

    
    List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    
    EventFullDto getEventById(Long userId, Long eventId, HttpServletRequest request);
}
