package ru.practicum.events.service.assemblers;

import ru.practicum.events.model.Event;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.events.dto.EventFullDtoWithComments;
import ru.practicum.interaction.events.dto.EventShortDto;

import java.util.List;


public interface EventDtoAssemblers {

    EventFullDto createEventFullDto(Event event, Long views, Long requests);

    EventFullDto createEventFullDto(Event event);

    EventFullDtoWithComments createEventFullDtoWithComments(Event event);

    List<EventFullDto> createEventFullDtoList(List<Event> events);

    List<EventShortDto> createEventShortDtoList(List<Event> events);
}
