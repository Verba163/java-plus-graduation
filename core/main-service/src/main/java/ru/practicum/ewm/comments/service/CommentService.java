package ru.practicum.ewm.comments.service;

import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.dto.UpdateCommentAdminDto;
import ru.practicum.ewm.comments.dto.parameters.GetCommentsForAdminParameters;
import ru.practicum.ewm.comments.dto.parameters.GetCommentsParameters;
import ru.practicum.ewm.comments.dto.parameters.UpdateCommentParameters;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, NewCommentDto newCommentDto);

    List<CommentDto> getComments(GetCommentsParameters parameters);

    CommentDto getComment(Long commentId, Long userId);

    CommentDto updateComment(UpdateCommentParameters parameters);

    void deleteComment(Long commentId, Long userId);

    List<CommentDto> getCommentsForAdmin(GetCommentsForAdminParameters parameters);

    CommentDto updateCommentByAdmin(long commentId, UpdateCommentAdminDto updateCommentAdminDto);
}
