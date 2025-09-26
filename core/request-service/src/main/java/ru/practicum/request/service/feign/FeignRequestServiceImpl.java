package ru.practicum.request.service.feign;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interaction.error.exception.NotFoundException;
import ru.practicum.interaction.request.dto.ParticipationRequestDto;
import ru.practicum.interaction.request.enums.RequestStatus;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeignRequestServiceImpl implements FeignRequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional(readOnly = true)
    public ParticipationRequestDto getUserRequest(Long userId, Long eventId) {
        Request request = requestRepository.findByRequesterIdAndEventId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format
                        ("Request not found for user with id %d", userId)
                ));
        return requestMapper.toRequestDto(request);
    }

    @Transactional(readOnly = true)
    @Override
    public Map<Long, Long> getConfirmedRequestsCount(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return new HashMap<>();
        }

        List<Request> confirmedRequests = requestRepository.findByEventIdInAndStatus(
                eventIds, RequestStatus.CONFIRMED
        );

        return confirmedRequests.stream()
                .collect(Collectors.groupingBy(
                        Request::getEventId,
                        Collectors.counting()
                ));
    }

    @Transactional
    @Override
    public void updateRequests(List<ParticipationRequestDto> requestDto) {
        if (requestDto == null || requestDto.isEmpty()) {
            return;
        }
        List<Request> requests = requestDto.stream().map(requestMapper::toRequestEntityFromDto).toList();
        requestRepository.saveAll(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByIds(List<Long> requestIds) {
        if (requestIds == null || requestIds.isEmpty()) {
            return List.of();
        }
        List<Request> requests = requestRepository.findAllById(requestIds);
        return requests.stream()
                .map(requestMapper::toRequestDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(Long eventId) {
        List<Request> requests = requestRepository.findByEventId(eventId);
        return requests.stream()
                .map(requestMapper::toRequestDto)
                .toList();
    }
}
