package ru.practicum.ewm.comments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCommentDto {

    @NotNull
    Long eventId;

    @NotBlank
    @Size(min = 5, max = 255, message = "Comment can not be less then 5 and more then 255 symbols")
    String text;
}
