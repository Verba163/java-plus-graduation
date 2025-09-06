package ru.practicum.ewm.comments.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.dto.UpdateCommentAdminDto;
import ru.practicum.ewm.comments.dto.UpdateCommentDto;
import ru.practicum.ewm.comments.dto.parameters.GetCommentsForAdminParameters;
import ru.practicum.ewm.comments.dto.parameters.GetCommentsParameters;
import ru.practicum.ewm.comments.dto.parameters.UpdateCommentParameters;
import ru.practicum.ewm.comments.mapper.CommentMapper;
import ru.practicum.ewm.comments.model.AdminAction;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.model.CommentStatus;
import ru.practicum.ewm.comments.model.QComment;
import ru.practicum.ewm.comments.storage.CommentRepository;
import ru.practicum.ewm.error.exception.ConflictException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.exception.ValidationException;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.storage.EventsRepository;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.util.Util;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;
    private final RequestRepository requestRepository;

    @Override
    @Transactional
    public CommentDto createComment(Long userId, NewCommentDto newCommentDto) {
        User user = getUserWithCheck(userId);
        Event event = getEventWithCheck(newCommentDto.getEventId());
        Request request = requestRepository.findByRequesterIdAndEventId(userId, newCommentDto.getEventId());

        if (request == null || !request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ValidationException("You cannot leave a comment because " +
                    "you did not leave a request to participate or your request was rejected.");
        }

        if (commentRepository.existsByAuthorIdAndEventId(userId, newCommentDto.getEventId())) {
            throw new ConflictException("You can leave a comment only once.");
        }

        Comment comment = CommentMapper.fromNewCommentDto(newCommentDto);
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreatedOn(Util.getNowTruncatedToSeconds());

        log.info("Created comment for userId={}, eventId={}", userId, newCommentDto.getEventId());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getComments(GetCommentsParameters parameters) {
        QComment comment = QComment.comment;
        getUserWithCheck(parameters.getUserId());
        List<BooleanExpression> conditions = new ArrayList<>();
        Pageable page = createPageableObject(parameters.getFrom(), parameters.getSize());

        conditions.add(comment.author.id.eq(parameters.getUserId()));

        if (parameters.getEventIds() != null && !parameters.getEventIds().isEmpty()) {
            conditions.add(comment.event.id.in(parameters.getEventIds()));
        }

        if (parameters.getStatus() != null) {
            conditions.add(comment.status.eq(parameters.getStatus()));
        }

        BooleanExpression condition = conditions.stream()
                .reduce(Expressions.asBoolean(true).isTrue(), BooleanExpression::and);

        return commentRepository.findAll(condition, page)
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    @Override
    public CommentDto getComment(Long commentId, Long userId) {
        Comment comment = getCommentWithCheck(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationException("Only author can see comment.");
        }
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(UpdateCommentParameters parameters) {
        Comment comment = getCommentWithCheck(parameters.getCommentId());
        if (!comment.getAuthor().getId().equals(parameters.getUserId())) {
            throw new ValidationException("Only author can update comment.");
        }

        if (comment.getStatus() == CommentStatus.PENDING) {
            throw new ValidationException("Cannot edit comment while it is pending moderation.");
        }

        UpdateCommentDto updateDto = parameters.getUpdateCommentDto();
        comment.setText(updateDto.getText());
        comment.setStatus(CommentStatus.PENDING);

        log.info("Updated comment id={} for userId={}", parameters.getCommentId(), parameters.getUserId());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getCommentWithCheck(commentId);
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationException("Only author can delete his comment.");
        }
        commentRepository.delete(comment);
        log.info("Deleted comment id={} for userId={}", commentId, userId);
    }

    @Override
    public List<CommentDto> getCommentsForAdmin(GetCommentsForAdminParameters parameters) {
        CommentStatus status = parameters.getStatus();
        Pageable pageable = createPageableObject(parameters.getFrom(), parameters.getSize());
        return commentRepository.findPageableCommentsForAdmin(status, pageable).stream()
                .map(CommentMapper::toCommentDto)
                .toList();
    }

    @Override
    public CommentDto updateCommentByAdmin(long commentId, UpdateCommentAdminDto updateCommentAdminDto) {
        Comment comment = getCommentWithCheck(commentId);
        AdminAction action = updateCommentAdminDto.getAction();

        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("Comment must has status PENDING.");
        }

        comment.setStatus(action == AdminAction.APPROVE ? CommentStatus.APPROVE : CommentStatus.REJECT);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private User getUserWithCheck(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found.", userId)));
    }

    private Event getEventWithCheck(Long eventId) {
        return eventsRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event id=%d not found.", eventId)));
    }

    private Comment getCommentWithCheck(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment id=%d not found.", commentId)));
    }

    private Pageable createPageableObject(Integer from, Integer size) {
        return PageRequest.of(from / size, size);
    }
}
