package ru.practicum.analyzer.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.model.UserAction;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {

    List<UserAction> findAllByUserId(Long userId, PageRequest pageRequest);

    List<UserAction> findAllByEventIdInAndUserId(Set<Long> viewedEvents, Long userId);

    List<UserAction> findAllByEventIdIn(Set<Long> eventIds);

    Optional<UserAction> findByUserIdAndEventId(Long userId, Long eventId);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);
}