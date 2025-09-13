package ru.practicum.ewm.events.service.users;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.parameters.EventsForUserParameters;
import ru.practicum.ewm.events.dto.parameters.UpdateEventParameters;
import ru.practicum.ewm.events.dto.parameters.UpdateRequestsStatusParameters;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface UserEventsService {
    
    
    List<EventShortDto> getEventsCreatedByUser(EventsForUserParameters eventsForUserParameters);

    EventFullDto updateEvent(UpdateEventParameters updateEventParameters);

    EventRequestStatusUpdateResult updateRequestsForEvent(UpdateRequestsStatusParameters updateParams);

    
    List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    
    EventFullDto getEventById(Long userId, Long eventId);
}
