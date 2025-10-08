package ru.practicum.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class AggregationService {

    private final Map<Long, Map<Long, Double>> eventActions = new HashMap<>();
    private final Map<Long, Double> eventWeightSums = new HashMap<>();
    private final Map<Long, Map<Long, Double>> eventMinWeightSums = new HashMap<>();

    public List<EventSimilarityAvro> updateSimilarity(UserActionAvro avro) {
        final long userId = avro.getUserId();
        final long eventId = avro.getEventId();
        final double newWeight = toWeight(avro.getActionType());

        final Map<Long, Double> userActions = eventActions.computeIfAbsent(eventId, k -> new HashMap<>());
        log.info("User actions for userId={} on eventId={}: {}", userId, eventId, userActions);

        final double oldWeight = userActions.getOrDefault(userId, 0.0);
        if (Double.compare(newWeight, oldWeight) <= 0) {
            log.info("Weight not increased for eventId={}, skipping update", eventId);
            return Collections.emptyList();
        }

        updateUserAction(userId, eventId, oldWeight, newWeight, userActions);

        List<EventSimilarityAvro> similarities = new ArrayList<>();
        eventActions.forEach((anotherEventId, weightsMap) -> {
            if (eventId == anotherEventId) return;

            final double anotherWeight = weightsMap.getOrDefault(userId, 0.0);
            if (anotherWeight > 0) {
                final double newMinSum = updateMinWeightSums(userId, eventId, anotherEventId, oldWeight, newWeight);
                final double similarity = calculateSimilarity(eventId, anotherEventId, newMinSum);
                similarities.add(createAvro(eventId, anotherEventId, similarity, avro.getTimestamp()));
            }
        });

        return similarities;
    }

    private Double toWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };
    }

    private void updateUserAction(long userId, long eventId, double oldWeight, double newWeight,
                                  Map<Long, Double> userActions) {
        final double oldSum = eventWeightSums.getOrDefault(eventId, 0.0);
        final double newSum = oldSum - oldWeight + newWeight;

        userActions.put(userId, newWeight);
        eventWeightSums.put(eventId, newSum);
        log.info("Updated weight sum for eventId={} to {}", eventId, newSum);
    }

    private double updateMinWeightSums(long userId, long eventId, long anotherEventId,
                                       double oldWeight, double newWeight) {
        log.info("Updating minimum weights sums for events {} and {}", eventId, anotherEventId);
        final long eventA = Math.min(eventId, anotherEventId);
        final long eventB = Math.max(eventId, anotherEventId);

        final double anotherWeight = eventActions
                .getOrDefault(anotherEventId, Collections.emptyMap())
                .getOrDefault(userId, 0.0);

        if (Double.compare(anotherWeight, 0.0) == 0)
            return 0.0;

        final Map<Long, Double> minWeights = eventMinWeightSums.computeIfAbsent(eventA, k -> new HashMap<>());
        final double oldSum = minWeights.getOrDefault(eventB, 0.0);

        final double oldMin = Math.min(oldWeight, anotherWeight);
        final double newMin = Math.min(newWeight, anotherWeight);

        final double newSum = oldSum - oldMin + newMin;
        minWeights.put(eventB, newSum);

        return newSum;
    }

    private double calculateSimilarity(long eventId, long anotherEventId, double newMinSum) {
        log.info("Calculating similarity for events {} and {}", eventId, anotherEventId);
        final double sumEvent = eventWeightSums.getOrDefault(eventId, 0.0);
        final double sumAnotherEvent = eventWeightSums.getOrDefault(anotherEventId, 0.0);

        if (sumEvent <= 0 || sumAnotherEvent <= 0) {
            return 0.0;
        }

        return newMinSum / Math.sqrt(sumEvent * sumAnotherEvent);
    }

    private EventSimilarityAvro createAvro(long eventId, long anotherEventId, double similarity, Instant timestamp) {
        final long eventA = Math.min(eventId, anotherEventId);
        final long eventB = Math.max(eventId, anotherEventId);

        return EventSimilarityAvro.newBuilder()
                .setEventA(eventA)
                .setEventB(eventB)
                .setScore(similarity)
                .setTimestamp(timestamp)
                .build();
    }
}