package ru.practicum.request.controller.feign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.feign.FeignRequestService;

import java.util.List;
import java.util.Map;

import static ru.practicum.interaction.events.constants.EventsApiPath.EVENT_ID;
import static ru.practicum.interaction.feign.FeignPathConstants.*;
import static ru.practicum.interaction.request.constants.RequestConstants.USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeignRequestController {

    private final FeignRequestService feignRequestService;


    @GetMapping(GET_USER_REQUESTS)
    public ParticipationRequestDto getUserRequest(@PathVariable(USER_ID) Long userId,
                                                  @PathVariable(EVENT_ID) Long eventId) {
        return feignRequestService.getUserRequest(userId, eventId);
    }

    @GetMapping(CONFIRMED_COUNT)
    public Map<Long, Long> getConfirmedRequestsCount(@RequestParam("eventIds") List<Long> eventIds) {
        log.info("Request to get confirmed request count whit events IDs: {}", eventIds);
        return feignRequestService.getConfirmedRequestsCount(eventIds);
    }

    @GetMapping(GET_REQUEST_BY_IDS)
    public List<ParticipationRequestDto> getRequestsByIds(@RequestParam(name = "requestIds", required = false) List<Long> requestIds) {
        return feignRequestService.getRequestsByIds(requestIds);
    }

    @PostMapping(UPDATE_REQUESTS)
    public void updateRequests(@RequestBody List<ParticipationRequestDto> request) {
        feignRequestService.updateRequests(request);
    }

    @GetMapping(GET_EVENT_REQUESTS)
    public List<ParticipationRequestDto> getEventRequests(@PathVariable(EVENT_ID) Long eventId) {
        return feignRequestService.getEventRequests(eventId);
    }
}
