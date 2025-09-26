package ru.practicum.compilation.service.admin;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.service.external.EventService;
import ru.practicum.compilation.service.util.CompilationDtoMapperUtil;
import ru.practicum.compilation.service.util.CompilationQueryService;
import ru.practicum.compilation.service.util.CompilationUpdater;
import ru.practicum.compilation.service.validation.CompilationValidator;
import ru.practicum.compilation.storage.CompilationRepository;
import ru.practicum.interaction.compilation.dto.CompilationDto;
import ru.practicum.interaction.compilation.dto.CompilationParams;
import ru.practicum.interaction.compilation.dto.NewCompilationDto;
import ru.practicum.interaction.error.exception.NotFoundException;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventService eventService;
    private final CompilationUpdater compilationUpdater;
    private final CompilationDtoMapperUtil compilationDtoMapperUtil;
    private final CompilationQueryService compilationQueryService;
    private final CompilationValidator compilationValidator;

    @Override
    @Transactional
    public CompilationDto createdCompilation(NewCompilationDto dto) {

        compilationValidator.validateNewCompilationDto(dto);

        if (dto.getEvents() == null) {
            dto.setEvents(new ArrayList<>());
        }

        if (!eventService.checkEventsExist(dto.getEvents())) {
            throw new NotFoundException("Not all received events were found");
        }

        Compilation compilation = compilationMapper.toCompilation(dto);
        Compilation savedCompilation = compilationRepository.save(compilation);

        log.info("Created compilation id={} title='{}'", savedCompilation.getId(), savedCompilation.getTitle());
        return compilationMapper
                .toCompilationDto(savedCompilation, eventService.getEventShortDtoList(savedCompilation.getEventsIds()));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationQueryService.findCompilationByIdOrThrow(compId);
        compilationRepository.deleteById(compId);
        log.info("Deleted compilation id={}", compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(CompilationParams params) {

        NewCompilationDto dto = params.getNewCompilationDto();
        Compilation compilation = compilationQueryService.findCompilationByIdOrThrow(params.getCompId());

        compilationUpdater.updateCompilationFields(compilation, dto);

        Compilation updated = compilationRepository.save(compilation);
        log.info("Updated compilation id={} title='{}'", updated.getId(), updated.getTitle());
        return compilationDtoMapperUtil.convertToDtoWithEvents(updated);
    }
}
