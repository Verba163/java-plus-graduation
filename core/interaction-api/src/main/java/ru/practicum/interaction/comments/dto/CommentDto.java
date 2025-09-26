package ru.practicum.interaction.comments.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.interaction.comments.enums.CommentStatus;

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
