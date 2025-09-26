package ru.practicum.user.service.user;


import ru.practicum.interaction.user.dto.NewUserRequestDto;
import ru.practicum.interaction.user.dto.UserDto;
import ru.practicum.interaction.user.params.UserQueryParams;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequestDto newUserRequestDto);

    List<UserDto> getAllUsers(UserQueryParams params);

    UserDto getUserById(Long userId);

    void deleteUser(Long userId);

}
