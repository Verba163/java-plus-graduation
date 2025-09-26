package ru.practicum.comments.service.external;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.interaction.feign.clients.RequestFeignClient;
import ru.practicum.interaction.request.dto.ParticipationRequestDto;

@Service
@RequiredArgsConstructor
public class ParticipationService {
    private final RequestFeignClient requestFeignClient;

    public ParticipationRequestDto getUserRequest(Long userId, Long eventId) {
        return requestFeignClient.getUserRequest(userId, eventId);
    }
}