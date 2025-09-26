package ru.practicum.comments.controller.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.service.user.UserCommentService;
import ru.practicum.interaction.comments.dto.CommentDto;
import ru.practicum.interaction.comments.dto.NewCommentDto;
import ru.practicum.interaction.comments.dto.UpdateCommentDto;
import ru.practicum.interaction.comments.dto.parameters.GetCommentsParameters;
import ru.practicum.interaction.comments.dto.parameters.UpdateCommentParameters;

import java.util.List;

import static ru.practicum.interaction.comments.constants.CommentApiPath.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserCommentController {

    private final UserCommentService userCommentService;

    @PostMapping(PRIVATE_API_PREFIX)
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable(USER_ID) Long userId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Request: create new comment from user id={}, newCommentDto={}", userId, newCommentDto);
        return userCommentService.createComment(userId, newCommentDto);
    }

    @GetMapping(PRIVATE_API_PREFIX)
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getComments(@PathVariable(USER_ID) Long userId,
                                        @Valid @ModelAttribute GetCommentsParameters parameters) {

        parameters.setUserId(userId);

        log.info("Request: get comments of user id={}. Parameters={}", userId, parameters);
        return userCommentService.getComments(parameters);

    }

    @GetMapping(PRIVATE_API_PREFIX_COMMENT_ID)
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getComment(@PathVariable(USER_ID) Long userId, @PathVariable(COMMENT_ID) Long commentId) {
        log.info("Request: get comment id={} of user id={}.", commentId, userId);
        return userCommentService.getComment(commentId, userId);
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

        return userCommentService.updateComment(parameters);
    }

    @DeleteMapping(PRIVATE_API_PREFIX_COMMENT_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable(USER_ID) Long userId, @PathVariable(COMMENT_ID) Long commentId) {
        log.info("Request: delete comment id={} of user id={}.", commentId, userId);
        userCommentService.deleteComment(commentId, userId);
    }
}
