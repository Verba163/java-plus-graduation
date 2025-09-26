package ru.practicum.comments.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.service.validation.CommentValidationService;
import ru.practicum.comments.storage.CommentRepository;
import ru.practicum.interaction.comments.dto.CommentDto;
import ru.practicum.interaction.comments.dto.UpdateCommentAdminDto;
import ru.practicum.interaction.comments.dto.parameters.GetCommentsForAdminParameters;
import ru.practicum.interaction.comments.enums.AdminAction;
import ru.practicum.interaction.comments.enums.CommentStatus;
import ru.practicum.interaction.error.exception.NotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentValidationService validationService;

    @Override
    public List<CommentDto> getCommentsForAdmin(GetCommentsForAdminParameters parameters) {
        CommentStatus status = parameters.getStatus();
        Pageable pageable = PageRequest.of(parameters.getFrom() / parameters.getSize(), parameters.getSize());
        return commentRepository.findPageableCommentsForAdmin(status, pageable).stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto updateCommentByAdmin(long commentId, UpdateCommentAdminDto updateCommentAdminDto) {
        Comment comment = getCommentWithCheck(commentId);
        validationService.validatePendingStatus(comment);

        AdminAction action = updateCommentAdminDto.getAction();
        comment.setStatus(action == AdminAction.APPROVE ? CommentStatus.APPROVE : CommentStatus.REJECT);

        Comment saved = commentRepository.save(comment);
        log.info("Admin updated comment id={} with action={}", commentId, action);
        return commentMapper.toCommentDto(saved);
    }

    private Comment getCommentWithCheck(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Comment id=%d not found.", commentId)
                ));
    }
}