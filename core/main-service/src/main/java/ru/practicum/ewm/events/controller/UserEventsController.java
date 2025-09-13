package ru.practicum.ewm.events.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.dto.parameters.EventsForUserParameters;
import ru.practicum.ewm.events.dto.parameters.UpdateEventParameters;
import ru.practicum.ewm.events.dto.parameters.UpdateRequestsStatusParameters;
import ru.practicum.ewm.events.service.users.UserEventsService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

import static ru.practicum.ewm.events.constants.EventsApiPath.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserEventsController {

    private final UserEventsService userEventsService;


    @GetMapping(PRIVATE_API_PREFIX + PUBLIC_API_PREFIX_USER_ID)
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsCreatedByUser(@PathVariable(USER_ID) Long userId,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        log.info("Request: get events for user id={}, from={}, size={}", userId, from, size);
        EventsForUserParameters eventsForUserRequestParams = EventsForUserParameters.builder()
                .userId(userId)
                .from(from)
                .size(size)
                .build();
        return userEventsService.getEventsCreatedByUser(eventsForUserRequestParams);
    }

    @PostMapping(PRIVATE_API_PREFIX + PUBLIC_API_PREFIX_USER_ID)
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable(USER_ID) Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Request: create new event from user id={}, newEventDto={}", userId, newEventDto);
        return userEventsService.createEvent(userId, newEventDto);
    }

    @GetMapping(PRIVATE_API_PREFIX + PRIVATE_API_PREFIX_USER_ID_EVENT_ID)
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable(USER_ID) Long userId,
                                     @PathVariable(EVENT_ID) Long eventId) {
        log.info("Request: get event id={} for user id={}", eventId, userId);
        return userEventsService.getEventById(userId, eventId);
    }

    @PatchMapping(PRIVATE_API_PREFIX + PRIVATE_API_PREFIX_USER_ID_EVENT_ID)
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable(USER_ID) Long userId,
                                    @PathVariable(EVENT_ID) Long eventId,
                                    @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Request: update event id={} by user id={}, data={}", eventId, userId, updateEventUserRequest);
        UpdateEventParameters updateEventParameters = UpdateEventParameters.builder()
                .userId(userId)
                .eventId(eventId)
                .updateEventUserRequest(updateEventUserRequest)
                .build();

        return userEventsService.updateEvent(updateEventParameters);
    }

    @GetMapping(PRIVATE_API_PREFIX + PRIVATE_API_USER_EVENT_REQUESTS)
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsForEvent(@PathVariable(USER_ID) Long userId,
                                                             @PathVariable(EVENT_ID) Long eventId) {
        log.info("Request: get requests for event id={} for user id={}", eventId, userId);
        return userEventsService.getRequestsForEvent(userId, eventId);
    }

    @PatchMapping(PRIVATE_API_PREFIX + PRIVATE_API_USER_EVENT_REQUESTS)
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestsForEvent(@PathVariable(USER_ID) Long userId,
                                                                 @PathVariable(EVENT_ID) Long eventId,
                                                                 @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("Request: update requests for event id={} for user id={}, data={}", eventId, userId, updateRequest);
        UpdateRequestsStatusParameters updateRequestsStatusParameters
                = UpdateRequestsStatusParameters.builder()
                .userId(userId)
                .eventId(eventId)
                .eventRequestStatusUpdateRequest(updateRequest)
                .build();
        return userEventsService.updateRequestsForEvent(updateRequestsStatusParameters);
    }
}