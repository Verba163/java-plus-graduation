package ru.practicum.request.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.UserActionClient;
import ru.practicum.grpc.stats.action.ActionTypeProto;
import ru.practicum.interaction.error.exception.NotFoundException;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.request.dto.ParticipationRequestDto;
import ru.practicum.interaction.request.enums.RequestStatus;
import ru.practicum.interaction.user.dto.UserDto;
import ru.practicum.interaction.util.Util;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.params.RequestValidator;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.request.service.external.event.EventService;
import ru.practicum.request.service.external.user.UserService;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRequestServiceImpl implements UserRequestService {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final EventService eventService;
    private final UserService userService;
    private final UserActionClient userActionClient;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestRepository.findByRequesterId(userId).stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto createUserRequest(Long userId, Long eventId) {

        EventFullDto event = eventService.getEventFullDto(eventId);

        UserDto requester = userService.getUserDto(userId);

        RequestStatus status = event.getParticipantLimit() == 0 || !event.getRequestModeration()
                ? RequestStatus.CONFIRMED
                : RequestStatus.PENDING;

        RequestValidator validator = new RequestValidator(event, userId, eventId, requestRepository);
        validator.validate();

        Request newRequest = Request.builder()
                .created(Util.getNowTruncatedToSeconds())
                .eventId(event.getId())
                .requesterId(requester.getId())
                .status(status)
                .build();
        userActionClient.collectUserAction(userId, eventId, ActionTypeProto.ACTION_REGISTER, Instant.now());
        return requestMapper.toRequestDto(requestRepository.save(newRequest));
    }

    @Override
    public ParticipationRequestDto cancelUserRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request not found with id %d", requestId)));

        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }
}
