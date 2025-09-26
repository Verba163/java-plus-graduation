package ru.practicum.interaction.feign.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.events.dto.EventShortDto;
import ru.practicum.interaction.feign.config.FeignConfig;

import java.util.List;

import static ru.practicum.interaction.category.constants.CategoryApiPath.CAT_ID;
import static ru.practicum.interaction.events.constants.EventsApiPath.EVENT_ID;
import static ru.practicum.interaction.feign.FeignPathConstants.*;

@FeignClient(name = "event-service", configuration = {FeignConfig.class})
public interface EventFeignClient {

    @GetMapping(EVENT_COUNT_BY_CATEGORY)
    Long countEventsByCategoryId(@PathVariable(CAT_ID) Long categoryId);

    @GetMapping(GET_EVENT_BY_ID)
    EventFullDto getEventById(@PathVariable(EVENT_ID) Long eventId);

    @GetMapping(GET_EVENT_SHORT_DTO)
    List<EventShortDto> getEventsShortDtoByIds(@RequestParam("eventIds") List<Long> eventIds);

    @GetMapping(CHECK_EVENT_EXISTING)
    Boolean checkEventExistingByIds(@RequestParam("eventIds") List<Long> eventIds);
}

