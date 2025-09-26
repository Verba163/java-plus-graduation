package ru.practicum.events.controller.feign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.service.feign.FeignEventsService;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.events.dto.EventShortDto;

import java.util.List;

import static ru.practicum.interaction.category.constants.CategoryApiPath.CAT_ID;
import static ru.practicum.interaction.events.constants.EventsApiPath.EVENT_ID;
import static ru.practicum.interaction.feign.FeignPathConstants.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FeignEventsController {

    private final FeignEventsService feignEventsService;

    @GetMapping(EVENT_COUNT_BY_CATEGORY)
    @ResponseStatus(HttpStatus.OK)
    public Long getEventsCountByCategoryId(@PathVariable(CAT_ID) Long catId) {
        log.info("Request: get count events by category id={} for feign.", catId);
        return feignEventsService.getEventsCountByCategoryId(catId);
    }

    @GetMapping(GET_EVENT_BY_ID)
    @ResponseStatus(HttpStatus.OK)
    EventFullDto getEventById(@PathVariable(EVENT_ID) Long eventId) {
        log.info("Request: get event id={} for feign.", eventId);
        return feignEventsService.getEventById(eventId);
    }

    @GetMapping(GET_EVENT_SHORT_DTO)
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsShortDtoByIds(@RequestParam("eventIds") List<Long> eventIds) {
        log.info("Request: get events for feign.");
        return feignEventsService.getEventsShortDtoByIds(eventIds);
    }

    @GetMapping(CHECK_EVENT_EXISTING)
    @ResponseStatus(HttpStatus.OK)
    public Boolean checkEventExistingByIds(@RequestParam("eventIds") List<Long> eventIds) {
        log.info("Request: check event by ids for feign.");
        return feignEventsService.checkEventExistingByIds(eventIds);
    }
}