package ru.practicum.ewm.comments.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.model.CommentStatus;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>,QuerydslPredicateExecutor<Comment> {
    @Query(nativeQuery = true, value = """
       SELECT c.event_id, COUNT(c.id)
       FROM comments c
       WHERE c.event_id IN (?1)
       GROUP BY c.event_id
       """)
    List<List<Long>> getCommentsNumberForEvents(List<Long> eventIds);

    @Query(nativeQuery = true, value = """
       SELECT c.*
       FROM comments c
       WHERE c.event_id=?1
       ORDER BY c.created_on DESC
       LIMIT ?2
       """)
    List<Comment> findFirstCommentsForEvent(Long eventId, Long size);

    @Query(nativeQuery = true, value = """
       SELECT c.*
       FROM comments c
       WHERE c.event_id=?1
       ORDER BY c.created_on DESC
       OFFSET ?2
       LIMIT ?3
       """)
    List<Comment> findPageableCommentsForEvent(Long eventId, Integer offset, Integer size);

    @Query("""
       SELECT c
       FROM Comment c
       WHERE (:status IS NULL OR c.status=:status)
       ORDER BY c.createdOn DESC
       """)
    List<Comment> findPageableCommentsForAdmin(@Param("status") CommentStatus status, Pageable pageable);

    boolean existsByAuthorIdAndEventId(Long userId, Long eventId);
}
