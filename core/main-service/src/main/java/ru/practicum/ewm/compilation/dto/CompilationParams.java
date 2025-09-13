package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationParams {

    Boolean pinned;

    @Min(value = 0, message = "'from' parameter must be >= 0")
    Long from;

    @Positive(message = "'size' parameter must be > 0")
    Long size;

    Long compId;

    NewCompilationDto newCompilationDto;
}
