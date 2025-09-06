package ru.practicum.ewm.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.user.dto.NewUserRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

@Component
public class UserMapper {

    public static UserDto toUserDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto toUserShortDto(User user) {

        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static User toUserEntity(UserDto userDto) {

        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static User toUserEntity(UserShortDto userShortDto) {

        return User.builder()
                .id(userShortDto.getId())
                .name(userShortDto.getName())
                .build();
    }

    public static User toUserEntity(NewUserRequestDto newUserRequestDto) {
        return User.builder()
                .name(newUserRequestDto.getName())
                .email(newUserRequestDto.getEmail())
                .build();
    }
}


