package ru.practicum.interaction.comments.dto.parameters;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.interaction.comments.dto.UpdateCommentDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCommentParameters {

    Long userId;
    Long commentId;
    UpdateCommentDto updateCommentDto;
}
