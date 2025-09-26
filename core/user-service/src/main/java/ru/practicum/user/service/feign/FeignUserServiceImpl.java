package ru.practicum.user.service.feign;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.error.exception.NotFoundException;
import ru.practicum.interaction.user.dto.UserShortDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FeignUserServiceImpl implements FeignUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public UserShortDto getUserShortDtoById(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserShortDto)
                .orElseThrow(() -> new NotFoundException(String.format("User with id %d not found", userId)));
    }
}
