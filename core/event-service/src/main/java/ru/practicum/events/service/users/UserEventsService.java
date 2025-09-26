package ru.practicum.events.service.users;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.events.dto.EventShortDto;
import ru.practicum.interaction.events.dto.NewEventDto;
import ru.practicum.interaction.events.dto.parameters.EventsForUserParameters;
import ru.practicum.interaction.events.dto.parameters.UpdateEventParameters;
import ru.practicum.interaction.events.dto.parameters.UpdateRequestsStatusParameters;
import ru.practicum.interaction.events.dto.requests.EventRequestStatusUpdateResult;
import ru.practicum.interaction.request.dto.ParticipationRequestDto;

import java.util.List;

public interface UserEventsService {


    List<EventShortDto> getEventsCreatedByUser(EventsForUserParameters eventsForUserParameters, HttpServletRequest request);

    EventFullDto updateEvent(UpdateEventParameters updateEventParameters);

    EventRequestStatusUpdateResult updateRequestsForEvent(UpdateRequestsStatusParameters updateParams);

    List<ParticipationRequestDto> getRequestsForEvent(Long userId, Long eventId);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventById(Long userId, Long eventId, HttpServletRequest request);
}
