package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.storage.EventsRepository;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.params.RequestValidator;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.Util;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestRepository.findByRequesterId(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createUserRequest(Long userId, Long eventId) {

        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event not found with id %d", eventId)));

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found with id %d", userId)));

        RequestStatus status = event.getParticipantLimit() == 0 || !event.getRequestModeration()
                ? RequestStatus.CONFIRMED
                : RequestStatus.PENDING;

        RequestValidator validator = new RequestValidator(event, userId, eventId, requestRepository);
        validator.validate();

        Request newRequest = Request.builder()
                .created(Util.getNowTruncatedToSeconds())
                .event(event)
                .requester(requester)
                .status(status)
                .build();
        return RequestMapper.toRequestDto(requestRepository.save(newRequest));
    }

    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request not found with id %d", requestId)));

        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }
}