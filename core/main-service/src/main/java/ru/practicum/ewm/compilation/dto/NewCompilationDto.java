package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCompilationDto {

    @Size(max = 50, message = "Title length must not exceed 50 characters")
    String title;

    boolean pinned;

    List<Long> events;
}
