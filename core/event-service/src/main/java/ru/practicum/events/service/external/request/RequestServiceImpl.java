package ru.practicum.events.service.external.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.interaction.feign.clients.RequestFeignClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestFeignClient requestFeignClient;

    @Override
    public Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return new HashMap<>();
        }
        return requestFeignClient.getConfirmedRequestsCount(eventIds);
    }
}