package ru.practicum.events.service.feign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.events.model.Event;
import ru.practicum.events.service.assemblers.EventDtoAssemblers;
import ru.practicum.events.service.validation.EventValidator;
import ru.practicum.events.storage.EventsRepository;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.events.dto.EventShortDto;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FeignEventsServiceImpl implements FeignEventsService {

    private final EventsRepository eventsRepository;
    private final EventValidator eventValidator;
    private final EventDtoAssemblers eventDtoAssemblers;

    @Override
    @Transactional(readOnly = true)
    public Long getEventsCountByCategoryId(Long catId) {
        return eventsRepository.countByCategoryId(catId);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean checkEventExistingByIds(List<Long> eventIds) {
        List<Event> events = eventsRepository.findAllById(eventIds);
        return events.size() == eventIds.size();
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long eventId) {
        Event event = eventValidator.getEventWithCheck(eventId);
        return eventDtoAssemblers.createEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsShortDtoByIds(List<Long> eventIds) {
        List<Event> events = eventsRepository.findAllById(eventIds);
        return eventDtoAssemblers.createEventShortDtoList(events);
    }
}