package ru.practicum.analyzer.service.params;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionService {

    private final UserActionRepository userActionRepository;
    private final ActionWeightService actionWeightService;

    public Set<Long> getRecentlyViewedEventIds(Long userId, int limit) {
        log.info("Fetching up to {} recent actions for userId={}", limit, userId);
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        Set<Long> eventIds = userActionRepository.findAllByUserId(userId, pageRequest).stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());
        log.info("Retrieved {} eventIds for userId={}", eventIds.size(), userId);
        return eventIds;
    }

    public boolean hasUserInteractedWithEvent(Long userId, Long eventId) {
        boolean exists = userActionRepository.existsByEventIdAndUserId(eventId, userId);
        log.debug("User {} {} eventId={}", userId, exists ? "has" : "has not", eventId);
        return exists;
    }

    public Map<Long, Double> getUserRatingsForEvents(Long userId, Set<Long> eventIds) {
        log.info("Fetching user ratings for userId={} on {} events", userId, eventIds.size());
        Map<Long, Double> ratings = userActionRepository.findAllByEventIdInAndUserId(eventIds, userId).stream()
                .collect(Collectors.toMap(
                        UserAction::getEventId,
                        userAction -> {
                            double weight = actionWeightService.getWeight(userAction.getActionType());
                            log.debug("EventId={} actionType={} mapped to weight={}", userAction.getEventId(), userAction.getActionType(), weight);
                            return weight;
                        }
                ));
        log.info("Collected ratings for userId={} for {} events", userId, ratings.size());
        return ratings;
    }

    public Map<Long, Double> computeEventScores(Set<Long> eventIds) {
        log.info("Computing scores for {} events", eventIds.size());
        Map<Long, Double> eventScores = new HashMap<>();

        userActionRepository.findAllByEventIdIn(eventIds).forEach(action -> {
            long eventId = action.getEventId();
            double weight = actionWeightService.getWeight(action.getActionType());
            eventScores.merge(eventId, weight, Double::sum);
            log.debug("Added weight={} to eventId={}, current total={}", weight, eventId, eventScores.get(eventId));
        });

        log.info("Computed scores for {} events", eventScores.size());
        return eventScores;
    }
}