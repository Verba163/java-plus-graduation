package ru.practicum.ewm.events.service.params.assemblers.publics;

import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventFullDtoWithComments;
import ru.practicum.ewm.events.model.Event;

public interface PublicEventDtoAssembler {

    EventFullDtoWithComments createEventFullDtoWithComments(Event event);

    EventFullDto createEventFullDto(Event event, long views, long confirmedRequests);
}
