package ru.practicum.compilation.service.user;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.service.external.EventServiceImpl;
import ru.practicum.compilation.service.util.CompilationDtoMapperUtil;
import ru.practicum.compilation.service.util.CompilationQueryService;
import ru.practicum.interaction.compilation.dto.CompilationDto;
import ru.practicum.interaction.compilation.dto.CompilationParams;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.compilation.service.util.PaginationHelper.calculatePageable;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCompilationServiceImpl implements UserCompilationService {

    private final CompilationMapper compilationMapper;
    private final CompilationDtoMapperUtil compilationDtoMapperUtil;
    private final CompilationQueryService compilationQueryService;
    private final EventServiceImpl eventService;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(CompilationParams params) {

        Pageable pageable = calculatePageable(params.getFrom(), params.getSize().intValue());

        Page<Compilation> page = compilationQueryService.getPageByPinned(params.getPinned(), pageable);

        log.info("Fetched compilations: from={}, size={}, pinned={}",
                params.getFrom(), params.getSize(), params.getPinned());

        return page.stream()
                .map(compilationDtoMapperUtil::convertToDtoWithEvents)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationQueryService.findCompilationByIdOrThrow(compId);
        log.info("Get compilation with id={}", compId);
        return compilationMapper.toCompilationDto(
                compilation, eventService.getEventShortDtoList(compilation.getEventsIds()
                ));
    }
}
