package ru.practicum.comments.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.comments.model.Comment;
import ru.practicum.interaction.comments.dto.CommentDto;
import ru.practicum.interaction.comments.dto.CommentShortDto;
import ru.practicum.interaction.comments.dto.NewCommentDto;


@Component
public class CommentMapper {


    public Comment fromNewCommentDto(NewCommentDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .eventId(comment.getEventId())
                .authorId(comment.getAuthorId())
                .text(comment.getText())
                .status(comment.getStatus())
                .createdOn(comment.getCreatedOn())
                .build();
    }

    public CommentShortDto toCommentShortDto(Comment comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(comment.getAuthorName())
                .build();
    }
}
