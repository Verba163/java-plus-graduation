package ru.practicum.comments.service.user;

import ru.practicum.interaction.comments.dto.CommentDto;
import ru.practicum.interaction.comments.dto.NewCommentDto;
import ru.practicum.interaction.comments.dto.parameters.GetCommentsParameters;
import ru.practicum.interaction.comments.dto.parameters.UpdateCommentParameters;

import java.util.List;

public interface UserCommentService {

    CommentDto createComment(Long userId, NewCommentDto newCommentDto);

    List<CommentDto> getComments(GetCommentsParameters parameters);

    CommentDto getComment(Long commentId, Long userId);

    CommentDto updateComment(UpdateCommentParameters parameters);

    void deleteComment(Long commentId, Long userId);

}
