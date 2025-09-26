package ru.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.interaction.compilation.dto.CompilationDto;
import ru.practicum.interaction.compilation.dto.NewCompilationDto;
import ru.practicum.interaction.events.dto.EventShortDto;

import java.util.List;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CompilationMapper {
    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    @Mapping(target = "events", source = "eventShortDtos")
    CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventShortDtos);

    default Compilation toCompilation(CompilationDto compilationDto, List<Long> events) {
        return Compilation.builder()
                .id(compilationDto.getId())
                .title(compilationDto.getTitle())
                .pinned(compilationDto.isPinned())
                .eventsIds(events)
                .build();
    }

    default Compilation toCompilationEntity(NewCompilationDto newCompilationDto, List<Long> events) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .eventsIds(events)
                .build();
    }


    default Compilation toCompilation(NewCompilationDto newCompilationDto) {

        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .eventsIds(newCompilationDto.getEvents())
                .build();
    }
}