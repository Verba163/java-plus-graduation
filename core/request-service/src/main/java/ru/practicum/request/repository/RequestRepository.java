package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.interaction.request.enums.RequestStatus;
import ru.practicum.request.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(Long userId);

    List<Request> findByEventId(Long eventId);

    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    Integer countByEventIdAndStatus(Long eventId, RequestStatus status);

    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findByEventIdInAndStatus(List<Long> eventIds, RequestStatus status);
}
