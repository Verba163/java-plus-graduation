package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.params.UserQueryParams;

import java.util.List;

public interface UserService {

    UserDto createUser(NewUserRequestDto newUserRequestDto);

    List<UserDto> getAllUsers(UserQueryParams params);

    UserDto getUserById(Long userId);

    void deleteUser(Long userId);
}
