package ru.practicum.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interaction.user.dto.NewUserRequestDto;
import ru.practicum.interaction.user.dto.UserDto;
import ru.practicum.interaction.user.params.UserQueryParams;
import ru.practicum.user.service.user.UserService;

import java.util.List;

import static ru.practicum.interaction.user.constants.UserConstants.*;


@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(ADMIN_USER)
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers(@Valid @ModelAttribute UserQueryParams params) {
        log.debug("Received GET request for all users with ids: {}, from: {}, size: {}",
                params.getIds(), params.getFrom(), params.getSize());
        return userService.getAllUsers(params);

    }

    @PostMapping(ADMIN_USER)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequestDto newUserRequestDto) {
        log.debug("Received POST request to create user: {}", newUserRequestDto);
        return userService.createUser(newUserRequestDto);
    }

    @DeleteMapping(ADMIN_USER + USER_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Valid @PathVariable(USER_ID) final long userId) {
        log.debug("Received DELETE request to remove user with id {}", userId);
        userService.deleteUser(userId);
    }

    @GetMapping(ADMIN_USER + USER_ID_PATH)
    public UserDto getUserById(@Valid @PathVariable(USER_ID) Long userId) {
        log.debug("Received GET request for user with id: {}}", userId);
        return userService.getUserById(userId);
    }
}

