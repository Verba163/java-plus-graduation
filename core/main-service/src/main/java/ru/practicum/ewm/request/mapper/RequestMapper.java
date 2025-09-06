package ru.practicum.ewm.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Component
public class RequestMapper {

    public static ParticipationRequestDto toRequestDto(Request request) {

        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated().toString())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().name())
                .build();
    }

    public static Request toRequestEntity(ParticipationRequestDto dto, Event event, User requester) {

        return Request.builder()
                .id(dto.getId())
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(RequestStatus.valueOf(dto.getStatus()))
                .build();
    }
}