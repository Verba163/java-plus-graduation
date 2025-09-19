package ru.practicum.ewm.comments.mapper;

import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.CommentShortDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.model.Comment;


public class CommentMapper {


    public static Comment fromNewCommentDto(NewCommentDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .eventId(comment.getEvent().getId())
                .authorId(comment.getAuthor().getId())
                .text(comment.getText())
                .status(comment.getStatus())
                .createdOn(comment.getCreatedOn())
                .build();
    }

    public static CommentShortDto toCommentShortDto(Comment comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(comment.getAuthor().getName())
                .build();
    }
}
