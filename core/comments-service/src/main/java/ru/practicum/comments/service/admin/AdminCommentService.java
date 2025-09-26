package ru.practicum.comments.service.admin;

import ru.practicum.interaction.comments.dto.CommentDto;
import ru.practicum.interaction.comments.dto.UpdateCommentAdminDto;
import ru.practicum.interaction.comments.dto.parameters.GetCommentsForAdminParameters;

import java.util.List;

public interface AdminCommentService {

    List<CommentDto> getCommentsForAdmin(GetCommentsForAdminParameters parameters);

    CommentDto updateCommentByAdmin(long commentId, UpdateCommentAdminDto updateCommentAdminDto);
}