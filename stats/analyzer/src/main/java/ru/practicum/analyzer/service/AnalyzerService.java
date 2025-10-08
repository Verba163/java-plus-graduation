package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.service.params.EventSimilarityService;
import ru.practicum.analyzer.service.params.RecommendationScoringService;
import ru.practicum.analyzer.service.params.UserActionService;
import ru.practicum.grpc.stats.recommendation.InteractionsCountRequestProto;
import ru.practicum.grpc.stats.recommendation.RecommendedEventProto;
import ru.practicum.grpc.stats.recommendation.SimilarEventsRequestProto;
import ru.practicum.grpc.stats.recommendation.UserPredictionsRequestProto;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzerService {

    private final UserActionService userActionService;
    private final EventSimilarityService similarityService;
    private final RecommendationScoringService scoringService;

    public List<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request) {
        Long userId = request.getUserId();
        int limit = request.getMaxResults();

        Set<Long> recentlyViewedEventIds = userActionService.getRecentlyViewedEventIds(userId, limit);
        if (recentlyViewedEventIds.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> candidateEventIds = findCandidateRecommendations(userId, recentlyViewedEventIds, limit);

        return generateRecommendations(candidateEventIds, userId, limit);
    }

    private Set<Long> findCandidateRecommendations(Long userId, Set<Long> viewedEventIds, int limit) {
        List<EventSimilarity> similaritiesA = similarityService.findSimilarByEventAIn(viewedEventIds, limit);
        List<EventSimilarity> similaritiesB = similarityService.findSimilarByEventBIn(viewedEventIds, limit);
        Set<Long> recommendations = new HashSet<>();

        addNewEventsFromSimilarities(similaritiesA, true, userId, recommendations);
        addNewEventsFromSimilarities(similaritiesB, false, userId, recommendations);

        return recommendations;
    }

    private void addNewEventsFromSimilarities(List<EventSimilarity> similarities,
                                              boolean isEventB,
                                              Long userId,
                                              Set<Long> result) {
        for (EventSimilarity es : similarities) {
            Long candidateId = isEventB ? es.getEventB() : es.getEventA();
            if (!userActionService.hasUserInteractedWithEvent(userId, candidateId)) {
                result.add(candidateId);
            }
        }
    }

    private List<RecommendedEventProto> generateRecommendations(Set<Long> candidateEventIds,
                                                                Long userId,
                                                                int limit) {
        Map<Long, Double> eventScores = candidateEventIds.stream()
                .collect(Collectors.toMap(
                        eventId -> eventId,
                        eventId -> scoringService.calculateRecommendationScore(eventId, userId, limit)
                ));

        return eventScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build())
                .toList();
    }

    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        Long eventId = request.getEventId();
        Long userId = request.getUserId();
        int limit = request.getMaxResults();

        List<EventSimilarity> similaritiesA = similarityService.findSimilarByEventA(eventId, limit);
        List<EventSimilarity> similaritiesB = similarityService.findSimilarByEventB(eventId, limit);

        List<RecommendedEventProto> recommendations = new ArrayList<>();
        similarityService.filterAndAddRecommendations(recommendations, similaritiesA, true, userId);
        similarityService.filterAndAddRecommendations(recommendations, similaritiesB, false, userId);

        recommendations.sort(Comparator.comparing(RecommendedEventProto::getScore).reversed());

        return recommendations.size() > limit ? recommendations.subList(0, limit) : recommendations;
    }

    public List<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request) {
        Set<Long> eventIds = new HashSet<>(request.getEventIdList());
        Map<Long, Double> eventScores = userActionService.computeEventScores(eventIds);

        return eventScores.entrySet().stream()
                .map(entry -> RecommendedEventProto.newBuilder()
                        .setEventId(entry.getKey())
                        .setScore(entry.getValue())
                        .build())
                .toList();
    }
}