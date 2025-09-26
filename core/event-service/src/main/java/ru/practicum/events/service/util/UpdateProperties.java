package ru.practicum.events.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.events.model.Event;
import ru.practicum.events.service.external.category.CategoryService;
import ru.practicum.events.service.validation.EventValidator;
import ru.practicum.interaction.events.dto.LocationDto;
import ru.practicum.interaction.events.dto.requests.UpdateEventCommonRequest;

@Component
@RequiredArgsConstructor
public class UpdateProperties {

    private final EventValidator eventValidator;
    private final CategoryService categoryService;

    public void updateEventProperties(Event event, UpdateEventCommonRequest updateEventRequest) {
        if (updateEventRequest.getEventDate() != null) {
            eventValidator.checkEventDateIsValid(updateEventRequest.getEventDate());
            event.setEventDate(updateEventRequest.getEventDate());
        }
        if (updateEventRequest.getCategory() != null) {
            event.setCategoryId(categoryService.getCategoryWithCheck(updateEventRequest.getCategory()).getId());
        }
        if (updateEventRequest.getTitle() != null) event.setTitle(updateEventRequest.getTitle());
        if (updateEventRequest.getDescription() != null) event.setDescription(updateEventRequest.getDescription());
        if (updateEventRequest.getAnnotation() != null) event.setAnnotation(updateEventRequest.getAnnotation());
        if (updateEventRequest.getLocation() != null) {
            LocationDto loc = updateEventRequest.getLocation();
            event.setLocationLat(loc.getLat());
            event.setLocationLon(loc.getLon());
        }
        if (updateEventRequest.getRequestModeration() != null)
            event.setRequestModeration(updateEventRequest.getRequestModeration());
        if (updateEventRequest.getPaid() != null) event.setPaid(updateEventRequest.getPaid());
        if (updateEventRequest.getParticipantLimit() != null)
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
    }
}
