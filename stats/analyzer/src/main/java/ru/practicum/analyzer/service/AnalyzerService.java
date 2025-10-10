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

        log.info("Fetching recently viewed events for userId={} with limit={}", userId, limit);
        Set<Long> recentlyViewedEventIds = userActionService.getRecentlyViewedEventIds(userId, limit);
        if (recentlyViewedEventIds.isEmpty()) {
            log.info("No recently viewed events found for userId={}. Returning empty recommendations.", userId);
            return Collections.emptyList();
        }

        log.info("Found {} recently viewed events for userId={}", recentlyViewedEventIds.size(), userId);
        Set<Long> candidateEventIds = findCandidateRecommendations(userId, recentlyViewedEventIds, limit);
        log.info("Identified {} candidate recommendations for userId={}", candidateEventIds.size(), userId);

        List<RecommendedEventProto> recommendations = generateRecommendations(candidateEventIds, userId, limit);
        log.info("Generated {} recommendations for userId={}", recommendations.size(), userId);

        return recommendations;
    }

    private Set<Long> findCandidateRecommendations(Long userId, Set<Long> viewedEventIds, int limit) {
        log.debug("Finding similar events for viewed events: {} for userId={}", viewedEventIds, userId);
        List<EventSimilarity> similaritiesA = similarityService.findSimilarByEventAIn(viewedEventIds, limit);
        List<EventSimilarity> similaritiesB = similarityService.findSimilarByEventBIn(viewedEventIds, limit);
        Set<Long> recommendations = new HashSet<>();

        log.debug("Processing similarities where event is A");
        addNewEventsFromSimilarities(similaritiesA, true, userId, recommendations);

        log.debug("Processing similarities where event is B");
        addNewEventsFromSimilarities(similaritiesB, false, userId, recommendations);

        log.info("Candidate recommendations for userId={} after filtering: {}", userId, recommendations);
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
                log.debug("Adding eventId={} as candidate for userId={}", candidateId, userId);
            } else {
                log.debug("UserId={} has already interacted with eventId={}; skipping", userId, candidateId);
            }
        }
    }

    private List<RecommendedEventProto> generateRecommendations(Set<Long> candidateEventIds,
                                                                Long userId,
                                                                int limit) {
        log.info("Calculating recommendation scores for {} candidate events for userId={}", candidateEventIds.size(), userId);
        Map<Long, Double> eventScores = candidateEventIds.stream()
                .collect(Collectors.toMap(
                        eventId -> eventId,
                        eventId -> {
                            double score = scoringService.calculateRecommendationScore(eventId, userId, limit);
                            log.debug("Score for eventId={} and userId={} is {}", eventId, userId, score);
                            return score;
                        }
                ));

        return eventScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    log.info("Recommendation: eventId={} with score={}", entry.getKey(), entry.getValue());
                    return RecommendedEventProto.newBuilder()
                            .setEventId(entry.getKey())
                            .setScore(entry.getValue())
                            .build();
                }).toList();
    }

    public List<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request) {
        Long eventId = request.getEventId();
        Long userId = request.getUserId();
        int limit = request.getMaxResults();

        log.info("Fetching similar events for eventId={} and userId={}", eventId, userId);
        List<EventSimilarity> similaritiesA = similarityService.findSimilarByEventA(eventId, limit);
        List<EventSimilarity> similaritiesB = similarityService.findSimilarByEventB(eventId, limit);

        List<RecommendedEventProto> recommendations = new ArrayList<>();
        similarityService.filterAndAddRecommendations(recommendations, similaritiesA, true, userId);
        similarityService.filterAndAddRecommendations(recommendations, similaritiesB, false, userId);

        recommendations.sort(Comparator.comparing(RecommendedEventProto::getScore).reversed());

        log.info("Returning {} similar events for eventId={}", recommendations.size(), eventId);
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