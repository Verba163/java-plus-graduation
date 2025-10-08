package ru.practicum.analyzer.service.params;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.repository.EventSimilarityRepository;
import ru.practicum.grpc.stats.recommendation.RecommendedEventProto;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EventSimilarityService {

    private final EventSimilarityRepository similarityRepository;
    private final UserActionService userActionService;

    public List<EventSimilarity> findSimilarByEventA(Long eventId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "score"));
        return similarityRepository.findAllByEventA(eventId, pageRequest);
    }

    public List<EventSimilarity> findSimilarByEventB(Long eventId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "score"));
        return similarityRepository.findAllByEventB(eventId, pageRequest);
    }

    public List<EventSimilarity> findSimilarByEventAIn(Set<Long> eventIds, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "score"));
        return similarityRepository.findAllByEventAIn(eventIds, pageRequest);
    }

    public List<EventSimilarity> findSimilarByEventBIn(Set<Long> eventIds, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "score"));
        return similarityRepository.findAllByEventBIn(eventIds, pageRequest);
    }

    public void filterAndAddRecommendations(List<RecommendedEventProto> recommendations,
                                            List<EventSimilarity> similarities,
                                            boolean isEventB,
                                            Long userId) {
        for (EventSimilarity es : similarities) {
            Long candidateEventId = isEventB ? es.getEventB() : es.getEventA();
            if (!userActionService.hasUserInteractedWithEvent(userId, candidateEventId)) {
                recommendations.add(RecommendedEventProto.newBuilder()
                        .setEventId(candidateEventId)
                        .setScore(es.getScore())
                        .build());
            }
        }
    }
}