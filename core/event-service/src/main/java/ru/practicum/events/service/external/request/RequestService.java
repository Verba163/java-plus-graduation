package ru.practicum.events.service.external.request;

import ru.practicum.interaction.request.dto.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

public interface RequestService {
    Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds);

    ParticipationRequestDto getUsersRequest(Long userId, Long eventId);
}
