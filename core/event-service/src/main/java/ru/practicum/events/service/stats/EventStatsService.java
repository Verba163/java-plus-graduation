package ru.practicum.events.service.stats;

import jakarta.servlet.http.HttpServletRequest;

public interface EventStatsService {

    void recordHit(HttpServletRequest request);

}
