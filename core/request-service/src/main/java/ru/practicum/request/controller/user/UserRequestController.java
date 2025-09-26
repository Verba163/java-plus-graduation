package ru.practicum.request.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.user.UserRequestService;

import java.util.List;

import static ru.practicum.interaction.request.constants.RequestConstants.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserRequestController {

    private final UserRequestService userRequestService;

    @GetMapping(USERS + REQUEST_BASE_PATH)
    List<ParticipationRequestDto> getUserRequests(@PathVariable(USER_ID) Long userId) {
        return userRequestService.getUserRequests(userId);
    }

    @PostMapping(USERS + REQUEST_BASE_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    ParticipationRequestDto createUserRequest(@PathVariable(USER_ID) Long userId,
                                              @RequestParam Long eventId) {
        log.info("Creating request for user with ID: {} for event ID: {}", userId, eventId);
        return userRequestService.createUserRequest(userId, eventId);
    }


    @PatchMapping(USERS + REQUEST_BASE_PATCH_PATH)
    ParticipationRequestDto cancelUserRequest(@PathVariable(USER_ID) Long userId,
                                              @PathVariable(REQUEST_ID) Long requestId) {
        log.info("Cancelling request with ID: {} for user with ID: {}", requestId, userId);
        return userRequestService.cancelUserRequest(userId, requestId);
    }
}