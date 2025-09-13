package ru.practicum.ewm.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.dto.parameters.MappingEventParameters;
import ru.practicum.ewm.events.dto.requests.UpdateEventAdminRequest;
import ru.practicum.ewm.events.dto.requests.UpdateEventCommonRequest;
import ru.practicum.ewm.events.dto.requests.UpdateEventUserRequest;
import ru.practicum.ewm.events.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "newEventDto.location.lat", target = "locationLat")
    @Mapping(source = "newEventDto.location.lon", target = "locationLon")
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "eventPublishState", ignore = true)
    Event fromNewEventDto(NewEventDto newEventDto, Category category);

    @Mapping(source = "event.id", target = "id")
    @Mapping(source = "event.annotation", target = "annotation")
    @Mapping(source = "categoryDto", target = "category")
    @Mapping(source = "confirmedRequests", target = "confirmedRequests")
    @Mapping(source = "event.createdOn", target = "createdOn")
    @Mapping(source = "event.description", target = "description")
    @Mapping(source = "event.eventDate", target = "eventDate")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(target = "location", expression = "java(new LocationDto(eventFullDtoParams.getEvent().getLocationLat(), eventFullDtoParams.getEvent().getLocationLon()))")
    @Mapping(source = "event.paid", target = "paid")
    @Mapping(source = "event.participantLimit", target = "participantLimit")
    @Mapping(source = "event.publishedOn", target = "publishedOn")
    @Mapping(source = "event.requestModeration", target = "requestModeration")
    @Mapping(source = "event.title", target = "title")
    @Mapping(source = "event.eventPublishState", target = "state")
    @Mapping(source = "views", target = "views")
    EventFullDto toEventFullDto(MappingEventParameters eventFullDtoParams);

    @Mapping(source = "event.id", target = "id")
    @Mapping(source = "event.annotation", target = "annotation")
    @Mapping(source = "categoryDto", target = "category")
    @Mapping(source = "confirmedRequests", target = "confirmedRequests")
    @Mapping(source = "event.createdOn", target = "createdOn")
    @Mapping(source = "event.description", target = "description")
    @Mapping(source = "event.eventDate", target = "eventDate")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(target = "location", expression = "java(new LocationDto(eventFullDtoParams.getEvent().getLocationLat(), eventFullDtoParams.getEvent().getLocationLon()))")
    @Mapping(source = "event.paid", target = "paid")
    @Mapping(source = "event.participantLimit", target = "participantLimit")
    @Mapping(source = "event.publishedOn", target = "publishedOn")
    @Mapping(source = "event.requestModeration", target = "requestModeration")
    @Mapping(source = "event.title", target = "title")
    @Mapping(source = "event.eventPublishState", target = "state")
    @Mapping(source = "views", target = "views")
    @Mapping(source = "comments", target = "comments")
    EventFullDtoWithComments toEventFullDtoWithComments(MappingEventParameters eventFullDtoParams);

    @Mapping(source = "event.id", target = "id")
    @Mapping(source = "event.annotation", target = "annotation")
    @Mapping(source = "categoryDto", target = "category")
    @Mapping(source = "confirmedRequests", target = "confirmedRequests")
    @Mapping(source = "event.eventDate", target = "eventDate")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(source = "event.paid", target = "paid")
    @Mapping(source = "event.title", target = "title")
    @Mapping(source = "views", target = "views")
    EventShortDto toEventShortDto(MappingEventParameters eventDtoParams);

    UpdateEventCommonRequest userUpdateRequestToCommonRequest(UpdateEventUserRequest request);

    UpdateEventCommonRequest adminUpdateRequestToCommonRequest(UpdateEventAdminRequest request);

    default Category map(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category category = new Category();
        category.setId(Long.valueOf(categoryId));
        return category;
    }
}