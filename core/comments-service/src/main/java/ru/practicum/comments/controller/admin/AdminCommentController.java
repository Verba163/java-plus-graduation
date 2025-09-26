package ru.practicum.comments.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.service.admin.AdminCommentService;
import ru.practicum.interaction.comments.dto.CommentDto;
import ru.practicum.interaction.comments.dto.UpdateCommentAdminDto;
import ru.practicum.interaction.comments.dto.parameters.GetCommentsForAdminParameters;

import java.util.List;

import static ru.practicum.interaction.comments.constants.CommentApiPath.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    @GetMapping(ADMIN_API_PREFIX)
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsForAdmin(@Valid @ModelAttribute GetCommentsForAdminParameters parameters) {
        log.info("Request: get comments for admin. Parameters={}", parameters);
        return adminCommentService.getCommentsForAdmin(parameters);
    }

    @PatchMapping(ADMIN_API_PREFIX_COMMENT_ID)
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateCommentByAdmin(@PathVariable(COMMENT_ID) Long commentId,
                                           @Valid @RequestBody UpdateCommentAdminDto updateCommentAdminDto) {
        log.info("Request: update comment id={} by admin. Data={}.", commentId, updateCommentAdminDto);
        return adminCommentService.updateCommentByAdmin(commentId, updateCommentAdminDto);
    }
}
