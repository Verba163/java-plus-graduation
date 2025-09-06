package ru.practicum.ewm.compilation.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationParams {

    Boolean pinned;
    Long from;
    Long size;
    Long compId;
    NewCompilationDto newCompilationDto;
}
