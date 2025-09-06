package ru.practicum.ewm.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationParams;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CompilationController {
    private static final String ADMIN_API_PREFIX = "/admin/compilations";
    private static final String PUBLIC_API_PREFIX = "/compilations";
    private static final String COMP_ID_PATH = "/{comp-id}";
    private static final String COMP_ID = "comp-id";

    private final CompilationService compilationService;

    @GetMapping(PUBLIC_API_PREFIX)
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") Long from,
                                                @RequestParam(defaultValue = "10") Long size) {
        log.info("Request: get compilations with pinned={}, from={}, size={}", pinned, from, size);
        CompilationParams compilationParams = CompilationParams
                .builder()
                .pinned(pinned)
                .from(from)
                .size(size)
                .build();
        return compilationService.getCompilations(compilationParams);
    }

    @GetMapping(PUBLIC_API_PREFIX + COMP_ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@PathVariable(COMP_ID) Long compId) {
        log.info("Request: get compilation with id={}", compId);
        return compilationService.getCompilationById(compId);
    }

    @PostMapping(ADMIN_API_PREFIX)
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createdCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Request: post compilation={}", newCompilationDto);
        return compilationService.createdCompilation(newCompilationDto);
    }

    @DeleteMapping(ADMIN_API_PREFIX + COMP_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(COMP_ID) Long compId) {
        log.info("Request: delete compilation with id={}", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping(ADMIN_API_PREFIX + COMP_ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto,
                                            @PathVariable(COMP_ID) Long compId) {
        log.info("Request: update compilation with id={}, update compilation={}", compId, newCompilationDto);

        CompilationParams compilationParams = CompilationParams
                .builder()
                .compId(compId)
                .newCompilationDto(newCompilationDto)
                .build();

        return compilationService.updateCompilation(compilationParams);
    }

}
