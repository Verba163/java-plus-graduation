package ru.practicum.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.interaction.user.dto.UserShortDto;
import ru.practicum.user.service.feign.FeignUserService;

import static ru.practicum.interaction.feign.FeignPathConstants.GET_USER_SHORT_DTO;
import static ru.practicum.interaction.user.constants.UserConstants.USER_ID;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class FeignUserController {

    private final FeignUserService feignUserService;

    @GetMapping(GET_USER_SHORT_DTO)
    public UserShortDto getUserShortDtoById(@Valid @PathVariable(USER_ID) Long userId) {
        log.debug("Received GET request for user with id: {}}", userId);
        return feignUserService.getUserShortDtoById(userId);
    }
}
