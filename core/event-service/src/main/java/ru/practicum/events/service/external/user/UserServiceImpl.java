package ru.practicum.events.service.external.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.interaction.feign.clients.UserFeignClient;
import ru.practicum.interaction.user.dto.UserDto;
import ru.practicum.interaction.user.dto.UserShortDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserFeignClient userFeignClient;

    @Override
    public UserShortDto getUserShorDto(Long userId) {
        return userFeignClient.getUserShortDtoById(userId);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return userFeignClient.getUserById(userId);
    }
}
