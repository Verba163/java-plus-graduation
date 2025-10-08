package ru.practicum.events.rating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.client.RecommendationsClient;
import ru.practicum.grpc.stats.recommendation.RecommendedEventProto;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventRatingService {

    private final RecommendationsClient recommendationsClient;

    public Map<Long, Double> getRatingMap(List<Long> eventIds) {
        Stream<RecommendedEventProto> eventRating = recommendationsClient.getInteractionsCount(eventIds);

        return eventRating
                .collect(Collectors.toMap(RecommendedEventProto::getEventId, RecommendedEventProto::getScore));
    }
}
