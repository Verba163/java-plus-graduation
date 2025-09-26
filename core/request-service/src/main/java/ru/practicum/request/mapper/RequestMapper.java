package ru.practicum.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.interaction.request.dto.ParticipationRequestDto;
import ru.practicum.interaction.request.enums.RequestStatus;
import ru.practicum.interaction.util.Util;
import ru.practicum.request.model.Request;

import java.time.LocalDateTime;

@Component
public class RequestMapper {

    public  Request toNewRequestEntity(Long eventId, Long requestId, RequestStatus status) {

        return Request.builder()
                .created(Util.getNowTruncatedToSeconds())
                .eventId(eventId)
                .requesterId(requestId)
                .status(status)
                .build();
    }

    public  ParticipationRequestDto toRequestDto(Request request) {

        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEventId())
                .requester(request.getRequesterId())
                .status(request.getStatus().name())
                .build();
    }

    public  Request toRequestEntity(ParticipationRequestDto dto, Long eventId, Long requesterId) {

        return Request.builder()
                .id(dto.getId())
                .created(LocalDateTime.now())
                .eventId(eventId)
                .requesterId(requesterId)
                .status(RequestStatus.valueOf(dto.getStatus()))
                .build();
    }

    public  Request toRequestEntityFromDto(ParticipationRequestDto dto) {

        return Request.builder()
                .id(dto.getId())
                .created(LocalDateTime.now())
                .eventId(dto.getEvent())
                .requesterId(dto.getRequester())
                .status(RequestStatus.valueOf(dto.getStatus()))
                .build();
    }
}