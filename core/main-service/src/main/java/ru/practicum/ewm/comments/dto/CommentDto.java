package ru.practicum.ewm.comments.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.comments.model.CommentStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {
    Long id;
    Long eventId;
    Long authorId;
    String text;
    CommentStatus status;
    LocalDateTime createdOn;
}
