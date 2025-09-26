package ru.practicum.request.service.external.user;

import ru.practicum.interaction.user.dto.UserDto;

public interface UserService {
    UserDto getUserDto(Long userId);
}
