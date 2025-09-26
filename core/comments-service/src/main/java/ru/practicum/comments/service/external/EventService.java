package ru.practicum.comments.service.external;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.feign.clients.EventFeignClient;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventFeignClient eventFeignClient;

    public EventFullDto getEventById(Long eventId) {
        return eventFeignClient.getEventById(eventId);
    }
}
