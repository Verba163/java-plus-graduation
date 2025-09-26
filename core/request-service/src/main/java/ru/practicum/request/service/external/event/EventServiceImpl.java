package ru.practicum.request.service.external.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.feign.clients.EventFeignClient;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventFeignClient eventFeignClient;

    @Override
    public EventFullDto getEventFullDto(Long eventId) {
        return eventFeignClient.getEventById(eventId);
    }
}