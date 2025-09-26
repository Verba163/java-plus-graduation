package ru.practicum.request.service.user;

import ru.practicum.interaction.request.dto.ParticipationRequestDto;

import java.util.List;

public interface UserRequestService {

    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto createUserRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelUserRequest(Long userId, Long requestId);
}
