package ru.practicum.user.service.feign;

import ru.practicum.interaction.user.dto.UserShortDto;

public interface FeignUserService {
    UserShortDto getUserShortDtoById(Long userId);
}
