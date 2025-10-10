package ru.practicum.analyzer.service.config;

import ru.practicum.analyzer.enums.ActionType;

public interface ActionWeightService {
    double getWeight(ActionType actionType);
}