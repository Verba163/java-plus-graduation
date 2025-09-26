package ru.practicum.compilation.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.service.external.EventServiceImpl;
import ru.practicum.interaction.compilation.dto.CompilationDto;
import ru.practicum.interaction.events.dto.EventShortDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CompilationDtoMapperUtil {

    private final EventServiceImpl eventService;
    private final CompilationMapper compilationMapper;

    public CompilationDto convertToDtoWithEvents(Compilation compilation) {
        List<EventShortDto> eventShortDtos = eventService.getEventShortDtoList(compilation.getEventsIds());
        return compilationMapper.toCompilationDto(compilation, eventShortDtos);
    }
}