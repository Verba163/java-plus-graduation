package ru.practicum.ewm.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.params.UserQueryParams;
import ru.practicum.ewm.user.service.UserService;

import java.util.List;

import static ru.practicum.ewm.user.constants.UserConstants.*;


@Slf4j
@RestController
@RequestMapping(ADMIN_USER)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers(@Valid @ModelAttribute UserQueryParams params) {

        log.debug("Received GET request for all users with ids: {}, from: {}, size: {}",
                params.getIds(), params.getFrom(), params.getSize());

        return userService.getAllUsers(params);

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequestDto newUserRequestDto) {
        log.debug("Received POST request to create user: {}", newUserRequestDto);
        return userService.createUser(newUserRequestDto);
    }

    @DeleteMapping(USER_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@Valid @PathVariable(USER_ID) final long userId) {
        log.debug("Received DELETE request to remove user with id {}", userId);
        userService.deleteUser(userId);
    }

}
