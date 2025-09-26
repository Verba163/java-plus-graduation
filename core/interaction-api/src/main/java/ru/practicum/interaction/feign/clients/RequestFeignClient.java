package ru.practicum.interaction.feign.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.feign.config.FeignConfig;
import ru.practicum.interaction.request.dto.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

import static ru.practicum.interaction.events.constants.EventsApiPath.EVENT_ID;
import static ru.practicum.interaction.feign.FeignPathConstants.*;
import static ru.practicum.interaction.user.constants.UserConstants.USER_ID;

@FeignClient(name = "request-service", configuration = FeignConfig.class)
public interface RequestFeignClient {

    @GetMapping(GET_USER_REQUESTS)
    ParticipationRequestDto getUserRequest(@PathVariable(USER_ID) Long userId,
                                           @PathVariable(EVENT_ID) Long eventId);


    @GetMapping(CONFIRMED_COUNT)
    Map<Long, Long> getConfirmedRequestsCount(@RequestParam("eventIds") List<Long> eventIds);

    @GetMapping(GET_REQUEST_BY_IDS)
    List<ParticipationRequestDto> getRequestsByIds(@RequestParam(name = "requestIds", required = false)
                                                   List<Long> requestIds);

    @PostMapping(value = UPDATE_REQUESTS, consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateRequests(@RequestBody List<ParticipationRequestDto> request);

    @GetMapping(GET_EVENT_REQUESTS)
    List<ParticipationRequestDto> getEventRequests(@PathVariable(EVENT_ID) Long eventId);

}
