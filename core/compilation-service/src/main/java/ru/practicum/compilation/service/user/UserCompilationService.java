package ru.practicum.compilation.service.user;


import ru.practicum.interaction.compilation.dto.CompilationDto;
import ru.practicum.interaction.compilation.dto.CompilationParams;

import java.util.List;

public interface UserCompilationService {

    List<CompilationDto> getCompilations(CompilationParams compilationParams);

    CompilationDto getCompilationById(Long compId);
}
