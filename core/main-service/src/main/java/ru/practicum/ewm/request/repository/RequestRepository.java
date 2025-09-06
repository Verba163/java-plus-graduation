package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long userId);

    List<Request> findByEventId(Long eventId);

    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    Request findByRequesterIdAndEventId(Long userId, Long eventId);

    Integer countByEventIdAndStatus(Long eventId, RequestStatus status);
}
