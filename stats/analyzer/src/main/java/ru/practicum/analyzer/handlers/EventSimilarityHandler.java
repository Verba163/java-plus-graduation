package ru.practicum.analyzer.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.repository.EventSimilarityRepository;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;


@Service
@RequiredArgsConstructor
public class EventSimilarityHandler {

    private final EventSimilarityRepository eventSimilarityRepository;

    public void handle(EventSimilarityAvro avro) {
        EventSimilarity similarity = EventSimilarity.builder()
                .eventA(avro.getEventA())
                .eventB(avro.getEventB())
                .score(avro.getScore())
                .timestamp(avro.getTimestamp())
                .build();
        eventSimilarityRepository.save(similarity);
    }
}