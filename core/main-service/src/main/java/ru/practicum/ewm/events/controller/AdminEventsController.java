package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.events.dto.parameters.SearchEventsParameters;
import ru.practicum.ewm.events.service.admin.AdminEventsService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.events.constants.EventsApiPath.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminEventsController {

    private final AdminEventsService adminEventsService;

    @GetMapping(ADMIN_API_PREFIX)
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,

            @DateTimeFormat(pattern = DATA_TIME_FORMAT)
            @RequestParam(required = false) LocalDateTime rangeStart,

            @DateTimeFormat(pattern = DATA_TIME_FORMAT)
            @RequestParam(required = false) LocalDateTime rangeEnd,

            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        SearchEventsParameters searchEventsParameters = SearchEventsParameters.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();

        log.info("Request: search events. Query={}", searchEventsParameters);
        return adminEventsService.searchEvents(searchEventsParameters);
    }

    @PatchMapping(ADMIN_API_PREFIX + EVENT_ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@PathVariable(EVENT_ID) Long eventId,
                                           @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {

        log.info("Request: update event id={} by admin, data={}", eventId, updateEventAdminRequest);

        return adminEventsService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }
}