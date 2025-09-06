package ru.practicum.ewm.comments.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.dto.UpdateCommentAdminDto;
import ru.practicum.ewm.comments.dto.UpdateCommentDto;
import ru.practicum.ewm.comments.dto.parameters.GetCommentsForAdminParameters;
import ru.practicum.ewm.comments.dto.parameters.GetCommentsParameters;
import ru.practicum.ewm.comments.dto.parameters.UpdateCommentParameters;
import ru.practicum.ewm.comments.service.CommentService;

import java.util.List;

import static ru.practicum.ewm.comments.constants.CommentConstants.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping(PRIVATE_API_PREFIX)
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable(USER_ID) Long userId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Request: create new comment from user id={}, newCommentDto={}", userId, newCommentDto);
        return commentService.createComment(userId, newCommentDto);
    }

    @GetMapping(PRIVATE_API_PREFIX)
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getComments(@PathVariable(USER_ID) Long userId,
                                        @Valid @ModelAttribute GetCommentsParameters parameters) {

        parameters.setUserId(userId);

        log.info("Request: get comments of user id={}. Parameters={}", userId, parameters);
        return commentService.getComments(parameters);

    }

    @GetMapping(PRIVATE_API_PREFIX_COMMENT_ID)
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getComment(@PathVariable(USER_ID) Long userId, @PathVariable(COMMENT_ID) Long commentId) {
        log.info("Request: get comment id={} of user id={}.", commentId, userId);
        return commentService.getComment(commentId, userId);
    }

    @PatchMapping(PRIVATE_API_PREFIX_COMMENT_ID)
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable(USER_ID) Long userId,
                                    @PathVariable(COMMENT_ID) Long commentId,
                                    @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        log.info("Request: update comment id={} of user id={}. Data={}.", commentId, userId, updateCommentDto);

        UpdateCommentParameters parameters = UpdateCommentParameters.builder()
                .userId(userId)
                .commentId(commentId)
                .updateCommentDto(updateCommentDto)
                .build();

        return commentService.updateComment(parameters);
    }

    @DeleteMapping(PRIVATE_API_PREFIX_COMMENT_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(USER_ID) Long userId, @PathVariable(COMMENT_ID) Long commentId) {
        log.info("Request: delete comment id={} of user id={}.", commentId, userId);
        commentService.deleteComment(commentId, userId);
    }

    @GetMapping(ADMIN_API_PREFIX)
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsForAdmin(@Valid @ModelAttribute GetCommentsForAdminParameters parameters) {
        log.info("Request: get comments for admin. Parameters={}", parameters);
        return commentService.getCommentsForAdmin(parameters);
    }

    @PatchMapping(ADMIN_API_PREFIX_COMMENT_ID)
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateCommentByAdmin(@PathVariable(COMMENT_ID) Long commentId,
                                           @Valid @RequestBody UpdateCommentAdminDto updateCommentAdminDto) {
        log.info("Request: update comment id={} by admin. Data={}.", commentId, updateCommentAdminDto);
        return commentService.updateCommentByAdmin(commentId, updateCommentAdminDto);
    }
}
