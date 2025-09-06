package ru.practicum.ewm.events.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.events.model.Event;

import java.util.List;

public interface EventsRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Page<Event> findAllByInitiatorIdIs(Long userId, Pageable pageable);

    long countByCategoryId(Long categoryId);

    @Query(nativeQuery = true, value = """
       SELECT e.id
       FROM events e
       LEFT JOIN requests r ON r.event_id = e.id
       GROUP BY e.id
       HAVING e.participant_limit = 0
            OR SUM(CASE WHEN r.id IS NOT NULL AND r.status = 'CONFIRMED' THEN 1 ELSE 0 END) < e.participant_limit
       """)
    List<Long> getAvailableEventIdsByParticipantLimit();

    @Query(nativeQuery = true, value = """
       SELECT e.id, SUM(CASE WHEN r.id IS NOT NULL AND r.status = 'CONFIRMED' THEN 1 ELSE 0 END)
       FROM events e
       LEFT JOIN requests r ON r.event_id = e.id
       WHERE e.id IN (?1)
       GROUP BY e.id
       """)
    List<List<Long>> getConfirmedRequestsForEvents(List<Long> eventIds);
}
