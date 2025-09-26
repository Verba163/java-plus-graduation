package ru.practicum.request.service.feign;

import ru.practicum.interaction.request.dto.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

public interface FeignRequestService {

    ParticipationRequestDto getUserRequest(Long userId, Long eventId);

    Map<Long, Long> getConfirmedRequestsCount(List<Long> eventIds);

    void updateRequests(List<ParticipationRequestDto> requestDto);

    List<ParticipationRequestDto> getRequestsByIds(List<Long> requestIds);

    List<ParticipationRequestDto> getEventRequests(Long eventId);
}