package ru.practicum.ewm.compilation.mapper;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.model.Event;

import java.util.List;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventShortDtos) {

        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.isPinned())
                .events(eventShortDtos)
                .build();
    }

    public static Compilation toCompilation(CompilationDto compilationDto, List<Event> events) {

        return Compilation.builder()
                .id(compilationDto.getId())
                .title(compilationDto.getTitle())
                .pinned(compilationDto.isPinned())
                .events(events)
                .build();
    }

    public static Compilation toCompilationEntity(NewCompilationDto newCompilationDto, List<Event> events) {

        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .events(events)
                .build();
    }
}