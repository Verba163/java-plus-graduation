package ru.practicum.events.service.external.user;

import ru.practicum.interaction.user.dto.UserDto;
import ru.practicum.interaction.user.dto.UserShortDto;

public interface UserService {

    UserShortDto getUserShorDto(Long userId);

    UserDto getUserById(Long userId);
}
