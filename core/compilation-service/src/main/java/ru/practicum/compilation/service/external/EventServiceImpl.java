package ru.practicum.compilation.service.external;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.interaction.events.dto.EventShortDto;
import ru.practicum.interaction.feign.clients.EventFeignClient;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventFeignClient eventFeignClient;

    @Override
    public boolean checkEventsExist(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return true;
        }
        return eventFeignClient.checkEventExistingByIds(eventIds);
    }

    @Override
    public List<EventShortDto> getEventShortDtoList(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyList();
        }
        return eventFeignClient.getEventsShortDtoByIds(eventIds);
    }
}
