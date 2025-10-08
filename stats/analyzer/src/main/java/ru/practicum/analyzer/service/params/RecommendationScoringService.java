package ru.practicum.analyzer.service.params;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.model.EventSimilarity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RecommendationScoringService {

    private final EventSimilarityService similarityService;
    private final UserActionService userActionService;

    public double calculateRecommendationScore(Long eventId, Long userId, int limit) {

        List<EventSimilarity> similaritiesA = similarityService.findSimilarByEventA(eventId, limit);
        List<EventSimilarity> similaritiesB = similarityService.findSimilarByEventB(eventId, limit);

        Map<Long, Double> similarityScores = new HashMap<>();
        collectViewedSimilarities(similaritiesA, true, userId, similarityScores);
        collectViewedSimilarities(similaritiesB, false, userId, similarityScores);

        Map<Long, Double> userRatings = userActionService.getUserRatingsForEvents(userId, similarityScores.keySet());

        double sumWeightedRatings = 0;
        double sumSimilarityScores = 0;

        for (Map.Entry<Long, Double> entry : similarityScores.entrySet()) {
            Long viewedEventId = entry.getKey();
            Double rating = userRatings.get(viewedEventId);
            if (rating != null) {
                sumWeightedRatings += rating * entry.getValue();
                sumSimilarityScores += entry.getValue();
            }
        }

        return sumSimilarityScores > 0 ? sumWeightedRatings / sumSimilarityScores : 0;
    }

    private void collectViewedSimilarities(List<EventSimilarity> similarities,
                                           boolean isEventB,
                                           Long userId,
                                           Map<Long, Double> result) {
        for (EventSimilarity es : similarities) {
            Long relatedEventId = isEventB ? es.getEventB() : es.getEventA();
            if (userActionService.hasUserInteractedWithEvent(userId, relatedEventId)) {
                result.put(relatedEventId, es.getScore());
            }
        }
    }
}