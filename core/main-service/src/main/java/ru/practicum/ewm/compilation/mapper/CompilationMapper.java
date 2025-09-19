package ru.practicum.ewm.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.model.Event;

import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    @Mapping(target = "events", source = "eventShortDtos")
    CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventShortDtos);

    default Compilation toCompilation(CompilationDto compilationDto, List<Event> events) {
        return Compilation.builder()
                .id(compilationDto.getId())
                .title(compilationDto.getTitle())
                .pinned(compilationDto.isPinned())
                .events(events)
                .build();
    }

    default Compilation toCompilationEntity(NewCompilationDto newCompilationDto, List<Event> events) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .events(events)
                .build();
    }
}