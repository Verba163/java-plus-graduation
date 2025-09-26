package ru.practicum.compilation.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.service.user.UserCompilationService;
import ru.practicum.interaction.compilation.dto.CompilationDto;
import ru.practicum.interaction.compilation.dto.CompilationParams;

import java.util.List;

import static ru.practicum.interaction.compilation.constants.CompilationApiPath.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserCompilationController {

    private final UserCompilationService userCompilationService;

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
        return userCompilationService.getCompilations(compilationParams);
    }

    @GetMapping(PUBLIC_API_PREFIX + COMP_ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@PathVariable(COMP_ID) Long compId) {
        log.info("Request: get compilation with id={}", compId);
        return userCompilationService.getCompilationById(compId);
    }
}
