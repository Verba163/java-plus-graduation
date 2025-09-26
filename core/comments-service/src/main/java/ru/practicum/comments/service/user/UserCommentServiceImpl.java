package ru.practicum.comments.service.user;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.model.QComment;
import ru.practicum.comments.service.external.EventService;
import ru.practicum.comments.service.external.ParticipationService;
import ru.practicum.comments.service.external.UserService;
import ru.practicum.comments.service.validation.CommentValidationService;
import ru.practicum.comments.storage.CommentRepository;
import ru.practicum.interaction.comments.dto.CommentDto;
import ru.practicum.interaction.comments.dto.NewCommentDto;
import ru.practicum.interaction.comments.dto.parameters.GetCommentsParameters;
import ru.practicum.interaction.comments.dto.parameters.UpdateCommentParameters;
import ru.practicum.interaction.comments.enums.CommentStatus;
import ru.practicum.interaction.error.exception.NotFoundException;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.request.dto.ParticipationRequestDto;
import ru.practicum.interaction.user.dto.UserDto;
import ru.practicum.interaction.util.Util;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommentServiceImpl implements UserCommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final EventService eventService;
    private final ParticipationService participationService;
    private final CommentValidationService validationService;

    @Override
    @Transactional
    public CommentDto createComment(Long userId, NewCommentDto newCommentDto) {
        UserDto user = userService.getUserById(userId);
        EventFullDto event = eventService.getEventById(newCommentDto.getEventId());
        ParticipationRequestDto request = participationService.getUserRequest(userId, newCommentDto.getEventId());

        validationService.validateUserCanComment(user, event, request);
        validationService.validateCommentNotExists(commentRepository, userId, newCommentDto.getEventId());

        Comment comment = commentMapper.fromNewCommentDto(newCommentDto);
        comment.setAuthorId(user.getId());
        comment.setEventId(event.getId());
        comment.setCreatedOn(Util.getNowTruncatedToSeconds());

        Comment saved = commentRepository.save(comment);
        log.info("Created comment for userId={}, eventId={}", userId, newCommentDto.getEventId());
        return commentMapper.toCommentDto(saved);
    }

    @Override
    public List<CommentDto> getComments(GetCommentsParameters parameters) {
        QComment comment = QComment.comment;
        userService.getUserById(parameters.getUserId()); // проверка пользователя

        List<BooleanExpression> conditions = new ArrayList<>();
        Pageable page = createPageableObject(parameters.getFrom(), parameters.getSize());

        conditions.add(comment.authorId.eq(parameters.getUserId()));

        if (parameters.getEventIds() != null && !parameters.getEventIds().isEmpty()) {
            conditions.add(comment.eventId.in(parameters.getEventIds()));
        }
        if (parameters.getStatus() != null) {
            conditions.add(comment.status.eq(parameters.getStatus()));
        }

        BooleanExpression condition = conditions.stream()
                .reduce(Expressions.asBoolean(true).isTrue(), BooleanExpression::and);

        return commentRepository.findAll(condition, page)
                .map(commentMapper::toCommentDto)
                .toList();
    }

    @Override
    public CommentDto getComment(Long commentId, Long userId) {
        Comment comment = getCommentWithCheck(commentId);
        validationService.validateAuthorAccess(comment, userId);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(UpdateCommentParameters parameters) {
        Comment comment = getCommentWithCheck(parameters.getCommentId());
        validationService.validateAuthorAccess(comment, parameters.getUserId());
        validationService.validateCanUpdate(comment);

        comment.setText(parameters.getUpdateCommentDto().getText());
        comment.setStatus(CommentStatus.PENDING);

        Comment saved = commentRepository.save(comment);
        log.info("Updated comment id={} for userId={}", parameters.getCommentId(), parameters.getUserId());
        return commentMapper.toCommentDto(saved);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = getCommentWithCheck(commentId);
        validationService.validateAuthorDelete(comment, userId);
        commentRepository.delete(comment);
        log.info("Deleted comment id={} for userId={}", commentId, userId);
    }

    private Comment getCommentWithCheck(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Comment id=%d not found.", commentId)
                ));
    }

    private Pageable createPageableObject(Integer from, Integer size) {
        return PageRequest.of(from / size, size);
    }
}
