package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

import static ru.practicum.ewm.request.constants.RequestConstants.*;

@Slf4j
@RestController
@RequestMapping(USERS)
@RequiredArgsConstructor
public final class RequestController {

    private final RequestService requestService;

    @GetMapping(REQUEST_BASE_PATH)
    List<ParticipationRequestDto> getUserRequests(@PathVariable(USER_ID) Long userId) {
        return requestService.getUserRequests(userId);
    }

    @PostMapping(REQUEST_BASE_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    ParticipationRequestDto createUserRequest(@PathVariable(USER_ID) Long userId,
                                              @RequestParam Long eventId) {
        log.info("Creating request for user with ID: {} for event ID: {}", userId, eventId);
        return requestService.createUserRequest(userId, eventId);
    }


    @PatchMapping(REQUEST_BASE_PATCH_PATH)
    ParticipationRequestDto cancelUserRequest(@PathVariable(USER_ID) Long userId,
                                              @PathVariable(REQUEST_ID) Long requestId) {
        log.info("Cancelling request with ID: {} for user with ID: {}", requestId, userId);
        return requestService.cancelUserRequest(userId, requestId);

    }
}
