package ru.practicum.analyzer.service.params;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.model.EventSimilarity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationScoringService {

    private final EventSimilarityService similarityService;
    private final UserActionService userActionService;

    public double calculateRecommendationScore(Long eventId, Long userId, int limit) {

        log.info("Calculating recommendation score for eventId={} and userId={}, limit={}", eventId, userId, limit);

        List<EventSimilarity> similaritiesA = similarityService.findSimilarByEventA(eventId, limit);
        log.debug("Found {} similarities for eventA", similaritiesA.size());

        List<EventSimilarity> similaritiesB = similarityService.findSimilarByEventB(eventId, limit);
        log.debug("Found {} similarities for eventB", similaritiesB.size());

        Map<Long, Double> similarityScores = new HashMap<>();

        collectViewedSimilarities(similaritiesA, true, userId, similarityScores);

        collectViewedSimilarities(similaritiesB, false, userId, similarityScores);
        log.info("Collected {} related events with similarity scores", similarityScores.size());

        Map<Long, Double> userRatings = userActionService.getUserRatingsForEvents(userId, similarityScores.keySet());
        log.debug("User ratings retrieved for {} related events", userRatings.size());

        double sumWeightedRatings = 0;
        double sumSimilarityScores = 0;

        for (Map.Entry<Long, Double> entry : similarityScores.entrySet()) {
            Long viewedEventId = entry.getKey();
            Double rating = userRatings.get(viewedEventId);
            if (rating != null) {
                sumWeightedRatings += rating * entry.getValue();
                sumSimilarityScores += entry.getValue();
                log.debug("EventId={} rating={} weighted contribution={}", viewedEventId, rating, rating * entry.getValue());
            } else {
                log.debug("EventId={} has no rating from user", viewedEventId);
            }
        }

        double score = (sumSimilarityScores > 0) ? (sumWeightedRatings / sumSimilarityScores) : 0;
        log.info("Final recommendation score: {}", score);
        return score;
    }

    private void collectViewedSimilarities(List<EventSimilarity> similarities,
                                           boolean isEventB,
                                           Long userId,
                                           Map<Long, Double> result) {
        log.debug("Collecting similarities (isEventB={}) for userId={}", isEventB, userId);
        for (EventSimilarity eventSimilarity : similarities) {
            Long relatedEventId = isEventB ? eventSimilarity.getEventB() : eventSimilarity.getEventA();
            if (userActionService.hasUserInteractedWithEvent(userId, relatedEventId)) {
                result.put(relatedEventId, eventSimilarity.getScore());
                log.debug("Related eventId={} added with score={}", relatedEventId, eventSimilarity.getScore());
            } else {
                log.debug("User has not interacted with eventId={}", relatedEventId);
            }
        }
    }
}