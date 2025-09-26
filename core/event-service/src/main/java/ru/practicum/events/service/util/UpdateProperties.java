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

    public void updateEventProperties(Event event, UpdateEventCommonRequest props) {
        if (props.getEventDate() != null) {
            eventValidator.checkEventDateIsValid(props.getEventDate());
            event.setEventDate(props.getEventDate());
        }
        if (props.getCategory() != null) {
            event.setCategoryId(categoryService.getCategoryWithCheck(props.getCategory()).getId());
        }
        if (props.getTitle() != null) event.setTitle(props.getTitle());
        if (props.getDescription() != null) event.setDescription(props.getDescription());
        if (props.getAnnotation() != null) event.setAnnotation(props.getAnnotation());
        if (props.getLocation() != null) {
            LocationDto loc = props.getLocation();
            event.setLocationLat(loc.getLat());
            event.setLocationLon(loc.getLon());
        }
        if (props.getRequestModeration() != null) event.setRequestModeration(props.getRequestModeration());
        if (props.getPaid() != null) event.setPaid(props.getPaid());
        if (props.getParticipantLimit() != null) event.setParticipantLimit(props.getParticipantLimit());
    }
}
