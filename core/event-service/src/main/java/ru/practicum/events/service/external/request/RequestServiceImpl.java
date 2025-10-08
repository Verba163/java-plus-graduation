package ru.practicum.events.service.external.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.events.service.external.user.UserService;
import ru.practicum.events.service.validation.EventValidator;
import ru.practicum.interaction.feign.clients.RequestFeignClient;
import ru.practicum.interaction.request.dto.ParticipationRequestDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestFeignClient requestFeignClient;
    private final UserService userService;
    private final EventValidator eventValidator;

    @Override
    public Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return new HashMap<>();
        }
        return requestFeignClient.getConfirmedRequestsCount(eventIds);
    }

    @Override
    public ParticipationRequestDto getUsersRequest(Long userId, Long eventId) {

        userService.getUserById(userId);
        eventValidator.getEventWithCheck(eventId);

        return requestFeignClient.getUserRequest(userId, eventId);
    }
}