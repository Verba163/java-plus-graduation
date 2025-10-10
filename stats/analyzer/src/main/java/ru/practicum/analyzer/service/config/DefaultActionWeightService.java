package ru.practicum.analyzer.service.config;

import org.springframework.stereotype.Component;
import ru.practicum.analyzer.enums.ActionType;

import java.util.Map;

@Component
public class DefaultActionWeightService implements ActionWeightService {

    private final Map<ActionType, Double> weights = Map.of(
            ActionType.VIEW, 0.4,
            ActionType.REGISTER, 0.8,
            ActionType.LIKE, 1.0
    );

    @Override
    public double getWeight(ActionType actionType) {
        return weights.getOrDefault(actionType, 0.0);
    }
}