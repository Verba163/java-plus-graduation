package ru.practicum.ewm.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface RequestMapper {

    @Mapping(source = "request.id", target = "id")
    @Mapping(source = "request.created", target = "created", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    @Mapping(source = "request.event.id", target = "event")
    @Mapping(source = "request.requester.id", target = "requester")
    @Mapping(source = "request.status", target = "status")
    ParticipationRequestDto toRequestDto(Request request);

    @Mapping(target = "created", expression = "java(LocalDateTime.now())")
    @Mapping(source = "dto.id", target = "id")
    @Mapping(source = "event", target = "event")
    @Mapping(source = "requester", target = "requester")
    @Mapping(source = "dto.status", target = "status", qualifiedByName = "stringToStatus")
    Request toRequestEntity(ParticipationRequestDto dto, Event event, User requester);

    @Named("stringToStatus")
    default RequestStatus stringToStatus(String status) {
        return RequestStatus.valueOf(status);
    }

    @Mapping(target = "status", expression = "java(request.getStatus().name())")
    default String mapStatusToString(RequestStatus status) {
        return status.name();
    }
}