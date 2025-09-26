package ru.practicum.events.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.events.model.Event;
import ru.practicum.events.params.MappingEventParameters;
import ru.practicum.interaction.category.dto.CategoryDto;
import ru.practicum.interaction.comments.dto.CommentShortDto;
import ru.practicum.interaction.events.dto.*;
import ru.practicum.interaction.events.dto.requests.UpdateEventAdminRequest;
import ru.practicum.interaction.events.dto.requests.UpdateEventCommonRequest;
import ru.practicum.interaction.events.dto.requests.UpdateEventUserRequest;
import ru.practicum.interaction.user.dto.UserShortDto;

import java.util.List;

@Component
public class EventMapper {
    public Event fromNewEventDto(NewEventDto newEventDto, Long categoryId) {
        return Event.builder()
                .title(newEventDto.getTitle())
                .description(newEventDto.getDescription())
                .annotation(newEventDto.getAnnotation())
                .categoryId(categoryId)
                .locationLat(newEventDto.getLocation().getLat())
                .locationLon(newEventDto.getLocation().getLon())
                .requestModeration(newEventDto.getRequestModeration())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .eventDate(newEventDto.getEventDate())
                .build();
    }

    public EventFullDto toEventFullDto(MappingEventParameters eventFullDtoParams) {
        Event event = eventFullDtoParams.getEvent();

        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(eventFullDtoParams.getCategoryDto())
                .confirmedRequests(eventFullDtoParams.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(eventFullDtoParams.getInitiator())
                .location(new LocationDto(event.getLocationLat(), event.getLocationLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                .state(event.getEventPublishState())
                .views(eventFullDtoParams.getViews())
                .build();
    }

    public EventFullDtoWithComments toEventFullDtoWithComments(MappingEventParameters eventFullDtoParams) {
        Event event = eventFullDtoParams.getEvent();

        return EventFullDtoWithComments.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(eventFullDtoParams.getCategoryDto())
                .confirmedRequests(eventFullDtoParams.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(eventFullDtoParams.getInitiator())
                .location(new LocationDto(event.getLocationLat(), event.getLocationLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                .state(event.getEventPublishState())
                .views(eventFullDtoParams.getViews())
                .comments(eventFullDtoParams.getComments())
                .build();
    }

    public EventShortDto toEventShortDto(MappingEventParameters eventDtoParams) {
        Event event = eventDtoParams.getEvent();

        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(eventDtoParams.getCategoryDto())
                .confirmedRequests(eventDtoParams.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(eventDtoParams.getInitiator())
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(eventDtoParams.getViews())
                .build();
    }

    public UpdateEventCommonRequest userUpdateRequestToCommonRequest(UpdateEventUserRequest request) {
        return UpdateEventCommonRequest.builder()
                .annotation(request.getAnnotation())
                .description(request.getDescription())
                .location(request.getLocation())
                .requestModeration(request.getRequestModeration())
                .participantLimit(request.getParticipantLimit())
                .category(request.getCategory())
                .eventDate(request.getEventDate())
                .paid(request.getPaid())
                .title(request.getTitle())
                .eventDate(request.getEventDate())
                .build();
    }

    public UpdateEventCommonRequest adminUpdateRequestToCommonRequest(UpdateEventAdminRequest request) {
        return UpdateEventCommonRequest.builder()
                .annotation(request.getAnnotation())
                .description(request.getDescription())
                .location(request.getLocation())
                .requestModeration(request.getRequestModeration())
                .participantLimit(request.getParticipantLimit())
                .category(request.getCategory())
                .eventDate(request.getEventDate())
                .paid(request.getPaid())
                .title(request.getTitle())
                .eventDate(request.getEventDate())
                .build();
    }

    public static MappingEventParameters createMappingEventParameter(Event event, CategoryDto categoryDto,
                                                              UserShortDto userShortDto,
                                                              Long views,
                                                              Long confirmedRequest) {
        return MappingEventParameters.builder()
                .event(event)
                .categoryDto(categoryDto)
                .initiator(userShortDto)
                .confirmedRequests(confirmedRequest)
                .views(views)
                .build();
    }

    public MappingEventParameters createMappingEventParameterWithComments(Event event, CategoryDto categoryDto,
                                                                          UserShortDto userShortDto,
                                                                          Long views,
                                                                          Long confirmedRequest,
                                                                          List<CommentShortDto> comments) {
        return MappingEventParameters.builder()
                .event(event)
                .categoryDto(categoryDto)
                .initiator(userShortDto)
                .confirmedRequests(confirmedRequest)
                .views(views)
                .comments(comments)
                .build();
    }
}