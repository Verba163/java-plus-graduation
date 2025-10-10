package ru.practicum.events.controller.publics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.service.publics.PublicEventsService;
import ru.practicum.interaction.comments.dto.CommentShortDto;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.events.dto.EventFullDtoWithComments;
import ru.practicum.interaction.events.dto.EventShortDto;
import ru.practicum.interaction.events.dto.parameters.GetAllCommentsParameters;
import ru.practicum.interaction.events.dto.parameters.SearchPublicEventsParameters;
import ru.practicum.interaction.events.enums.SortingEvents;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.interaction.events.constants.EventsApiPath.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PublicEventsController {

    private final PublicEventsService publicEventsService;

    @GetMapping(PUBLIC_API_PREFIX)
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchPublicEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,

            @DateTimeFormat(pattern = DATA_TIME_FORMAT)
            @RequestParam(required = false) LocalDateTime rangeStart,

            @DateTimeFormat(pattern = DATA_TIME_FORMAT)
            @RequestParam(required = false) LocalDateTime rangeEnd,

            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) SortingEvents sort,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

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
        return publicEventsService.searchPublicEvents(searchPublicEventsParameters);
    }

    @GetMapping(PUBLIC_API_PREFIX + EVENT_ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public EventFullDtoWithComments getPublicEventById(@PathVariable(EVENT_ID) Long eventId,
                                                       @RequestHeader("X-EWM-USER-ID") long userId) {
        log.info("Request: get public event with id={}", eventId);
        return publicEventsService.getPublicEventById(eventId);
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
        return publicEventsService.getAllEventComments(parameters);
    }

    @GetMapping(PUBLIC_API_PREFIX + RECOMMENDATION_PATH)
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getRecommendation(@RequestHeader(USER_API_HEADER) long userId,
                                                 @RequestParam(required = false, defaultValue = "5") int maxResult) {
        log.info("Request: get recommendation for user with id={}", userId);
        return publicEventsService.getRecommendation(userId, maxResult);
    }

    @PutMapping(PUBLIC_API_PREFIX + EVENT_ID_PATH + LIKE_API_PATH)
    @ResponseStatus(HttpStatus.OK)
    public void addLikeToEvent(@PathVariable(EVENT_ID) Long eventId,
                               @RequestHeader(USER_API_HEADER) long userId) {
        log.info("Request: put like for event with id={} from user with id={}", eventId, userId);

        publicEventsService.addLikeToEvent(eventId, userId);
    }
}