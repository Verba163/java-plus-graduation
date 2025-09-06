package ru.practicum.ewm.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.StatHitDto;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.comments.dto.CommentShortDto;
import ru.practicum.ewm.events.constants.EventsConstants;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventFullDtoWithComments;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.events.dto.UpdateEventUserRequest;
import ru.practicum.ewm.events.dto.parameters.EventsForUserParameters;
import ru.practicum.ewm.events.dto.parameters.GetAllCommentsParameters;
import ru.practicum.ewm.events.dto.parameters.SearchEventsParameters;
import ru.practicum.ewm.events.dto.parameters.SearchPublicEventsParameters;
import ru.practicum.ewm.events.dto.parameters.UpdateEventParameters;
import ru.practicum.ewm.events.dto.parameters.UpdateRequestsStatusParameters;
import ru.practicum.ewm.events.enums.SortingEvents;
import ru.practicum.ewm.events.service.EventsService;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.util.Util;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.events.constants.EventsConstants.EVENT_ID;
import static ru.practicum.ewm.events.constants.EventsConstants.EVENT_ID_PATH;
import static ru.practicum.ewm.events.constants.EventsConstants.PRIVATE_API_PREFIX_USER_ID_EVENT_ID;
import static ru.practicum.ewm.events.constants.EventsConstants.PRIVATE_API_USER_EVENT_REQUESTS;
import static ru.practicum.ewm.events.constants.EventsConstants.PUBLIC_API_PREFIX_COMMENTS;
import static ru.practicum.ewm.events.constants.EventsConstants.PUBLIC_API_PREFIX_USER_ID;
import static ru.practicum.ewm.events.constants.EventsConstants.USER_ID;

@RestController
@Slf4j
public class EventsController {
    private final EventsService eventsService;
    private final StatClient statClient;
    private final String applicationName;

    @Autowired
    public EventsController(EventsService eventsService,
                            StatClient statClient,
                            @Value("${application.name}")
                            String applicationName) {
        this.eventsService = eventsService;
        this.statClient = statClient;
        this.applicationName = applicationName;
    }

    // region PRIVATE
    @GetMapping(EventsConstants.PRIVATE_API_PREFIX + PUBLIC_API_PREFIX_USER_ID)
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
        return eventsService.getEventsCreatedByUser(eventsForUserRequestParams);
    }

    @PostMapping(EventsConstants.PRIVATE_API_PREFIX + PUBLIC_API_PREFIX_USER_ID)
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable(USER_ID) Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Request: create new event from user id={}, newEventDto={}", userId, newEventDto);
        return eventsService.createEvent(userId, newEventDto);
    }

    @GetMapping(EventsConstants.PRIVATE_API_PREFIX + PRIVATE_API_PREFIX_USER_ID_EVENT_ID)
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable(USER_ID) Long userId,
                                     @PathVariable(EVENT_ID) Long eventId) {
        log.info("Request: get event id={} for user id={}", eventId, userId);
        return eventsService.getEventById(userId, eventId);
    }

    @PatchMapping(EventsConstants.PRIVATE_API_PREFIX + PRIVATE_API_PREFIX_USER_ID_EVENT_ID)
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

        return eventsService.updateEvent(updateEventParameters);
    }

    @GetMapping(EventsConstants.PRIVATE_API_PREFIX + PRIVATE_API_USER_EVENT_REQUESTS)
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsForEvent(@PathVariable(USER_ID) Long userId,
                                                             @PathVariable(EVENT_ID) Long eventId) {
        log.info("Request: get requests for event id={} for user id={}", eventId, userId);
        return eventsService.getRequestsForEvent(userId, eventId);
    }

    @PatchMapping(EventsConstants.PRIVATE_API_PREFIX + PRIVATE_API_USER_EVENT_REQUESTS)
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
        return eventsService.updateRequestsForEvent(updateRequestsStatusParameters);
    }
    // endregion

    // region ADMIN
    @GetMapping(EventsConstants.ADMIN_API_PREFIX)
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,

            @DateTimeFormat(pattern = EventsConstants.DATA_TIME_FORMAT)
            @RequestParam(required = false) LocalDateTime rangeStart,

            @DateTimeFormat(pattern = EventsConstants.DATA_TIME_FORMAT)
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
        return eventsService.searchEvents(searchEventsParameters);
    }

    @PatchMapping(EventsConstants.ADMIN_API_PREFIX + EVENT_ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@PathVariable(EVENT_ID) Long eventId,
                                           @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Request: update event id={} by admin, data={}", eventId, updateEventAdminRequest);
        return eventsService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }
    // endregion

    // region PUBLIC
    @GetMapping(EventsConstants.PUBLIC_API_PREFIX)
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchPublicEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,

            @DateTimeFormat(pattern = EventsConstants.DATA_TIME_FORMAT)
            @RequestParam(required = false) LocalDateTime rangeStart,

            @DateTimeFormat(pattern = EventsConstants.DATA_TIME_FORMAT)
            @RequestParam(required = false) LocalDateTime rangeEnd,

            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) SortingEvents sort,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            HttpServletRequest request) {
        SearchPublicEventsParameters searchPublicEventsParameters = SearchPublicEventsParameters.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .size(size)
                .build();
        log.info("Request: search public events. Query={}", searchPublicEventsParameters);
        List<EventFullDto> result = eventsService.searchPublicEvents(searchPublicEventsParameters);
        hitStat(request);
        return result;
    }

    @GetMapping(EventsConstants.PUBLIC_API_PREFIX + EVENT_ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public EventFullDtoWithComments getPublicEventById(@PathVariable(EVENT_ID) Long eventId, HttpServletRequest request) {
        log.info("Request: get public event with id={}", eventId);
        EventFullDtoWithComments result = eventsService.getPublicEventById(eventId);
        hitStat(request);
        return result;
    }

    @GetMapping(PUBLIC_API_PREFIX_COMMENTS)
    @ResponseStatus(HttpStatus.OK)
    public List<CommentShortDto> getAllEventComments(
            @PathVariable(EVENT_ID) Long eventId,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        GetAllCommentsParameters parameters = GetAllCommentsParameters.builder()
                .eventId(eventId)
                .from(from)
                .size(size)
                .build();
        log.info("Request: get all comments for event id={}.Parameters={}", eventId, parameters);
        return eventsService.getAllEventComments(parameters);
    }
    // endregion

    private void hitStat(HttpServletRequest request) {
        StatHitDto statHitDto = StatHitDto.builder()
                .app(applicationName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(Util.getNowTruncatedToSeconds())
                .build();
        try {
            statClient.hit(statHitDto);
        } catch (Exception e) {
            log.error("Error on hitting stats. Msg: {}, \nstackTrace: {}", e.getMessage(), e.getStackTrace());
        }
    }
}
