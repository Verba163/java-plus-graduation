package ru.practicum.comments.service.external;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.interaction.feign.clients.UserFeignClient;
import ru.practicum.interaction.user.dto.UserDto;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserFeignClient userFeignClient;

    public UserDto getUserById(Long userId) {
        return userFeignClient.getUserById(userId);
    }
}