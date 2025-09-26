package ru.practicum.comments.service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.storage.CommentRepository;
import ru.practicum.interaction.comments.enums.CommentStatus;
import ru.practicum.interaction.error.exception.ConflictException;
import ru.practicum.interaction.error.exception.ValidationException;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.request.dto.ParticipationRequestDto;
import ru.practicum.interaction.request.enums.RequestStatus;
import ru.practicum.interaction.user.dto.UserDto;

@Service
@RequiredArgsConstructor
public class CommentValidationService {

    public void validateUserCanComment(UserDto user, EventFullDto event, ParticipationRequestDto request) {
        if (request == null
                || !RequestStatus.CONFIRMED.toString().equals(request.getStatus())) {
            throw new ValidationException("You cannot leave a comment because " +
                    "you did not leave a request to participate or your request was rejected.");
        }
    }

    public void validateCommentNotExists(CommentRepository commentRepository, Long userId, Long eventId) {
        if (commentRepository.existsByAuthorIdAndEventId(userId, eventId)) {
            throw new ConflictException("You can leave a comment only once.");
        }
    }

    public void validateAuthorAccess(Comment comment, Long userId) {
        if (!comment.getAuthorId().equals(userId)) {
            throw new ValidationException("Only author can access this comment.");
        }
    }

    public void validateCanUpdate(Comment comment) {
        if (comment.getStatus() == CommentStatus.PENDING) {
            throw new ValidationException("Cannot edit comment while it is pending moderation.");
        }
    }

    public void validatePendingStatus(Comment comment) {
        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("Comment must has status PENDING.");
        }
    }

    public void validateAuthorDelete(Comment comment, Long userId) {
        if (!comment.getAuthorId().equals(userId)) {
            throw new ValidationException("Only author can delete his comment.");
        }
    }
}