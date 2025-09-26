package ru.practicum.interaction.feign.clients;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.interaction.feign.config.FeignConfig;
import ru.practicum.interaction.user.dto.UserDto;
import ru.practicum.interaction.user.dto.UserShortDto;

import static ru.practicum.interaction.feign.FeignPathConstants.GET_USER_SHORT_DTO;
import static ru.practicum.interaction.user.constants.UserConstants.*;

@FeignClient(name = "user-service", configuration = {FeignConfig.class})
public interface UserFeignClient {

    @GetMapping(ADMIN_USER + USER_ID_PATH)
    UserDto getUserById(@Valid @PathVariable(USER_ID) Long userId);

    @GetMapping(GET_USER_SHORT_DTO)
    UserShortDto getUserShortDtoById(@Valid @PathVariable(USER_ID) Long userId);

}
