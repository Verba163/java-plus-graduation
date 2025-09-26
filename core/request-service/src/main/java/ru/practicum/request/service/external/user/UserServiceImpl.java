package ru.practicum.request.service.external.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.interaction.feign.clients.UserFeignClient;
import ru.practicum.interaction.user.dto.UserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserFeignClient userFeignClient;

    @Override
    public UserDto getUserDto(Long userId) {
        return userFeignClient.getUserById(userId);
    }
}
