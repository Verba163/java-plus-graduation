package ru.practicum.ewm.events.service.params.stats;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface EventStatsService {

    void recordHit(HttpServletRequest request);

    Map<Long, Long> getConfirmedRequests(List<Long> eventIds);
}
