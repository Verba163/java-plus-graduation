package ru.practicum.analyzer.service.params;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.model.UserAction;
import ru.practicum.analyzer.repository.UserActionRepository;
import ru.practicum.analyzer.service.config.ActionWeightService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserActionService {

    private final UserActionRepository userActionRepository;
    private final ActionWeightService actionWeightService;

    public Set<Long> getRecentlyViewedEventIds(Long userId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        return userActionRepository.findAllByUserId(userId, pageRequest).stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());
    }

    public boolean hasUserInteractedWithEvent(Long userId, Long eventId) {
        return userActionRepository.existsByEventIdAndUserId(eventId, userId);
    }

    public Map<Long, Double> getUserRatingsForEvents(Long userId, Set<Long> eventIds) {
        return userActionRepository.findAllByEventIdInAndUserId(eventIds, userId).stream()
                .collect(Collectors.toMap(
                        UserAction::getEventId,
                        userAction -> actionWeightService.getWeight(userAction.getActionType())
                ));
    }

    public Map<Long, Double> computeEventScores(Set<Long> eventIds) {
        var eventScores = new HashMap<Long, Double>();

        userActionRepository.findAllByEventIdIn(eventIds).forEach(action -> {
            long eventId = action.getEventId();
            double weight = actionWeightService.getWeight(action.getActionType());
            eventScores.merge(eventId, weight, Double::sum);
        });

        return eventScores;
    }
}