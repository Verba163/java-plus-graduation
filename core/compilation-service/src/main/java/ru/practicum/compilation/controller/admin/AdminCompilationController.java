package ru.practicum.compilation.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.service.admin.AdminCompilationService;
import ru.practicum.interaction.compilation.dto.CompilationDto;
import ru.practicum.interaction.compilation.dto.CompilationParams;
import ru.practicum.interaction.compilation.dto.NewCompilationDto;

import static ru.practicum.interaction.compilation.constants.CompilationApiPath.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminCompilationController {

    private final AdminCompilationService adminCompilationService;


    @PostMapping(ADMIN_API_PREFIX)
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createdCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Request: post compilation={}", newCompilationDto);
        return adminCompilationService.createdCompilation(newCompilationDto);
    }

    @DeleteMapping(ADMIN_API_PREFIX + COMP_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(COMP_ID) Long compId) {
        log.info("Request: delete compilation with id={}", compId);
        adminCompilationService.deleteCompilation(compId);
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

        return adminCompilationService.updateCompilation(compilationParams);
    }
}
